package org.iceslab.frobot.master;

import org.iceslab.frobot.master.manager.channel.WorkerManager;
import org.iceslab.frobot.master.manager.project.ProjectManager;
import org.iceslab.frobot.master.manager.project.ProjectParser;
import org.iceslab.frobot.master.manager.project.ProjectScheduler;
import org.iceslab.frobot.master.manager.resource.ResourcesManager;
import org.iceslab.frobot.commons.io.FileSender;
import org.iceslab.frobot.remoting.delegate.RemotingServerDelegate;

import java.util.Map;

/**
 * @author Neuclil
 *         Master节点的Application 用于存放系统中的数据和对象
 */
public class MasterApplication {
    /* Master节点的配置信息 */
    private MasterNodeConfig masterNodeConfig;
    /* 通信服务端代理 用于处理通信事务 */
    private RemotingServerDelegate remotingServer;
    /* 负责解析 存储远程传过来的Project */
    private ProjectParser projectParser;
    /* 负责管理整个系统中的Project, ProjectEngine等 */
    private ProjectManager projectManager;
    /* 定时Project的调度器 其功能类似于定时器*/
    private ProjectScheduler projectScheduler;
    /* WorkerManager 管理worker的状态信息 */
    private WorkerManager workerManager;
    /* 系统状态信息管理者 */
    private ResourcesManager resourcesManager;
    /* 数据端口 */
    private FileSender fileSender;

    private static final MasterApplication application = new MasterApplication();

    /**
     * 单例,采用饿汉模式
     */
    private MasterApplication() {
    }

    public static MasterApplication getInstance() {
        return application;
    }


    /************************* 	Getter & Setter	*************************/
    public MasterNodeConfig getMasterNodeConfig() {
        return masterNodeConfig;
    }

    public void setMasterNodeConfig(MasterNodeConfig masterNodeConfig) {
        this.masterNodeConfig = masterNodeConfig;
    }

    public WorkerManager getWorkerManager() {
        return workerManager;
    }

    public void setWorkerManager(WorkerManager workerManager) {
        this.workerManager = workerManager;
    }

    public RemotingServerDelegate getRemotingServer() {
        return remotingServer;
    }

    public void setRemotingServer(RemotingServerDelegate remotingServer) {
        this.remotingServer = remotingServer;
    }

    public ProjectParser getProjectParser() {
        return projectParser;
    }

    public void setProjectParser(ProjectParser projectParser) {
        this.projectParser = projectParser;
    }


    public ProjectScheduler getProjectScheduler() {
        return projectScheduler;
    }

    public void setProjectScheduler(ProjectScheduler projectScheduler) {
        this.projectScheduler = projectScheduler;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public void setProjectManager(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    public ResourcesManager getResourcesManager() {
        return resourcesManager;
    }

    public void setResourcesManager(ResourcesManager resourcesManager) {
        this.resourcesManager = resourcesManager;
    }

    public String getWorkspacePath() {
        return masterNodeConfig.getWorkspacePath();
    }

    public Map<String, DefaultProjectEngineInfo> getDefaultProjectEngines() {
        return masterNodeConfig.getDefaultProjectEngines();
    }

    public Map<String, Map<String, DefaultTaskEngineInfo>> getDefaultTaskEngines() {
        return masterNodeConfig.getDefaultTaskEngines();
    }

    public FileSender getFileSender() {
        return fileSender;
    }

    public void setFileSender(FileSender fileSender) {
        this.fileSender = fileSender;
    }

    public static MasterApplication getApplication() {
        return application;
    }

    public String getRootPath(){
        return getMasterNodeConfig().getRootPath();
    }
}
