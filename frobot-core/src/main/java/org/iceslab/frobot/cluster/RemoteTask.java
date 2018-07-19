package org.iceslab.frobot.cluster;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Master将task信息封装到此类,传送给worker
 */
public class RemoteTask implements Serializable {

	private static final long serialVersionUID = 3863733464708763490L;

	private String userName;

	private String projectName;

	private String taskID;

	private int subTaskSequence;

	private String taskSimpleName;

	private String taskJarName;

	private String taskEngineJarPath;

	private String taskEngineClassNameWithPackageName;
	
	private String dependOn;
	
	private String ip;

	private int port;
	
	private String inputDateFilePath;

	private String configFilePath;

	private byte[] config;
	
	private int needExtraEngine;

	private String[] hashValues;

	private String charset;

	public RemoteTask() {

	}

	/************************* Getter & Setter *************************/

	public String getUserName() {
		return userName;
	}

	public RemoteTask setUserName(String userName) {
		this.userName = userName;
		return this;
	}

	public String getProjectName() {
		return projectName;
	}

	public RemoteTask setProjectName(String projectName) {
		this.projectName = projectName;
		return this;
	}

	public String getTaskID() {
		return taskID;
	}

	public RemoteTask setTaskID(String taskID) {
		this.taskID = taskID;
		return this;
	}

	public int getSubTaskSequence() {
		return subTaskSequence;
	}

	public RemoteTask setSubTaskSequence(int subTaskSequence) {
		this.subTaskSequence = subTaskSequence;
		return this;
	}

	public byte[] getConfig() {
		return config;
	}

	public void setConfig(byte[] config) {
		this.config = config;
	}

	public String[] getHashValues() {
		return hashValues;
	}

	public void setHashValues(String[] hashValues) {
		this.hashValues = hashValues;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
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

	public String getInputDateFilePath() {
		return inputDateFilePath;
	}

	public void setInputDateFilePath(String inputDateFilePath) {
		this.inputDateFilePath = inputDateFilePath;
	}

	public String getDependOn() {
		return dependOn;
	}

	public void setDependOn(String dependOn) {
		this.dependOn = dependOn;
	}

	public String getConfigFilePath() {
		return configFilePath;
	}

	public void setConfigFilePath(String configFilePath) {
		this.configFilePath = configFilePath;
	}

	public int getNeedExtraEngine() {
		return needExtraEngine;
	}

	public void setNeedExtraEngine(int needExtraEngine) {
		this.needExtraEngine = needExtraEngine;
	}

	@Override
	public String toString() {
		return "RemoteTask [\nuserName=" + userName + ",\nprojectName=" + projectName + ",\ntaskID=" + taskID
				+ ",\nsubTaskSequence=" + subTaskSequence + ",\ntaskSimpleName=" + taskSimpleName + ",\ntaskJarName="
				+ taskJarName + ",\ntaskEngineJarPath=" + taskEngineJarPath + ",\ntaskEngineClassNameWithPackageName="
				+ taskEngineClassNameWithPackageName + ",\ndependOn=" + dependOn + ",\nip=" + ip + ",\nport=" + port
				+ ",\ninputDateFilePath=" + inputDateFilePath + ",\nconfigFilePath="
				+ configFilePath + ",\nconfig=" + Arrays.toString(config) + ",\nneedExtraEngine=" + needExtraEngine
				+ ",\nhashValues=" + Arrays.toString(hashValues) + ",\ncharset=" + charset + "]";
	}
	
}