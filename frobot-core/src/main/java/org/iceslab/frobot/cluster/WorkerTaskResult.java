package org.iceslab.frobot.cluster;

import java.io.Serializable;

/**
 * Created by Neuclil on 17-4-16.
 */
public class WorkerTaskResult implements Serializable {
	private static final long serialVersionUID = 8557852186262885952L;
	private String taskID;
    private int subTaskSequence;
    private String outputPath;

    public WorkerTaskResult(String taskID, int subTaskSequence, String outputPath) {
        this.taskID = taskID;
        this.subTaskSequence = subTaskSequence;
        this.outputPath = outputPath;
    }
    
    public void modifyOutputPath(){
		outputPath = outputPath.substring(WorkerNodeConfig.DEFAULT_WORKSPACE_PATH.length());
    }

    /************************* 	Getter & Setter	*************************/
    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public int getSubTaskSequence() {
        return subTaskSequence;
    }

    public void setSubTaskSequence(int subTaskSequence) {
        this.subTaskSequence = subTaskSequence;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
