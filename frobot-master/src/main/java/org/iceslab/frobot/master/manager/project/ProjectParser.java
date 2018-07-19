package org.iceslab.frobot.master.manager.project;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.ProjectReturnCode;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.utils.db.SQLiteOperation;
import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.iceslab.frobot.commons.utils.general.XMLParseUtil;
import org.iceslab.frobot.master.*;
import org.iceslab.frobot.cluster.Project;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Project Parser used to parser project_config.xml
 *
 * @auther Neuclil
 */
public class ProjectParser {
    private static final Logger LOGGER = Logger.getLogger(ProjectParser.class);

    private MasterApplication application;

    public ProjectParser(MasterApplication application) {
        this.application = application;
    }

    public void start() {
        //TODO:
    }

    /**
     * @param project
     * @return
     */
    public ProjectReturnCode parseAndSaveProject(Project project) {
        if (SQLiteOperation.isProjectExist(application.getRootPath(),
                project.getProjectId())) {
            LOGGER.warn("project has exist, save project to database error!");
            return ProjectReturnCode.PROJECTDEPLOY_AREDYEXIXT;
        }
        try {
            String configFilePath = saveProject(project, application.getWorkspacePath());
            if (configFilePath == null) {
                LOGGER.warn("save project error!");
                return ProjectReturnCode.PROJECTSAVE_FAILURE;
            }

            checkProjectJarInfo(project);

            project.setPriority(1);

            project.setProjectStatus(ProjectInfo.RUNNING);

            SQLiteOperation.addProject(application.getRootPath(), project);
            LOGGER.info("project parse and save successfully.");
            return ProjectReturnCode.PROJECTDEPLOY_SUCCESS;
        } catch (Exception e) {
            LOGGER.debug("project parse and save error!", e);
            project.setProjectStatus(ProjectInfo.WAITING);
            return ProjectReturnCode.PROJECTDEPLOY_FAIL;
        }
    }

    private void checkProjectJarInfo(Project project) {
        if (!project.isPersonalProjectEngine()) {
            DefaultProjectEngineInfo defaultProjectEngineInfo = application.getDefaultProjectEngines()
                    .get(project.getProjectSimpleName());
            project.setProjectJarName(defaultProjectEngineInfo.getProjectJarName())
                    .setProjectJarPath(MasterNodeConfig.DEFAULT_PROJECT_ENGINE_PATH)
                    .setProjectEngineClassWithPackageName(defaultProjectEngineInfo.getProjectEngineName());
        } else {
            project.setProjectJarPath(MasterNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                    + project.getUserName() + File.separator
                    + project.getProjectName() + File.separator
                    + "lib" + File.separator
                    + "project_engine");
        }
    }

    public static String saveProject(Project project, String workspacePath) {
        if (!workspacePath.endsWith(File.separator)) {
            workspacePath += File.separator;
        }
        String projectRootDirectory = workspacePath
                + project.getUserName() + File.separator
                + project.getProjectName();
        project.setProjectRootDirectory(projectRootDirectory);

        if (!FileManageUtil.generateDirectory(projectRootDirectory)) {
            LOGGER.error("generate project directory error");
            return null;
        }

        /**  save projectConfig.xml  */
        String configFilePath = projectRootDirectory + File.separator + "project_config.xml";
        if (!FileManageUtil.writeBytesToFile(project.getProjectConfigFile(), configFilePath)) {
            LOGGER.error("save projectConfig.xml error");
            return null;
        }
        project.setConfigFilePath(configFilePath);

        /**  save taskconfig.xml  */
        Set<Map.Entry<String, byte[]>> entrySetTaskConfig = project.getTaskConfigFiles().entrySet();
        for (Map.Entry<String, byte[]> taskConfig : entrySetTaskConfig) {
            if (!FileManageUtil.writeBytesToFile(taskConfig.getValue(),
                    projectRootDirectory + File.separator + taskConfig.getKey())) {
                LOGGER.error("save taskConfig.xml error, taskConfig is :" + projectRootDirectory + taskConfig.getKey());
                return null;
            }
        }

        /**  write  .project  */
        if (!writeBasicInfo(projectRootDirectory + File.separator + ".project", project)) {
            LOGGER.warn("write .project error, taskConfig is :");
        }
        return configFilePath;
    }

