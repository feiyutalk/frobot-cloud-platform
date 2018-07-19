package org.iceslab.frobot.cloudserver;

import org.iceslab.frobot.cluster.NodeConfig;
import org.iceslab.frobot.cluster.NodeType;

import java.io.File;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Neuclil on 17-4-11.
 */
public class CloudServerNodeConfig extends NodeConfig {
    public static final String DEFAULT_WORKSPACE_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_cloudserver" + File.separator
                    + "workspace";

    private static final  String DEFAULT_IDENTITY_PREFIX = "cloudserver-";

    public static final String DEFAULT_CLUSTER_NAME = "frobot_cluster";
    private int dataPort;

    private CloudServerNodeConfig(Builder builder){
        this.available = builder.available;
        this.ip = builder.ip;
        this.commandPort = builder.commandPort;
        this.dataPort = builder.dataPort;
        this.identity = builder.identity;
        this.nodeType = builder.nodeType;
        this.invokeTimeoutMillis = builder.invokeTimeoutMillis;
        this.workspacePath = builder.workspacePath;
        this.clusterName = builder.clusterName;
    }

    public static class Builder implements Serializable{
        private boolean available = true;
        private String ip = "localhost";
        private int commandPort = 8000;
        private int dataPort = 8001;
        private String identity = CloudServerNodeConfig.DEFAULT_IDENTITY_PREFIX + UUID.randomUUID();
        private NodeType nodeType = NodeType.CLOUD_SERVER;
        private int invokeTimeoutMillis = 10 * 60 * 1000;
        private String workspacePath = CloudServerNodeConfig.DEFAULT_WORKSPACE_PATH;
        private String clusterName = CloudServerNodeConfig.DEFAULT_CLUSTER_NAME;

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

        public CloudServerNodeConfig build(){
            return new CloudServerNodeConfig(this);
        }
    }

    /************************* 	Getter & Setter	*************************/
    public int getDataPort() {
        return dataPort;
    }

    public void setDataPort(int dataPort) {
        this.dataPort = dataPort;
    }
}
