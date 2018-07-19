package org.iceslab.frobot.master;

import java.io.Serializable;

public class ProjectInfo implements Serializable {
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


	public static final int START_ATONCE = 0;

	public static final int START_BYUSER = 1;

	public static final int START_DELAY = 2;
	

	private String projectID;

	private String projectName;

	private String userName;

	private String projectSimpleName;
	
	private String projectJarName;
	
	private String projectEngineJarPath;
	
	private String projectEngineClassNameWithPackageName;

	private String projectConfigFilePath;

	private int startType;// 启动方式: 0为立即启动，1为手动启动，2为定时启动

	private int startDelayTime;// 定时启动方式的延迟时间,单位默认分钟

	private int priority;

	private int projectStatus;
	
	private int needExtraEngine;

	public enum ProjectEngineSource {
		FROBOT,
		USER;
	}

	public static class Builder {
		private String userName;
		private String projectName;
		private String projectID;
		private String projectConfigFilePath;
		private String projectSimpleName;
		private String projectJarName;
		private String projectEngineJarPath;
		private String projectEngineClassNameWithPackageName;
		private int startType;
		private int startDelayTime;
		private int priority;
		private int projectStatus;
		private int needExtraEngine;

		public Builder userName(String val) {
			userName = val;
			return this;
		}

		public Builder projectName(String val) {
			projectName = val;
			return this;
		}

		public Builder projectID(String val) {
			projectID = val;
			return this;
		}

		public Builder projectSimpleName(String val) {
			projectSimpleName = val;
			return this;
		}
		
		public Builder projectJarName(String val){
			projectJarName = val;
			return this;
		}
		
		public Builder projectEngineJarPath(String val){
			projectEngineJarPath = val;
			return this;
		}
		public Builder projectEngineClassNameWithPackageName(String val){
			projectEngineClassNameWithPackageName = val;
			return this;
		}

		public Builder startType(int val) {
			startType = val;
			return this;
		}

		public Builder startDelayTime(int val) {
			startDelayTime = val;
			return this;
		}

		public Builder priority(int val) {
			priority = val;
			return this;
		}

		public Builder projectStatus(int val) {
			projectStatus = val;
			return this;
		}
		
		public Builder needExtraEngine(int val){
			needExtraEngine = val;
			return this;
		}

		public Builder projectConfigFilePath(String val) {
			projectConfigFilePath = val;
			return this;
		}
		

		public ProjectInfo build() {
			return new ProjectInfo(this);
		}
	}

	private ProjectInfo(Builder builder) {
		userName = builder.userName;
		projectName = builder.projectName;
		projectID = builder.projectID;
		projectSimpleName = builder.projectSimpleName;
		projectJarName = builder.projectJarName;
		projectEngineJarPath = builder.projectEngineJarPath;
		projectEngineClassNameWithPackageName = builder.projectEngineClassNameWithPackageName;
		projectConfigFilePath = builder.projectConfigFilePath;
		startType = builder.startType;
		startDelayTime = builder.startDelayTime;
		priority = builder.priority;
		projectStatus = builder.projectStatus;
		needExtraEngine = builder.needExtraEngine;
	}
	
	public ProjectInfo() {
		
	}

	/********************* getters && setters **************************************/
	public String getProjectID() {
		return projectID;
	}

	public void setProjectID(String projectID) {
		this.projectID = projectID;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getProjectConfigFilePath() {
		return projectConfigFilePath;
	}

	public void setProjectConfigFilePath(String projectConfigFilePath) {
		this.projectConfigFilePath = projectConfigFilePath;
	}

	public String getProjectSimpleName() {
		return projectSimpleName;
	}
    
	public String getProjectEngineJarPath() {
		return projectEngineJarPath;
	}

	public void setProjectEngineJarPath(String projectEngineJarPath) {
		this.projectEngineJarPath = projectEngineJarPath;
	}

	public String getProjectEngineClassNameWithPackageName() {
		return projectEngineClassNameWithPackageName;
	}

	public void setProjectEngineClassNameWithPackageName(String projectEngineClassNameWithPackageName) {
		this.projectEngineClassNameWithPackageName = projectEngineClassNameWithPackageName;
	}

	public void setProjectSimpleName(String projectSimpleName) {
		this.projectSimpleName = projectSimpleName;
	}

	public int getProjectStatus() {
		return projectStatus;
	}

	public void setProjectStatus(int projectStatus) {
		this.projectStatus = projectStatus;
	}

	public int getStartType() {
		return startType;
	}

	public void setStartType(int startType) {
		this.startType = startType;
	}

	public int getStartDelayTime() {
		return startDelayTime;
	}

	public void setStartDelayTime(int startDelayTime) {
		this.startDelayTime = startDelayTime;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getProjectJarName() {
		return projectJarName;
	}

	public void setProjectJarName(String projectJarName) {
		this.projectJarName = projectJarName;
	}

	public int getNeedExtraEngine() {
		return needExtraEngine;
	}

	public void setNeedExtraEngine(int needExtraEngine) {
		this.needExtraEngine = needExtraEngine;
	}

	@Override
	public String toString() {
		return "ProjectInfo{" +
				"projectID='" + projectID + '\'' +
				", projectName='" + projectName + '\'' +
				", userName='" + userName + '\'' +
				'}';
	}
}