    private static boolean writeBasicInfo(String infoFile, Project project) {
        File file = new File(infoFile);
        file.delete();
        if (file.exists()) {
            return false;
        }
        XMLParseUtil projectInfoFile = XMLParseUtil.createWriteRoot("project");
        XMLParseUtil item = null;
        XMLParseUtil subItem = null;
        item = projectInfoFile.createChild("userName");
        item.putString("userName", project.getUserName());

        item = projectInfoFile.createChild("project");
        item.putString("projectName", project.getProjectName());

        item = projectInfoFile.createChild("projectID");
        item.putString("id", project.getUserName() + project.getProjectName());

        item = projectInfoFile.createChild("hashValues");
        subItem = item.createChild("dataHash");
        subItem.putString("value", project.getHashValues()[0]);

        subItem = item.createChild("configHash");
        subItem.putString("value", project.getHashValues()[1]);
        try {
            projectInfoFile.save(infoFile);
            return true;
        } catch (IOException e) {
            LOGGER.debug("write .project file error");
            return false;
        }
    }

    public static Map<TaskInfo, String> parseTasksBasicConfig(Project project,
                                                              Map<String, DefaultTaskEngineInfo> defaultTaskEngines)
            throws XMLFileNotMatchException {
        Map<TaskInfo, String> taskInfos = null;
        try {
            taskInfos = new HashMap<TaskInfo, String>();
            XMLParseUtil projectRoot = XMLParseUtil
                    .createReadRoot(project.getConfigFilePath());
            XMLParseUtil[] taskItem = projectRoot.getChildren("task");
            String taskConfigFilePath = null;
            for (XMLParseUtil task : taskItem) {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskName(task.getString("taskName"));
                taskInfo.setWorkerNum(task.getInteger("workerNum"));
                taskInfo.setProjectID(project.getProjectId());
                taskInfo.setTaskID(project.getProjectId() + "_" + task.getString("taskName"));
                taskInfo.setTaskSimpleName(task.getString("taskSimpleName"));
                taskConfigFilePath = project.getProjectRootDirectory() + File.separator + "tasks" + File.separator
                        + task.getString("taskName");
                taskInfo.setTaskConfigFilePath(taskConfigFilePath);
                taskInfo.setSubTaskSequence(0); //TODO: modify the int value to status enum
                taskInfo.setNeedExtraEngine(project.isPersonalProjectEngine() ? 1 : 0);
                XMLParseUtil dependencyItem = task.getChild("dependency");
                boolean existDependency = dependencyItem.getBoolean("existDependency");
                XMLParseUtil taskDataNameItem = task.getChild("taskDataName");
                if (existDependency) {
                    taskInfo.setTaskDataName(null);
                    taskInfo.setDependOn(dependencyItem.getTextData());
                } else {
                    taskInfo.setTaskDataName(taskDataNameItem.getTextData());
                    taskInfo.setDependOn(dependencyItem.getTextData());
                }
                XMLParseUtil taskConfigNameItem = task.getChild("taskConfigName");
                taskInfo.setTaskConfigName(taskConfigNameItem.getTextData());
                if (project.isPersonalProjectEngine()) {
                    taskInfo.setTaskJarName(task.getString("taskJarName"));
                    taskInfo.setTaskEngineClassNameWithPackageName(task.getString("taskEngineClassWithPackageName"));
                    taskInfo.setTaskEngineJarPath(
                            MasterNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                                    + project.getUserName() + File.separator
                                    + project.getProjectName() + File.separator
                                    + "lib" + File.separator
                                    + "task_engine");
                } else {
                    DefaultTaskEngineInfo defaultTaskEngineInfo = defaultTaskEngines.get(task.getString("taskSimpleName"));
                    taskInfo.setTaskJarName(defaultTaskEngineInfo.getTaskJarName());
                    taskInfo.setTaskEngineClassNameWithPackageName(defaultTaskEngineInfo.getTaskEngineName());
                    taskInfo.setTaskEngineJarPath(
                            MasterNodeConfig.DEFAULT_TASK_ENGINE_PATH);
                }
                taskInfos.put(taskInfo, dependencyItem.getTextData());
            }
        } catch (IOException e) {
            throw new XMLFileNotMatchException();
        }
        return taskInfos;
    }

}
