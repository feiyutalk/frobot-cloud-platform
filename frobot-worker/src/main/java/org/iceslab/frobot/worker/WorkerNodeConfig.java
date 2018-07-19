package org.iceslab.frobot.worker;

import org.iceslab.frobot.cluster.NodeConfig;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.commons.constants.Constants;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Neuclil on 17-4-11.
 */
public class WorkerNodeConfig extends NodeConfig {
    public static final String DEFAULT_ROOT_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_worker";

    public static final String DEFAULT_WORKSPACE_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_worker" + File.separator
                    + "workspace";

    public static final String DEFAULT_TASK_ENGINE_PATH =
            WorkerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                    + "frobot" + File.separator
                    + "lib" + File.separator
                    + "task_engine";

    private static final  int DEFAULT_RETRY_TIMES = 5;

    private static final String DEFAULT_IDENTITY_PREFIX = "worker-";

    private int dataPort;

    private String registryAddress;

    private String taskEnginePath;

    private int retryTimes;

    private WorkerNodeConfig(Builder builder){
        this.identity = builder.identity;
        this.nodeType = builder.nodeType;
        this.available = builder.available;
        this.clusterName = builder.clusterName;
        this.ip = builder.ip;
        this.commandPort = builder.commandPort;
        this.dataPort = builder.dataPort;
        this.registryAddress = builder.registryAddress;
        this.invokeTimeoutMillis = builder.invokeTimeoutMillis;
        this.rootPath = builder.rootPath;
        this.workspacePath = builder.workspacePath;
        this.taskEnginePath = builder.taskEnginePath;
        this.retryTimes = builder.retryTimes;
    }

   public static class Builder implements Serializable{
       private String identity = WorkerNodeConfig.DEFAULT_IDENTITY_PREFIX + UUID.randomUUID();
       private NodeType nodeType = NodeType.WORKER;
       private boolean available = true;
       private String clusterName = Constants.DEFAULT_CLUSTER_NAME;
       private String ip = "localhost";
       private int commandPort = 8000;
       private int dataPort = 8001;
       private String registryAddress = "localhost";
       private int invokeTimeoutMillis = 10 * 60 * 1000;
       private String rootPath = WorkerNodeConfig.DEFAULT_ROOT_PATH;
       private String workspacePath = WorkerNodeConfig.DEFAULT_WORKSPACE_PATH;
       private String taskEnginePath = WorkerNodeConfig.DEFAULT_TASK_ENGINE_PATH;
       private int retryTimes = WorkerNodeConfig.DEFAULT_RETRY_TIMES;

       public Builder() {
       }

       public Builder available(boolean available) {
           this.available = available;
           return this;
       }

       public Builder ip(String ip) {
           this.ip = ip;
           return this;
       }

       public Builder commandPort(int commandPort) {
           this.commandPort = commandPort;
           return this;
       }

       public Builder dataPort(int dataPort){
           this.dataPort = dataPort;
           return this;
       }

       public Builder identity(String identity) {
           this.identity = identity;
           return this;
       }

       public Builder nodeType(NodeType nodeType) {
           this.nodeType = nodeType;
           return this;
       }

       public Builder registryAddress(String registryAddress) {
           this.registryAddress = registryAddress;
           return this;
       }

       public Builder rootPath(String rootPath){
           this.rootPath = rootPath;
           return this;
       }

       public Builder taskEnginePath(String taskEnginePath){
           this.taskEnginePath = taskEnginePath;
           return this;
       }

       public Builder retryTimes(int retryTimes){
           this.retryTimes = retryTimes;
           return this;
       }

       public Builder invokeTimeoutMillis(int invokeTimeoutMillis) {
           this.invokeTimeoutMillis = invokeTimeoutMillis;
           return this;
       }

       public Builder workspacePath(String workspacePath) {
           this.workspacePath = workspacePath;
           return this;
       }

       public Builder clusterName(String clusterName) {
           this.clusterName = clusterName;
           return this;
       }

       public WorkerNodeConfig build(){
           return new WorkerNodeConfig(this);
       }
   }

   /************************* 	Getter & Setter	*************************/
    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public int getDataPort() {
        return dataPort;
    }

    public void setDataPort(int dataPort) {
        this.dataPort = dataPort;
    }

    public String getTaskEnginePath() {
        return taskEnginePath;
    }

    public void setTaskEnginePath(String taskEnginePath) {
        this.taskEnginePath = taskEnginePath;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    @Override
    public String toString() {
        return "NodeConfig{" +
                "available=" + available +
                ", ip='" + ip + '\'' +
                ", commandPort=" + commandPort +
                ", dataPort=" + dataPort +
                ", registryAddress" + registryAddress +
                ", identity='" + identity + '\'' +
                ", nodeType=" + nodeType +
                ", invokeTimeoutMillis=" + invokeTimeoutMillis +
                ", workspacePath='" + workspacePath + '\'' +
                ", clusterName='" + clusterName + '\'' +
                '}';
    }
}
