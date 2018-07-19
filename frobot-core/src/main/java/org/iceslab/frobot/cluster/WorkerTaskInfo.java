package org.iceslab.frobot.cluster;

import java.io.Serializable;

public class WorkerTaskInfo implements Serializable {

	private static final long serialVersionUID = -4494265687081708677L;

	public static final int WAITING = 0;

	public static final int RUNNING = 1;

	public static final int STOP = 2;

	public static final int FINISH = 3;

	public static final int PAUSE = 4;


	private String taskID;

	private String userName;
	
	private String projectName;

	private String taskSimpleName;

	private String taskJarName;

	private String taskEngineJarPath;

	private String taskEngineClassNameWithPackageName;

	private String taskConfigFilePath;

	private int subTaskSequence;

	private String inputDateFilePath;

	private String outputResultFilePath;

	private String dependOn;

	public WorkerTaskInfo() {
	}

	/***************** get and set ***********************************/
	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getTaskSimpleName() {
		return taskSimpleName;
	}

	public void setTaskSimpleName(String taskSimpleName) {
		this.taskSimpleName = taskSimpleName;
	}

	public String getTaskJarName() {
		return taskJarName;
	}

	public void setTaskJarName(String taskJarName) {
		this.taskJarName = taskJarName;
	}

	public String getTaskEngineJarPath() {
		return taskEngineJarPath;
	}

	public void setTaskEngineJarPath(String taskEngineJarPath) {
		this.taskEngineJarPath = taskEngineJarPath;
	}

	public String getTaskEngineClassNameWithPackageName() {
		return taskEngineClassNameWithPackageName;
	}

	public void setTaskEngineClassNameWithPackageName(String taskEngineClassNameWithPackageName) {
		this.taskEngineClassNameWithPackageName = taskEngineClassNameWithPackageName;
	}

	public String getTaskConfigFilePath() {
		return taskConfigFilePath;
	}

	public void setTaskConfigFilePath(String taskConfigFilePath) {
		this.taskConfigFilePath = taskConfigFilePath;
	}

	public int getSubTaskSequence() {
		return subTaskSequence;
	}

	public void setSubTaskSequence(int subTaskSequence) {
		this.subTaskSequence = subTaskSequence;
	}

	public String getDependOn() {
		return dependOn;
	}

	public void setDependOn(String dependOn) {
		this.dependOn = dependOn;
	}

	public String getInputDateFilePath() {
		return inputDateFilePath;
	}

	public void setInputDateFilePath(String inputDateFilePath) {
		this.inputDateFilePath = inputDateFilePath;
	}

	public String getOutputResultFilePath() {
		return outputResultFilePath;
	}

	public void setOutputResultFilePath(String outputResultFilePath) {
		this.outputResultFilePath = outputResultFilePath;
	}

	@Override
	public String toString() {
		return "WorkerTaskInfo [\ntaskID=" + taskID + "\nuserName=" + userName + "\nprojectName=" + projectName
				+ "\ntaskSimpleName=" + taskSimpleName + "\ntaskJarName=" + taskJarName + "\ntaskEngineJarPath="
				+ taskEngineJarPath + "\ntaskEngineClassNameWithPackageName=" + taskEngineClassNameWithPackageName
				+ "\ntaskConfigFilePath=" + taskConfigFilePath + "\nsubTaskSequence=" + subTaskSequence
				+ "\ninputDateFilePath=" + inputDateFilePath + "\noutputResultFilePath=" + outputResultFilePath
				+ "\ndependOn=" + dependOn + "]";
	}

}
