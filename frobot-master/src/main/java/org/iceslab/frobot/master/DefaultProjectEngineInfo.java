package org.iceslab.frobot.master;

import java.io.Serializable;

/**
 * Created by Neuclil on 17-4-10.
 */
public class DefaultProjectEngineInfo implements Serializable{
	private static final long serialVersionUID = 3353439381428174908L;
	private String projectSimpleName; // for exmaple, spider
    private String projectJarName; // for example, spider.jar
    private String projectEngineName; // for example, org.iceslab.spider.SpiderProjectEngine

    public DefaultProjectEngineInfo() {
    }

    public DefaultProjectEngineInfo(String projectSimpleName, String projectJarName, String projectEngineName) {
        this.projectSimpleName = projectSimpleName;
        this.projectJarName = projectJarName;
        this.projectEngineName = projectEngineName;
    }
    
    /************************* 	Getter & Setter	*************************/

	public String getProjectSimpleName() {
		return projectSimpleName;
	}

	public void setProjectSimpleName(String projectSimpleName) {
		this.projectSimpleName = projectSimpleName;
	}

	public String getProjectJarName() {
		return projectJarName;
	}

	public void setProjectJarName(String projectJarName) {
		this.projectJarName = projectJarName;
	}

	public String getProjectEngineName() {
		return projectEngineName;
	}

	public void setProjectEngineName(String projectEngineName) {
		this.projectEngineName = projectEngineName;
	}

}
