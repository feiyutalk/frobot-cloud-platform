package org.iceslab.frobot.cluster;

import java.io.Serializable;

/**
 * Created by Neuclil on 17-4-11.
 */
public abstract class NodeConfig implements Serializable{
	private static final long serialVersionUID = -6359874276599173321L;
	/* 是否可用 */
    public boolean available = true;
    /* 通信服务ip */
    public String ip;
    /* 通信服务命令端口*/
    public int commandPort;
    /* 节点唯一标识 */
    public String identity;
    /* 节点类型 */
    public NodeType nodeType;
    /* 请求超时时间*/
    public int invokeTimeoutMillis;
    /* root path */
    public String rootPath;
    /* 系统的workspace路径*/
    public String workspacePath;
    /* 系统集群名*/
    public String clusterName;

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getCommandPort() {
        return commandPort;
    }

    public void setCommandPort(int commandPort) {
        this.commandPort = commandPort;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public NodeType getNodeType() {
        return nodeType;
    }

    public void setNodeType(NodeType nodeType) {
        this.nodeType = nodeType;
    }

    public int getInvokeTimeoutMillis() {
        return invokeTimeoutMillis;
    }

    public void setInvokeTimeoutMillis(int invokeTimeoutMillis) {
        this.invokeTimeoutMillis = invokeTimeoutMillis;
    }

    public String getWorkspacePath() {
        return workspacePath;
    }

    public void setWorkspacePath(String workspacePath) {
        this.workspacePath = workspacePath;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

}
