package org.iceslab.frobot.master.manager.project;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.master.ProjectInfo;
import org.iceslab.frobot.cluster.RemoteTask;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.cluster.WorkerNodeConfig;
import org.iceslab.frobot.cluster.WorkerTaskInfo;
import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.iceslab.frobot.commons.utils.general.XMLParseUtil;

/**
 * 处理task相关的一些方法
 * 
 * @author wanghao
 *         批注：函数参数的名称是以_File结尾，则输出的则是文件名，形如"/home/user/frobot/config.xml"的字符串
 *         函数参数的名称是以_Path结尾，则输出的则是文件所在的文件夹路径，形如"/home/user/frobot/"的字符串
 */

public class TaskUtils {
	private static final Logger LOGGER = Logger.getLogger(TaskUtils.class);

	/**
	 * move these task configs to the level of tasks directory
	 * @param tasks
	 * @param project
	 * @return success or failure
	 */
	public static boolean ConfigsBackupToTasks(Map<TaskInfo, String> tasks, Project project) {
		String projectFilePath = project.getProjectRootDirectory();
		Map<String, String> xmlFile = FileManageUtil.getFileFromFolder(projectFilePath, "xml");
		Set<TaskInfo> taskInfoList = tasks.keySet();
		Iterator<TaskInfo> taskInfos = taskInfoList.iterator();
		TaskInfo taskInfo;
		while (taskInfos.hasNext()) {
			taskInfo = taskInfos.next();
			System.out.println(taskInfo.getTaskConfigName());
			ConfigBackupToTask(xmlFile.get(taskInfo.getTaskConfigName()), taskInfo);
		}
		return true;
	}
	
	/**
	 * move a task config to the level of task
	 * @param projectConfigFile
	 * @param taskInfo
	 * @return
	 */
	private static boolean ConfigBackupToTask(String projectConfigFile, TaskInfo taskInfo) {
		String source = projectConfigFile;
		String target = source.substring(0, source.lastIndexOf("/")) + File.separator + 
				        "tasks" + File.separator +
			     	    taskInfo.getTaskName();
		boolean copySuccess = copy(source, target);
		if (!copySuccess) {
			LOGGER.debug("write taskConfig file error! the taskID is : " + taskInfo.getTaskID());
			return false;
		}
		FileManageUtil.deleteFile(source);
		return true;
	}

	/**
	 * move all task datas to the level of tasks directory
	 * @param tasks
	 * @param projectInfo
	 * @return success or failure
	 */
	public static boolean DatasBackupToTasks(Map<TaskInfo, String> tasks, ProjectInfo projectInfo) {
		String projectFilePath = projectInfo.getProjectConfigFilePath();
		Map<String, String> xmlFile = FileManageUtil.getFileFromFolder(projectFilePath, "xml");
		Set<TaskInfo> dataList = tasks.keySet();
		Iterator<TaskInfo> taskInfos = dataList.iterator();
		TaskInfo taskInfo;
		while (taskInfos.hasNext()) {
			taskInfo = taskInfos.next();
			if (taskInfo.getTaskDataName() != null)
				DataBackupToTask(xmlFile.get(taskInfo.getTaskDataName()));
		}
		return true;
	}
	
	/**
	 * move one data file to the level of task
	 * @param projectDataFile
	 * @return
	 */
	private static boolean DataBackupToTask(String projectDataFile) {
		String target = projectDataFile.substring(0, projectDataFile.lastIndexOf("/")) + File.separator + 
				        "tasks";
		boolean copySuccess = copy(projectDataFile, target);
		if (!copySuccess) {
			LOGGER.debug("move data file to the level of task error！");
			return false;
		}
		FileManageUtil.deleteFile(projectDataFile);
		return true;
	}

