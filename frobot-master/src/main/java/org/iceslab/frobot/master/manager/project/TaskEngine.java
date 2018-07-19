package org.iceslab.frobot.master.manager.project;

import java.util.List;

import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.cluster.WorkerTaskInfo;
import org.iceslab.frobot.cluster.WorkerTaskResult;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;

/**
 * Created by Neuclil on 17-4-18.
 */
public interface TaskEngine {
	List<TaskInfo> init(List<TaskInfo> taskInfos);

	boolean start(List<TaskInfo> subTasks, List<WorkerInfo> workers, final MasterApplication application);

	void pause();

	void stop();

	WorkerTaskResult handler(WorkerTaskInfo workerTaskInfo);
	
}
