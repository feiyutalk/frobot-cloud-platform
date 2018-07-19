package org.iceslab.frobot.master.manager.project;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.utils.ExtensionLoader;
import org.iceslab.frobot.master.DefaultTaskEngineInfo;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.master.workflow.WorkFlow;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;

/**
 * Abstract Project Engine which implements Project Engine interface and implements
 * some basic method.
 *
 * @auther Neuclil
 */
public abstract class AbstractProjectEngine implements ProjectEngine {
    private ExtensionLoader<AbstractTaskEngine> loader;

    public AbstractProjectEngine() {
        loader = new ExtensionLoader<AbstractTaskEngine>();
    }

    @Override
    public ProjectRuntime init(Project project, Map<String, DefaultTaskEngineInfo> taskEngines) throws XMLFileNotMatchException {
        beforeInit(project);
        Map<TaskInfo, String> taskInfos = ProjectParser.parseTasksBasicConfig(project, taskEngines);
        Iterator<TaskInfo> iterator = taskInfos.keySet().iterator();
        TaskInfo firstTaskInfo = iterator.next();
        ProjectRuntime projectRuntime = new ProjectRuntime.Builder()
                .workerNum(firstTaskInfo.getWorkerNum())
                .cpu(firstTaskInfo.getCup())
                .hardDisk(firstTaskInfo.getHardDisk())
                .memory(firstTaskInfo.getMemory())
                .taskInfos(taskInfos).build();
        return projectRuntime;
    }

    protected abstract void beforeInit(Project project);

    /**
     * Start the project engine according to the workflow.
     *
     * @param workFlow    work flow indicates work execute order.
     * @param workers     workers to execute the project.
     * @param application application that simplify the information sharing.
     */
    @Override
    public final void start(WorkFlow workFlow, Map<String, List<WorkerInfo>> workers, MasterApplication application)
            throws ClassNotFoundException {
        beforeStart();
        TaskInfo taskInfo = workFlow.next();
        AbstractTaskEngine taskEngine = loadTaskEngine(taskInfo);
        List<TaskInfo> subTasks = taskEngine.init(workFlow.getTaskInfos());
        for (TaskInfo taskInfo2 : subTasks) {
            taskInfo2.setIp(application.getMasterNodeConfig().getIp());
            taskInfo2.setDataPort(application.getMasterNodeConfig().getDataPort());
        }
        taskEngine.start(subTasks, workers.get(taskInfo.getTaskID()), application);
    }

    public abstract void beforeStart();

    private AbstractTaskEngine loadTaskEngine(TaskInfo taskInfo) throws ClassNotFoundException {
        String taskEngineJarPath = taskInfo.getTaskEngineJarPath();
        String taskJarName = taskInfo.getTaskJarName();
        String taskEngineClassNameWithPackageName =
                taskInfo.getTaskEngineClassNameWithPackageName();
        AbstractTaskEngine taskEngine = loader.loadClass(taskEngineJarPath + File.separator + taskJarName,
                taskEngineClassNameWithPackageName, AbstractTaskEngine.class);
        return taskEngine;
    }

    @Override
    public void stop() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

}