	/**
	 * move the task config to the level of subtask
	 * @param taskInfo
	 * @return
	 */
	public static boolean ConfigBackupToSubTask(TaskInfo taskInfo) {
		String taskConfigFile = taskInfo.getTaskConfigFilePath() + File.separator + 
				                taskInfo.getTaskConfigName();
		for (int sequence = 1; sequence <= taskInfo.getWorkerNum(); sequence++) {
			String target = taskInfo.getTaskConfigFilePath() + File.separator + 
					        sequence;
			boolean copySuccess = copy(taskConfigFile, target);
			if (!copySuccess) {
				LOGGER.debug("write subTaskConfig file error");
				return false;
			}
		}
		FileManageUtil.deleteFile(taskConfigFile);
		return true;
	}

	public static String getProjectIDbyTaskID(String taskID) {
		return TaskDBOperation.getProjectIDbyTaskID(taskID);
	}

	public static RemoteTask getRemoteTaskByTaskInfo(TaskInfo subTask) {
		RemoteTask remoteTask = new RemoteTask();
		String projectID = subTask.getProjectID();
		String[] split = projectID.split("_");
		String userName = split[0];
		String projectName = split[1];
		remoteTask.setUserName(userName);
		remoteTask.setProjectName(projectName);
		remoteTask.setTaskID(subTask.getTaskID());
		remoteTask.setSubTaskSequence(subTask.getSubTaskSequence());
		remoteTask.setTaskJarName(subTask.getTaskJarName());
		remoteTask.setTaskSimpleName(subTask.getTaskSimpleName());
		remoteTask.setTaskEngineClassNameWithPackageName(subTask.getTaskEngineClassNameWithPackageName());
		remoteTask.setDependOn(subTask.getDependOn());
		remoteTask.setTaskEngineJarPath(null);
		remoteTask.setNeedExtraEngine(subTask.getNeedExtraEngine());

		String taskConfigFilePath = userName + File.separator + projectName + File.separator + subTask.getTaskID()
				+ File.separator + subTask.getSubTaskSequence();
		remoteTask.setConfigFilePath(taskConfigFilePath);

		/**  if a task doesn't depend on other task, it must own origin data*/
		if (subTask.getDependOn().equals("nodepend")) {
			String inputDataFilePath = userName + File.separator + projectName + File.separator + subTask.getTaskID()
					                 + File.separator + subTask.getSubTaskSequence() + File.separator + "data";
			remoteTask.setInputDateFilePath(inputDataFilePath);
		} else {
			String dependOn = subTask.getDependOn();
			String dependOnTaskID = projectID + "_" + dependOn;
			String inputDataFilePath = userName + File.separator + projectName + File.separator + dependOnTaskID
					+ File.separator + subTask.getSubTaskSequence() + File.separator + "result";
			remoteTask.setInputDateFilePath(inputDataFilePath);
		}
		String configFile = subTask.getTaskConfigFilePath() + File.separator + subTask.getTaskConfigName();
		remoteTask.setConfig(FileManageUtil.getBytesFromFile(new File(configFile)));
		remoteTask.setHashValues(null);
		remoteTask.setCharset("utf-8");
		return remoteTask;
	}

	public static WorkerTaskInfo getWorkerTaskInfoByRemoteTask(RemoteTask remoteTask) {
		WorkerTaskInfo workerTaskInfo = new WorkerTaskInfo();
		workerTaskInfo.setTaskID(remoteTask.getTaskID());
		workerTaskInfo.setUserName(remoteTask.getUserName());
		workerTaskInfo.setProjectName(remoteTask.getProjectName());
		workerTaskInfo.setTaskID(remoteTask.getTaskID());
		workerTaskInfo.setSubTaskSequence(remoteTask.getSubTaskSequence());
		workerTaskInfo.setTaskJarName(remoteTask.getTaskJarName());
		workerTaskInfo.setTaskEngineJarPath(
									WorkerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
											+ remoteTask.getUserName() + File.separator
											+ remoteTask.getProjectName() + File.separator
											+ "lib" + File.separator
											+ "task_engine");
		workerTaskInfo.setTaskSimpleName(remoteTask.getTaskSimpleName());
		workerTaskInfo.setTaskEngineClassNameWithPackageName(remoteTask.getTaskEngineClassNameWithPackageName());
		workerTaskInfo.setDependOn(remoteTask.getDependOn());
		workerTaskInfo.setInputDateFilePath(
									WorkerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator + remoteTask.getInputDateFilePath());
        workerTaskInfo.setTaskConfigFilePath(WorkerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator +remoteTask.getConfigFilePath());
		String outputResultFilePath =
									WorkerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
											+ remoteTask.getUserName() + File.separator
											+ remoteTask.getProjectName() + File.separator
											+ remoteTask.getTaskID() + File.separator
											+ remoteTask.getSubTaskSequence() + File.separator
											+ "result";
		workerTaskInfo.setOutputResultFilePath(outputResultFilePath);
		return workerTaskInfo;
	}

