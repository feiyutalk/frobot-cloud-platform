package org.iceslab.frobot.master;

import java.io.Serializable;

/**
 * Created by Neuclil on 17-4-10.
 */
public class DefaultTaskEngineInfo implements Serializable{
	private static final long serialVersionUID = -7517017419030086427L;
	private String taskSimpleName; // for exmaple, spider
    private String taskJarName; // for example, spider.jar
    private String taskEngineName; // for example, org.iceslab.spider.SpidertaskEngine

    public DefaultTaskEngineInfo() {
    	
    }

    public DefaultTaskEngineInfo(String taskSimpleName, String taskJarName, String taskEngineName) {
        this.taskSimpleName = taskSimpleName;
        this.taskJarName = taskJarName;
        this.taskEngineName = taskEngineName;
    }
    
    /************************* 	Getter & Setter	*************************/

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

	public String getTaskEngineName() {
		return taskEngineName;
	}

	public void setTaskEngineName(String taskEngineName) {
		this.taskEngineName = taskEngineName;
	}
    
   




   
}
