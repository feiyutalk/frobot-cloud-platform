package org.iceslab.frobot.registry;

import org.iceslab.frobot.cluster.NodeConfig;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.commons.constants.Constants;

import java.io.File;
import java.util.UUID;

/**
 * Created by Neuclil on 17-4-12.
 */
public class RegistryNodeConfig extends NodeConfig {
    private static final String DEFAULT_IDENTITY_PREFIX = "registry-";
    private static final String DEFAULT_WORKSPACE_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_registry" + File.separator
                    + "workspace";
    private int registerPort;

    private RegistryNodeConfig(Builder builder){
        this.available = builder.available;
        this.ip = builder.ip;
        this.commandPort = builder.commandPort;
        this.registerPort = builder.registerPort;
        this.identity = builder.identity;
        this.nodeType = builder.nodeType;
        this.invokeTimeoutMillis = builder.invokeTimeoutMillis;
        this.workspacePath = builder.workspacePath;
        this.clusterName = builder.clusterName;
    }

    public static class Builder{
        private boolean available = true;
        private String ip = "localhost";
        private int commandPort = 8000;
        private int registerPort = 8002;
        private String identity = RegistryNodeConfig.DEFAULT_IDENTITY_PREFIX + UUID.randomUUID();
        private NodeType nodeType = NodeType.REGISTRY;
        private int invokeTimeoutMillis = 10 * 60 * 1000;
        private String workspacePath = RegistryNodeConfig.DEFAULT_WORKSPACE_PATH;
        private String clusterName = Constants.DEFAULT_CLUSTER_NAME;

        public Builder() {}

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

        public Builder registerPort(int registerPort){
            this.registerPort = registerPort;
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

        public RegistryNodeConfig build(){
           return new RegistryNodeConfig(this);
        }
    }

    /************************* 	Getter & Setter	*************************/

    public int getRegisterPort() {
        return registerPort;
    }

    public void setRegisterPort(int registerPort) {
        this.registerPort = registerPort;
    }

    @Override
    public String toString() {
        return "NodeConfig{" +
                "available=" + available +
                ", ip='" + ip + '\'' +
                ", commandPort=" + commandPort +
                ", registerPort=" + registerPort +
                ", identity='" + identity + '\'' +
                ", nodeType=" + nodeType +
                ", invokeTimeoutMillis=" + invokeTimeoutMillis +
                ", workspacePath='" + workspacePath + '\'' +
                ", clusterName='" + clusterName + '\'' +
                '}';
    }
}