	public static Map<String, String> changeToTaskID(Map<TaskInfo, String> taskConfig) {
		Map<String, String> tasks = new HashMap<>();
		Set<Entry<TaskInfo, String>> entrySet = taskConfig.entrySet();
		Iterator<Entry<TaskInfo, String>> iterator = entrySet.iterator();
		while (iterator.hasNext()) {
			Entry<TaskInfo, String> next = iterator.next();
			tasks.put(next.getKey().getTaskID(), next.getValue());
		}
		return tasks;
	}

	/**
	 * save subTask information into worker locol file resource
	 * @param remoteTask
	 * @param workspacePath
	 * @return taskConfigFilePath
	 */
	public static String saveTask(RemoteTask remoteTask, String workspacePath) {
		if (!workspacePath.endsWith(File.separator)) {
			workspacePath += File.separator;
		}
		String taskPath = workspacePath + remoteTask.getUserName() + File.separator + remoteTask.getProjectName()
				+ File.separator + remoteTask.getTaskID() + File.separator + remoteTask.getSubTaskSequence();
		boolean success = FileManageUtil.generateDirectory(taskPath);
		if(!success) {
			LOGGER.error("generate task directory error");
			return null;
		}
		/**  save taskConfig.xml  */
		String configFilePath = taskPath + File.separator + "taskConfig.xml";
		boolean saveRemoteTaskConfigSuccess = saveRemoteTaskFile(remoteTask.getConfig(), configFilePath);
		if(!saveRemoteTaskConfigSuccess) {
			LOGGER.error("save taskConfig.xml to worker error");
			return null;
		}
		/**  wirte .task */
		String infoFilePath = taskPath + ".task";
		boolean writeTaskBasicInfoSuccess = writeTaskBasicFile(infoFilePath, remoteTask);
		if(!writeTaskBasicInfoSuccess) {
			LOGGER.warn("write .task error, taskConfig is :");
		}
		return taskPath;
	}


	private static boolean saveRemoteTaskFile(byte[] bytes, String filePath) {
		File file = new File(filePath);
		file.delete();
		if (file.exists()) {
			return false;
		}
		return FileManageUtil.writeBytesToFile(bytes, file);
	}
	
	private static boolean writeTaskBasicFile(String filePath, RemoteTask remoteTask) {
		File file = new File(filePath);
		file.delete();
		if (file.exists()) {
			return false;
		}
		return writeTaskBasicInfo(filePath, remoteTask);
	}
	
