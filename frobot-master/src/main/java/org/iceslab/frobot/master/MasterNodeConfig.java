package org.iceslab.frobot.master;

import org.iceslab.frobot.cluster.NodeConfig;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.commons.constants.Constants;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 * Created by Neuclil on 17-4-11.
 */
public class MasterNodeConfig extends NodeConfig {
    public static final String DEFAULT_ROOT_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_master";

    public static final String DEFAULT_WORKSPACE_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_master" + File.separator
                    + "workspace";

    public static final String DEFAULT_PROJECT_ENGINE_PATH =
            MasterNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                    + "frobot" + File.separator
                    + "lib" + File.separator
                    + "project_engine";

    public static final String DEFAULT_TASK_ENGINE_PATH =
            MasterNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                    + "frobot" + File.separator
                    + "lib" + File.separator
                    + "task_engine";



    private static final long serialVersionUID = 5146437425073232081L;
    /* 传输数据端口 */
    private int dataPort;
    /* 注册中心地址 */
    private String registryAddress;
    /* projectEngine 文件目录 */
    private String projectEnginePath;
    /* taskEngine 文件目录 */
    private String taskEnginePath;
    /* 默认的ProjectEngine配置信息 */
    private Map<String, DefaultProjectEngineInfo> defaultProjectEngines;
    /* 默认的TaskEngine配置信息 */
    private Map<String, Map<String, DefaultTaskEngineInfo>> defaultTaskEngines;

    private MasterNodeConfig(Builder builder) {
        this.available = builder.available;
        this.ip = builder.ip;
        this.commandPort = builder.commandPort;
        this.dataPort = builder.dataPort;
        this.identity = builder.identity;
        this.nodeType = builder.nodeType;
        this.registryAddress = builder.registryAddress;
        this.rootPath = builder.rootPath;
        this.workspacePath = builder.workspacePath;
        this.projectEnginePath = builder.projectEnginePath;
        this.taskEnginePath = builder.taskEnginePath;
        this.invokeTimeoutMillis = builder.invokeTimeoutMillis;
        this.clusterName = builder.clusterName;
        this.defaultProjectEngines = builder.defaultProjectEngines;
        this.defaultTaskEngines = builder.defaultTaskEngines;
    }

    public static class Builder implements Serializable {
        private boolean available = true;
        private String ip = "localhost";
        private int commandPort = 8000;
        private int dataPort = 8001;
        private String identity = "unregister";
        private NodeType nodeType = NodeType.MASTER;
        private String registryAddress = "localhost";
        private String rootPath = MasterNodeConfig.DEFAULT_ROOT_PATH;
        private String workspacePath = MasterNodeConfig.DEFAULT_WORKSPACE_PATH;
        private String projectEnginePath = MasterNodeConfig.DEFAULT_PROJECT_ENGINE_PATH;
        private String taskEnginePath = MasterNodeConfig.DEFAULT_TASK_ENGINE_PATH;
        private int invokeTimeoutMillis = 10 * 60 * 1000;
        private String clusterName = Constants.DEFAULT_CLUSTER_NAME;
        private Map<String, DefaultProjectEngineInfo> defaultProjectEngines;
        private Map<String, Map<String, DefaultTaskEngineInfo>> defaultTaskEngines;

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

        public Builder dataPort(int dataPort) {
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

        public Builder invokeTimeoutMillis(int invokeTimeoutMillis) {
            this.invokeTimeoutMillis = invokeTimeoutMillis;
            return this;
        }

        public Builder rootPath(String rootPath){
            this.rootPath = rootPath;
            return this;
        }

        public Builder projectEnginePath(String projectEnginePath) {
            this.projectEnginePath = projectEnginePath;
            return this;
        }

        public Builder taskEnginePath(String taskEnginePath) {
            this.taskEnginePath = taskEnginePath;
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

        public Builder defaultProjectEngines(Map<String, DefaultProjectEngineInfo> defaultProjectEngines) {
            this.defaultProjectEngines = defaultProjectEngines;
            return this;
        }

        public Builder defaultTaskEngines(Map<String, Map<String, DefaultTaskEngineInfo>> defaultTaskEngines) {
            this.defaultTaskEngines = defaultTaskEngines;
            return this;
        }

        public MasterNodeConfig build() {
            return new MasterNodeConfig(this);
        }
    }

    /************************* 	Getter & Setter	*************************/

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public Map<String, DefaultProjectEngineInfo> getDefaultProjectEngines() {
        return defaultProjectEngines;
    }

    public void setDefaultProjectEngines(Map<String, DefaultProjectEngineInfo> defaultProjectEngines) {
        this.defaultProjectEngines = defaultProjectEngines;
    }

    public Map<String, Map<String, DefaultTaskEngineInfo>> getDefaultTaskEngines() {
        return defaultTaskEngines;
    }

    public void setDefaultTaskEngines(Map<String, Map<String, DefaultTaskEngineInfo>> defaultTaskEngines) {
        this.defaultTaskEngines = defaultTaskEngines;
    }

    public int getDataPort() {
        return dataPort;
    }

    public void setDataPort(int dataPort) {
        this.dataPort = dataPort;
    }

    public String getProjectEnginePath() {
        return projectEnginePath;
    }

    public void setProjectEnginePath(String projectEnginePath) {
        this.projectEnginePath = projectEnginePath;
    }

    public String getTaskEnginePath() {
        return taskEnginePath;
    }

    public void setTaskEnginePath(String taskEnginePath) {
        this.taskEnginePath = taskEnginePath;
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
                ", defaultProjectEngines=" + defaultProjectEngines +
                ", defaultTaskEngines=" + defaultTaskEngines +
                '}';
    }
}
