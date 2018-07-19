package org.iceslab.frobot.master.manager.project;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.master.*;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.utils.general.XMLParseUtil;

/**
 * 用于解析.xml的配置文件
 *
 * @author wanghao
 *         批注：函数参数的名称是以_File结尾，则输出的则是文件名，形如"/home/user/frobot/config.xml"的字符串
 *         函数参数的名称是以_Path结尾，则输出的则是文件所在的文件夹路径，形如"/home/user/frobot/"的字符串
 */

public class Parser {
    private final static Logger LOGGER = Logger.getLogger(Parser.class);

    public static Map<TaskInfo, String> parseTaskConfig(ProjectInfo projectInfo,
                                                        Map<String, DefaultTaskEngineInfo> taskEngines)
            throws XMLFileNotMatchException {
        Map<TaskInfo, String> taskInfos = null;
        try {
            taskInfos = new HashMap<TaskInfo, String>();
            XMLParseUtil projectInfoFile = XMLParseUtil
                    .createReadRoot(projectInfo.getProjectConfigFilePath() + File.separator + "project_config.xml");
            XMLParseUtil[] taskItem = projectInfoFile.getChildren("task");
            String taskConfigFilePath = null;
            for (XMLParseUtil task : taskItem) {
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setTaskName(task.getString("taskName"));
                taskInfo.setWorkerNum(task.getInteger("workerNum"));
                taskInfo.setTaskID(projectInfo.getProjectID() + "_" + task.getString("taskName"));
                taskInfo.setProjectID(projectInfo.getProjectID());
                taskInfo.setTaskSimpleName(task.getString("taskSimpleName"));
                taskConfigFilePath = projectInfo.getProjectConfigFilePath() + File.separator + "tasks" + File.separator
                        + task.getString("taskName");
                taskInfo.setTaskConfigFilePath(taskConfigFilePath);
                taskInfo.setSubTaskSequence(0);
                taskInfo.setNeedExtraEngine(projectInfo.getNeedExtraEngine());
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
                if (projectInfo.getNeedExtraEngine() == ProjectInfo.NEED_EXTRA_ENGINE) {
                    taskInfo.setTaskJarName(task.getString("taskJarName"));
                    taskInfo.setTaskEngineClassNameWithPackageName(task.getString("taskEngineClassWithPackageName"));
                    taskInfo.setTaskEngineJarPath(
                            MasterNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                                    + projectInfo.getUserName() + File.separator
                                    + projectInfo.getProjectName() + File.separator
                                    + "lib" + File.separator
                                    + "task_engine");
                } else {
                    DefaultTaskEngineInfo defaultTaskEngineInfo = taskEngines.get(task.getString("taskSimpleName"));
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