	/**
	 * copy a file from a directory to other directory
	 * @param source
	 * @param targetPath
	 * @return
	 */
	private static boolean copy(String source, String targetPath) {
		File sourceFile = new File(source);
		File targetFile = new File(targetPath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new BufferedInputStream(new FileInputStream(sourceFile), 16 * 1024);
			FileOutputStream fileOutputStream = new FileOutputStream(
					targetFile + source.substring(source.lastIndexOf("/"), source.length()));// 一定要加上文件名称
			out = new BufferedOutputStream(fileOutputStream, 16 * 1024);
			byte[] buffer = new byte[16 * 1024];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			return true;
		} catch (Exception e) {
			LOGGER.error("write file error", e);
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					LOGGER.warn("close input stream failure");
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {
					LOGGER.warn("close output stream failure");
				}
			}
		}
		return false;
	}

	
	/**
	 * write .task
	 * @param infoFile
	 * @param remoteTask
	 */
	private static boolean writeTaskBasicInfo(String infoFile, RemoteTask remoteTask) {
		XMLParseUtil projectInfoFile = XMLParseUtil.createWriteRoot("task");
		XMLParseUtil item = null;
		item = projectInfoFile.createChild("taskID");
		item.putString("taskID", remoteTask.getTaskID());

		item = projectInfoFile.createChild("sequence");
		item.putInteger("sequence", remoteTask.getSubTaskSequence());
		try {
			projectInfoFile.save(infoFile);
		} catch (IOException e) {
			LOGGER.debug("write .task error");
			return false;
		}
		return true;
	}

	/**
	 * according to workerNum, divide the task data to workerNum subTask datas
	 * @param taskInfo
	 */
	public static void divideTaskDataToSubTaskData(TaskInfo taskInfo) {
		try {
			String taskConfigFilePath = taskInfo.getTaskConfigFilePath();
			String taskDataFile = taskConfigFilePath.substring(0, taskConfigFilePath.lastIndexOf("/")) + File.separator + 
					              taskInfo.getTaskDataName();
			XMLParseUtil taskDataItem = XMLParseUtil.createReadRoot(taskDataFile);
			XMLParseUtil[] urlItem = taskDataItem.getChildren("url");
			List<String> taskData = new ArrayList<String>();
			String taskDataFilePath = taskConfigFilePath.substring(0, taskConfigFilePath.lastIndexOf("/")) + File.separator + 
					taskInfo.getTaskName() + File.separator;
			for (XMLParseUtil url : urlItem) {
				taskData.add(url.getTextData());
			}
			Map<Integer, Integer> nums = calculateSubTaskDataNum(taskData.size(), taskInfo.getWorkerNum());
			Set<Entry<Integer, Integer>> entrySet = nums.entrySet();
			Iterator<Entry<Integer, Integer>> iterator = entrySet.iterator();
			while (iterator.hasNext()) {
				Entry<Integer, Integer> entry = iterator.next();
				int subTaskSequence = entry.getKey();
				int getDataNum = entry.getValue();
				List<String> subData = new ArrayList<String>();
				for (int i = 0; i < getDataNum; i++) {
					subData.add(taskData.get(0));
					taskData.remove(0);
				}
				boolean success = FileManageUtil.generateDirectory(taskDataFilePath + subTaskSequence + File.separator);
				if (!success) {
					LOGGER.error("generate the directory of subTask dataset error");
					return;
				}
				String subTaskDataFile = taskDataFilePath + subTaskSequence + File.separator + 
						                 taskInfo.getTaskDataName();
				
				File dataFile = new File(subTaskDataFile);
				dataFile.delete();
				if (dataFile.exists()) {
					System.out.println("delete old subTask data error!");
					return;
				}
				writeTaskData(subData, subTaskDataFile);
				FileManageUtil.deleteFile(taskDataFile);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * write the dataset to xml file
	 * @param datas
	 * @param taskDataFile
	 */
	private static void writeTaskData(List<String> datas, String taskDataFile) {
		XMLParseUtil dataItem = XMLParseUtil.createWriteRoot("data");
		XMLParseUtil urlItem = null;
		for (String data : datas) {
			urlItem = dataItem.createChild("url");
			urlItem.putTextData(data);
		}
		try {
			dataItem.save(taskDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * divide the datas
	 * @param urlNum
	 * @param workerNum
	 * @return Map<key:workerIndex, value:dividedUrlNum>
	 */
	protected static Map<Integer, Integer> calculateSubTaskDataNum(int urlNum, int workerNum) {
		Map<Integer, Integer> nums = new HashMap<Integer, Integer>();
	    //  the num of url is less than worker
		if (urlNum < workerNum) {
			for (int i = 1; i <= workerNum; i++) {
				if (urlNum > 0) {
					nums.put(i, 1);
					urlNum--;
				} else {
					nums.put(i, 0);
				}
			}
			return nums;
		} else {
			int one = urlNum / workerNum;
			int two = one + 1;
			int twoNum = urlNum % workerNum;
			for (int i = 1; i <= twoNum; i++) {
				nums.put(i, two);
			}
			for (int j = twoNum + 1; j <= workerNum; j++) {
				nums.put(j, one);
			}
			return nums;
		}
	}
}
