package org.iceslab.frobot.master.manager.project;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.utils.ExtensionLoader;
import org.iceslab.frobot.commons.utils.db.SQLiteOperation;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.cluster.RemoteTask;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;
import org.iceslab.frobot.cluster.WorkerTaskResult;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.request.TaskPushRequestBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.master.workflow.WorkFlow;

/**
 * Manage the project lifecycle, including initialized(apply for resources), execute
 * project, deploy project and so on.
 *
 * @auther Neuclil
 */
public class ProjectManager {
    private static final Logger LOGGER = Logger.getLogger(ProjectManager.class);
    private static volatile ProjectManager projectManager;
    private ConcurrentHashMap<String, ProjectEngine> projectEngines;
    private ExtensionLoader<ProjectEngine> loader;
    private MasterApplication application;
    private Map<String, WorkFlow> workFlowTable;

    private ProjectManager(MasterApplication application) {
        this.application = application;
    }

    public void start() {
        loader = new ExtensionLoader<ProjectEngine>();
        projectEngines = new ConcurrentHashMap<String, ProjectEngine>();
        workFlowTable = new ConcurrentHashMap<>();
    }

    public static ProjectManager getInstance(MasterApplication application) {
        if (projectManager == null) {
            synchronized (ProjectManager.class) {
                if (projectManager == null) {
                    projectManager = new ProjectManager(application);
                }
            }
        }
        return projectManager;
    }

    public Project getWaitingProject() {
        return SQLiteOperation.getFirstWaitingProject(application.getMasterNodeConfig().getRootPath());
    }

    public void processProject(final Project project) {
        /* dynamic load engine */
        ProjectEngine projectEngine = findProjectEngine(project);
        if (projectEngine == null) {
            return;
        }

		/* init project */
        ProjectRuntime projectRuntime = null;
        try {
            projectRuntime = projectEngine.init(project,
                    application.getDefaultTaskEngines().get(project.getProjectSimpleName()));
        } catch (XMLFileNotMatchException e) {
            LOGGER.error("project engine initialize error!"
                    + project.getUserName() + ":" + project.getProjectName(), e);
            return;
        }

        /* require Resources */
        boolean success = requireWorkerResources(projectRuntime);
        if (!success) {
            return;
        }

        /* save taskConfig and move task config file if require resources success */
        Map<TaskInfo, String> taskConfig = projectRuntime.getTaskInfos();
        SQLiteOperation.addTaskInfos(application.getRootPath(),taskConfig);
        TaskUtils.ConfigsBackupToTasks(taskConfig, project);

		/* get workers */
        Map<String, List<WorkerInfo>> workers = getWorkers(project);

        /* get workFlow */
        WorkFlow workFlow = WorkFlowUtils.getWorkFlow(taskConfig);
        workFlowTable.put(project.getProjectId(), workFlow);

        /* projectEngine start */
        try {
            projectEngine.start(workFlow, workers, application);
        } catch (ClassNotFoundException e) {
            LOGGER.error("ProjectManager dynamic load class error.");
        }
        LOGGER.info("ProjectManager process project successfully!");
    }

    private ProjectEngine findProjectEngine(final Project project) {
        //TODO: fix bug about find engine by project simple name.
        ProjectEngine projectEngine = projectEngines.get(project.getUserName() + "_" + project.getProjectSimpleName());
        if (projectEngine == null) {// 需要动态加载用户自己的引擎
            try {
                projectEngine = loadProjectEngine(project);
                LOGGER.debug("dynamic load project engine successfully:" + projectEngine);
            } catch (ClassNotFoundException e) {
                LOGGER.error("dynamic load project engine error:" + e);
                return null;
            }
            projectEngines.put(project.getProjectSimpleName(), projectEngine);//TODO:
        }
        return projectEngine;
    }

    /**
     * @param project
     * @return ProjectEngine SubClass
     * @throws ClassNotFoundException
     */

    private ProjectEngine loadProjectEngine(Project project) throws ClassNotFoundException {
        String projectEngineJarPath = project.getProjectJarPath();
        String projectJarName = project.getProjectJarName();
        String projectEngineClassNameWithPackageName = project.getProjectEngineClassWithPackageName();
        return loader.loadClass(projectEngineJarPath + File.separator + projectJarName,
                projectEngineClassNameWithPackageName, ProjectEngine.class);
    }

    /**
     * 处理worker返回的执行结果
     *
     * @param workerTaskResult
     */
    public boolean processTaskResult(WorkerTaskResult workerTaskResult) {
        String taskID = workerTaskResult.getTaskID();
        int subTaskSequence = workerTaskResult.getSubTaskSequence();
        String outputPath = workerTaskResult.getOutputPath();
        String projectID = TaskUtils.getProjectIDbyTaskID(taskID);
        WorkFlow workFlow = workFlowTable.get(projectID);

        TaskInfo taskInfo = workFlow.getNext(taskID);
        if (taskInfo == null)
            return true;
        taskInfo = TaskDBOperation.getTaskInfoByTaskIDAndSequence(taskInfo.getTaskID(), subTaskSequence);
        if (taskInfo == null) {
            return false;
        }

        WorkerInfo worker = getWorker(taskInfo.getTaskID(), subTaskSequence);
        RemoteTask remoteTask = TaskUtils.getRemoteTaskByTaskInfo(taskInfo);//TODO
        remoteTask.setSubTaskSequence(subTaskSequence);
        remoteTask.setIp(application.getMasterNodeConfig().getIp());
        remoteTask.setPort(application.getMasterNodeConfig().getDataPort());
        remoteTask.setInputDateFilePath(outputPath);
        RemotingCommand request = RemotingCommand.createRequestCommand(
                RemotingProtos.RequestCode.PUSH_TASK.code(),
                new TaskPushRequestBody(remoteTask));

        Thread thread = null;
        String filePath = application.getMasterNodeConfig().getWorkspacePath()
                + remoteTask.getUserName() + File.separator
                + "lib" + File.separator
                + "task_engine" + File.separator
                + taskInfo.getTaskJarName();
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                application.getFileSender().sendFile(filePath);
            }
        });
        thread.start();
        RemotingCommand response = application.getRemotingServer().invokeSync(
                worker.getChannel(), request);

        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (response.getCode() == RemotingProtos.ResponseCode.PUSH_TASK_SUCCESS.code()) {
            LOGGER.debug("Push Task成功!" + remoteTask);
            return true;
        } else {
            LOGGER.debug("Push Task成功!" + remoteTask);
            return false;
        }
    }

    private boolean requireWorkerResources(final ProjectRuntime projectRuntime) {
        return application.getResourcesManager().requireResources(projectRuntime);
    }

    private Map<String, List<WorkerInfo>> getWorkers(Project project) {
        List<String> taskIDs = SQLiteOperation.getTasksId(application.getMasterNodeConfig().getRootPath(),
                project.getProjectId());
        if (taskIDs != null) {
            Map<String, List<WorkerInfo>> workersOfAProject = new HashMap<>();
            for (String taskID : taskIDs) {
                List<WorkerInfo> workers = application.getResourcesManager().getWorkers(taskID);
                workersOfAProject.put(taskID, workers);
            }
            return workersOfAProject;
        }
        return null;
    }

    private WorkerInfo getWorker(String taskID, int subTaskSequence) {
        LOGGER.debug(taskID + "正在获得已分配的worker....");
        List<WorkerInfo> workers = application.getResourcesManager().getWorkers(taskID);
        WorkerInfo workerInfo = workers.get(subTaskSequence - 1);
        LOGGER.debug(taskID + "获得worker:" + workerInfo);
        return workerInfo;
    }
}
