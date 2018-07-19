package org.iceslab.frobot.worker.manager.task;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.RemoteTask;
import org.iceslab.frobot.commons.utils.ExtensionLoader;
import org.iceslab.frobot.master.manager.project.TaskEngine;
import org.iceslab.frobot.master.manager.project.TaskUtils;
import org.iceslab.frobot.worker.WorkerApplication;
import org.iceslab.frobot.cluster.WorkerTaskInfo;
import org.iceslab.frobot.cluster.WorkerTaskResult;
import org.iceslab.frobot.commons.utils.db.WorkerDBOperation;
import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.request.PushTaskResultRequestBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;

import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 解析Project中通用的配置,并将其存放到workspace和数据库中
 */
public class TaskReceiver {
	/* 日志 */
	private static final Logger LOGGER = Logger.getLogger(TaskReceiver.class);
	/* 线程池,该线程池主要用于处理立即执行的task */
	private ThreadPoolExecutor executor;
	/* Application 用于存放系统运行时的一些数据和对象 */
	private WorkerApplication application;
	/* 类加载器 用于动态加载对象 */
	private ExtensionLoader<TaskEngine> loader;
	/**
	 * 构造函数
	 * 
	 * @param application
	 */
	public TaskReceiver(WorkerApplication application) {
		this.application = application;
		executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
		loader = new ExtensionLoader<TaskEngine>();
	}

	
	/**
	 * 解析task中的信息,并将其存入workspace和数据库
	 * 
	 * @param remoteTask
	 * @return
	 */
	public TaskReturnCode receive(RemoteTask remoteTask) {
		String taskID = remoteTask.getTaskID();
		int subTaskSequence = remoteTask.getSubTaskSequence();
		/* 判断项目是否已经存在 */
		if (WorkerDBOperation.hasTaskExisted(taskID, subTaskSequence)) {
			LOGGER.debug(taskID+" already exist！");
			return TaskReturnCode.TASKDEPLOY_AREDYEXIST;
		}
		try {
			String workspacePath = application.getWorkerNodeConfig().workspacePath;
			String taskPath = TaskUtils.saveTask(remoteTask, workspacePath);
			if (taskPath == null) {
				LOGGER.debug("save " + taskID + " failed");
				return TaskReturnCode.TASKSAVE_FAILURE;
			}

			WorkerTaskInfo workerTaskInfo = TaskUtils.getWorkerTaskInfoByRemoteTask(remoteTask);
			workerTaskInfo.setTaskConfigFilePath(taskPath);
			FileManageUtil.generateDirectory(workerTaskInfo.getOutputResultFilePath());
			executor.execute(new Runnable() {
				@Override
				public void run() {
					//动态加载 taskEngine
					TaskEngine taskEngine = null;
					try{
						String taskEngineJarPath = workerTaskInfo.getTaskEngineJarPath();
						String taskJarName = workerTaskInfo.getTaskJarName();
						String taskEngineClassNameWithPackageName = workerTaskInfo.getTaskEngineClassNameWithPackageName();
						taskEngine = loader.loadClass(taskEngineJarPath + File.separator +taskJarName,
								taskEngineClassNameWithPackageName, TaskEngine.class);
						LOGGER.debug("动态加载TaskEngine成功:" + taskEngine);
					}catch(ClassNotFoundException e){
					   	LOGGER.error("动态加载TaskEngine失败",e);
					}
					//执行taskEngine
					WorkerTaskResult workerTaskResult = taskEngine.handler(workerTaskInfo);
					LOGGER.debug("taskEngine执行成功!");

					//发送结果给master
					workerTaskResult.modifyOutputPath();
					RemotingCommand request = RemotingCommand.createRequestCommand(
							RemotingProtos.RequestCode.PUSH_TASK_RESULT.code(),
							new PushTaskResultRequestBody(workerTaskResult));
					RemotingCommand response = application.getRemotingClient().invokeSync(
							application.getMasterAddress(),
							request);
					//返回结果
				}
			});
            WorkerDBOperation.addSubTask(workerTaskInfo);
			LOGGER.debug(taskID+"提交成功");
			return TaskReturnCode.TASKDEPLOY_SUCCESS;
		} catch (Exception e) {
			LOGGER.debug("worker提交subtask异常!", e);
			return TaskReturnCode.TASKDEPLOY_FAIL;
		}
	}
}
