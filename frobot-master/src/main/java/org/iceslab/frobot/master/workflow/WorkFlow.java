package org.iceslab.frobot.master.workflow;

import java.util.List;

import org.iceslab.frobot.cluster.TaskInfo;

/**
 * Created by Neuclil on 17-4-16.
 */
public interface WorkFlow {
	TaskInfo getNext(String id);
	
	TaskInfo current();
	
    TaskInfo next();
    
    List<TaskInfo> getTaskInfos();
}
