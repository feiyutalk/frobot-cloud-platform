package org.iceslab.frobot.master.workflow;

import org.iceslab.frobot.cluster.TaskInfo;

import java.util.List;

/**
 * Created by Neuclil on 17-4-16.
 */
public class OneByOneWorkFlow implements WorkFlow {
    private List<TaskInfo> taskList;
    private int index;

    public OneByOneWorkFlow(List<TaskInfo> taskList){
        this.taskList = taskList;
        index = 0;
    }

    @Override
    public TaskInfo next() {
        synchronized (OneByOneWorkFlow.class){
            TaskInfo taskInfo = null;
            if(index < taskList.size()){
                taskInfo = taskList.get(index);
                index++;
            }
            return taskInfo;
        }
    }
    
    
    public TaskInfo getNext(String id){
    	for(int i=0; i<taskList.size(); i++){
    		if(taskList.get(i).getTaskID().equals(id)){
    			if((i+1) < taskList.size())
    				return taskList.get(i+1);
    		}
    	}
    	return null;
    }
    
    @Override
    public List<TaskInfo> getTaskInfos(){
        return taskList;
    }

    @Override
    public String toString() {
        return "OneByOneWorkFlow{" +
                "taskList=" + taskList +
                ", index=" + index +
                '}';
    }

	@Override
	public TaskInfo current() {
		return taskList.get(index-1);
	}
}
