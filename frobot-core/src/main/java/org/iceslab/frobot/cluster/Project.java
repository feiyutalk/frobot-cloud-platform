package org.iceslab.frobot.cluster;

import java.io.Serializable;
import java.util.Map;

public class Project implements Serializable {
    private static final long serialVersionUID = 961508603564220387L;

    public static final int WAITING = 0;

    public static final int RUNNING = 1;

    public static final int STOP = 2;

    public static final int FINISH = 3;

    public static final int PAUSE = 4;

    public static final int DELETE = 5;

    public static final int DISCONNECTION = 6;


    public static final int NOT_NEED_EXTRA_ENGINE = 0;

    public static final int NEED_EXTRA_ENGINE = 1;


    public static final int START_ATONCE = 0;

    public static final int START_BYUSER = 1;

    public static final int START_DELAY = 2;

    private String projectId;

    private String userName;

    private String projectName;

    private String projectSimpleName;

    private boolean personalProjectEngine;

    private String projectEngineClassWithPackageName;

    private String startType;

    private long startDelayTime;

    private String projectJarName;

    private String projectJarPath;

    private int taskNum;

    private byte[] projectConfigFile;

    private Map<String, byte[]> taskConfigFiles;

    private String charset;

    private String[] hashValues;

    private int UIProjectStatus;

    private String ip;

    private int port;

    private String[] taskJarName;

    private String projectRootDirectory;

    private String configFilePath;

    private int projectStatus;

    private int priority;

    public Project() {
    }

    /************************* 	Getter & Setter	*************************/
    public String getProjectId() {
        return projectId;
    }

    public Project setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public String getProjectSimpleName() {
        return projectSimpleName;
    }

    public Project setProjectSimpleName(String projectSimpleName) {
        this.projectSimpleName = projectSimpleName;
        return this;
    }

    public boolean isPersonalProjectEngine() {
        return personalProjectEngine;
    }

    public Project setPersonalProjectEngine(boolean personalProjectEngine) {
        this.personalProjectEngine = personalProjectEngine;
        return this;
    }

    public String getProjectEngineClassWithPackageName() {
        return projectEngineClassWithPackageName;
    }

    public Project setProjectEngineClassWithPackageName(String projectEngineClassWithPackageName) {
        this.projectEngineClassWithPackageName = projectEngineClassWithPackageName;
        return this;
    }

    public String getStartType() {
        return startType;
    }

    public Project setStartType(String startType) {
        this.startType = startType;
        return this;
    }

    public long getStartDelayTime() {
        return startDelayTime;
    }

    public Project setStartDelayTime(long startDelayTime) {
        this.startDelayTime = startDelayTime;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public Project setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getProjectRootDirectory() {
        return projectRootDirectory;
    }

    public Project setProjectRootDirectory(String projectRootDirectory) {
        this.projectRootDirectory = projectRootDirectory;
        return this;
    }

    public String getProjectName() {
        return projectName;
    }

    public Project setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public String getProjectJarName() {
        return projectJarName;
    }

    public Project setProjectJarName(String projectJarName) {
        this.projectJarName = projectJarName;
        return this;
    }


    public byte[] getProjectConfigFile() {
        return projectConfigFile;
    }

    public Project setProjectConfigFile(byte[] projectConfigFile) {
        this.projectConfigFile = projectConfigFile;
        return this;
    }

    public Map<String, byte[]> getTaskConfigFiles() {
        return taskConfigFiles;
    }

    public Project setTaskConfigFiles(Map<String, byte[]> taskConfigFiles) {
        this.taskConfigFiles = taskConfigFiles;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public Project setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public String[] getHashValues() {
        return hashValues;
    }

    public Project setHashValues(String[] hashValues) {
        this.hashValues = hashValues;
        return this;
    }

    public int getUIProjectStatus() {
        return UIProjectStatus;
    }

    public Project setUIProjectStatus(int uIProjectStatus) {
        UIProjectStatus = uIProjectStatus;
        return this;
    }

    public String getIp() {
        return ip;
    }

    public Project setIp(String ip) {
        this.ip = ip;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Project setPort(int port) {
        this.port = port;
        return this;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public Project setTaskNum(int taskNum) {
        this.taskNum = taskNum;
        return this;
    }

    public String[] getTaskJarName() {
        return taskJarName;
    }

    public Project setTaskJarName(String[] taskJarName) {
        this.taskJarName = taskJarName;
        return this;
    }

    public int getProjectStatus() {
        return projectStatus;
    }

    public Project setProjectStatus(int projectStatus) {
        this.projectStatus = projectStatus;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public Project setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    public String getProjectJarPath() {
        return projectJarPath;
    }

    public Project setProjectJarPath(String projectJarPath) {
        this.projectJarPath = projectJarPath;
        return this;
    }

    public String getConfigFilePath() {
        return configFilePath;
    }

    public void setConfigFilePath(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    @Override
    public String toString() {
        return "Project{" +
                "userName='" + userName + '\'' +
                ", projectName='" + projectName + '\'' +
                '}';
    }
}
