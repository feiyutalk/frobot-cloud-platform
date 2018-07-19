package org.iceslab.frobot.worker;

import org.iceslab.frobot.worker.manager.HeartBeatMonitor;
import org.iceslab.frobot.worker.manager.task.TaskReceiver;
import org.iceslab.frobot.remoting.delegate.RemotingClientDelegate;

/**
 * Worker的application 存放系统中的数据和对象
 */
public class WorkerApplication {
	/* Worker节点的配置信息 */
    private WorkerNodeConfig workerNodeConfig;
    /* 通信服务Client端代理 */
    private RemotingClientDelegate remotingClient;
    /* 心跳监视器 */
    private HeartBeatMonitor heartBeatMonitor;
    /* master地址 */
    private String masterAddress;
    /* TaskParser 负责接受并保存Task */
    private TaskReceiver taskReceiver;
    /* 连接Master的重试次数 */
    public final static int RETRY_TIMES = 5;
    
    private static final WorkerApplication application = new WorkerApplication();

    /************************* 	Getter & Setter	*************************/

    public WorkerNodeConfig getWorkerNodeConfig() {
        return workerNodeConfig;
    }

    public void setWorkerNodeConfig(WorkerNodeConfig workerNodeConfig) {
        this.workerNodeConfig = workerNodeConfig;
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public void setMasterAddress(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    public RemotingClientDelegate getRemotingClient() {
        return remotingClient;
    }

    public void setRemotingClient(RemotingClientDelegate remotingClient) {
        this.remotingClient = remotingClient;
    }

    public HeartBeatMonitor getHeartBeatMonitor() {
        return heartBeatMonitor;
    }

    public void setHeartBeatMonitor(HeartBeatMonitor heartBeatMonitor) {
        this.heartBeatMonitor = heartBeatMonitor;
    }

    public static int getRetryTimes() {
        return RETRY_TIMES;
    }

	public TaskReceiver getTaskReceiver() {
		return taskReceiver;
	}

	public void setTaskReceiver(TaskReceiver taskReceiver) {
		this.taskReceiver = taskReceiver;
	}

	public static WorkerApplication getInstance() {
		return application;
	}
    
}
