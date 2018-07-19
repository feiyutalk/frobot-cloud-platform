package org.iceslab.frobot.master.manager.resource;

import org.apache.log4j.Logger;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.master.manager.channel.WorkerManager;
import org.iceslab.frobot.master.manager.project.ProjectRuntime;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The resource resources manager. The manager maintain a table, which record
 * task and the worker it has applied for success.
 * @auther Neuclil
 */
public class ResourcesManager {
	private static final Logger LOGGER = Logger.getLogger(ResourcesManager.class);
	private MasterApplication application;
	private Map<String,List<String>> allocateTable = new ConcurrentHashMap<>();

	public ResourcesManager(MasterApplication application) {
		this.application = application;
	}

	/**
	 * According to project runtime, apply for project resources.
	 * @param projectRuntime the {@link ProjectRuntime}, which include needed resources
	 *                       to execute the project. According to {@link ProjectRuntime},
	 *                       the {@link WorkerManager}
	 *                       will select workers that meet
	 *                       requirements to execute the project.
	 * @return true if the resources apply for success, false otherwise.
	 */
	public boolean requireResources(ProjectRuntime projectRuntime){
		LOGGER.info("ResourcesManager start to apply for resources : " + projectRuntime);
		Map<TaskInfo, String> taskConfig = projectRuntime.getTaskInfos();
		List<String> allocatedIds = application.getWorkerManager().requireWorkerResources(projectRuntime);
		if(null == allocatedIds){
			LOGGER.warn("ResourcesManager apply for resources failure.");
			return false;
		}else{
			for (TaskInfo taskInfo : taskConfig.keySet()) {
				allocateTable.put(taskInfo.getTaskID(), allocatedIds);
			}
			LOGGER.info("ResourcesManager apply for resources successfully!"
					+ allocateTable);
			return true;
		}
	}

	/**
	 * Get the workers information that had allocated to the project.
	 * @param taskId task id
	 * @return
	 */
	public synchronized List<WorkerInfo> getWorkers(String taskId){
		LOGGER.info(taskId + "start to get allocated workers, now the allocated table is :" + allocateTable);
		List<String> workerIds = allocateTable.get(taskId);
		List<WorkerInfo> workers = application.getWorkerManager()
											  .getWorkers(workerIds);
		return workers;
	}

}
