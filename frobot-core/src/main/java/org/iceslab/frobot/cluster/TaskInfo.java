package org.iceslab.frobot.cluster;

import java.io.Serializable;

public class TaskInfo implements Serializable {

    private static final long serialVersionUID = -4494265687081708677L;

    public static final int WAITING = 0;

    public static final int RUNNING = 1;

    public static final int STOP = 2;

    public static final int FINISH = 3;

    public static final int PAUSE = 4;

    public static final int DELETE = 5;

    public static final int DISCONNECTION = 6;


    public static final int NOT_NEED_EXTRA_ENGINE = 0;

    public static final int NEED_EXTRA_ENGINE = 1;

    private String taskID;

    private String taskName;

    private String projectID;

    private int workerNum;

    private int cup;

    private int hardDisk;

    private int memory;

    private String taskSimpleName;

    private String taskJarName;

    private String taskEngineJarPath;

    private String taskEngineClassNameWithPackageName;

    private String taskConfigFilePath;

    private int subTaskSequence;

    private String taskConfigName;

    private String taskDataName;

    private String dependOn;

    private int needExtraEngine;

    private int taskStatus;

    private String ip;

    private int dataPort;


    public TaskInfo() {
    }

    /***************** get and set ***********************************/

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }


    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getProjectID() {
        return projectID;
    }

    public void setProjectID(String projectID) {
        this.projectID = projectID;
    }

    public int getWorkerNum() {
        return workerNum;
    }

    public void setWorkerNum(int workerNum) {
        this.workerNum = workerNum;
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

    public String getTaskConfigName() {
        return taskConfigName;
    }

    public void setTaskConfigName(String taskConfigName) {
        this.taskConfigName = taskConfigName;
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

    public String getTaskDataName() {
        return taskDataName;
    }

    public void setTaskDataName(String taskDataName) {
        this.taskDataName = taskDataName;
    }

    public String getDependOn() {
        return dependOn;
    }

    public void setDependOn(String dependOn) {
        this.dependOn = dependOn;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getDataPort() {
        return dataPort;
    }

    public void setDataPort(int dataPort) {
        this.dataPort = dataPort;
    }

    public int getNeedExtraEngine() {
        return needExtraEngine;
    }

    public void setNeedExtraEngine(int needExtraEngine) {
        this.needExtraEngine = needExtraEngine;
    }

    public int getCup() {
        return cup;
    }

    public void setCup(int cup) {
        this.cup = cup;
    }

    public int getHardDisk() {
        return hardDisk;
    }

    public void setHardDisk(int hardDisk) {
        this.hardDisk = hardDisk;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "TaskInfo{" +
                "taskID='" + taskID + '\'' +
                ", taskName='" + taskName + '\'' +
                ", subTaskSequence=" + subTaskSequence +
                '}';
    }
}
