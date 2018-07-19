package org.iceslab.frobot.worker.manager;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.RemoteWorkerInfo;
import org.iceslab.frobot.commons.utils.SystemInfoUtil;
import org.iceslab.frobot.worker.WorkerApplication;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.CommandBodyWrapper;
import org.iceslab.frobot.remoting.command.body.request.HeartBeatRequestBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.delegate.RemotingClientDelegate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 心跳监控类,心跳包带有worker的系统信息
 */
public class HeartBeatMonitor {
    /* 日志 */
    private Logger LOGGER = Logger.getLogger(HeartBeatMonitor.class);
    /* Worker application*/
    private WorkerApplication application;
	/* 执行定时任务的线程池 */
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    /* 任务执行情况对象 */
    private java.util.concurrent.ScheduledFuture<?> pingScheduledFuture;
    /* 原子变量 防止多次启动pingStart */
    private AtomicBoolean pingStart = new AtomicBoolean(false);
    
    /**
     * 构造函数
     */
    public HeartBeatMonitor(WorkerApplication application) {
        this.application = application;
    }
    
    /**
     * 开始发送心跳,默认是10秒后每隔30秒发送一次心跳
     */
    public void start() {
        try {
            if (pingStart.compareAndSet(false, true)) {
                if (pingScheduledFuture == null) {
                    pingScheduledFuture = executor.scheduleWithFixedDelay(new Runnable() {
                        @Override
                        public void run() {
                            if (pingStart.get()) {
                                if(beat(application.getRemotingClient(), application.getMasterAddress())){
                                    LOGGER.debug("send heart beat successfully!");
                                }else{
                                    LOGGER.debug("send heart beat failed!");
                                }
                            }
                        }
                    }, 10, 30, TimeUnit.SECONDS);
                }
            }
        } catch (Exception e) {
            LOGGER.error("发送心跳包异常!", e);
        }
    }

    /**
     * 停止发送心跳
     */
    public void stop() {
        try {
            if (pingStart.compareAndSet(true, false)) {
                pingScheduledFuture.cancel(true);
                executor.shutdown();
            }
        } catch (Exception e) {
            LOGGER.error("停止发送心跳异常!", e);
        }
    }

    /**
     * 真正发送心跳的业务逻辑
     * 1. 先收集系统信息,封装成remoteWorkerInfo
     * 2. 创建通信消息,里面带有remoteWorkerInfo这个通信内容
     * @param remotingClient
     * @param addr
     * @return
     */
    protected boolean beat(RemotingClientDelegate remotingClient, String addr) {
        RemoteWorkerInfo remoteWorkerInfo = SystemInfoUtil.getWorkerInfo();
        LOGGER.debug("HeartBeatMonitor collect resource info:" + remoteWorkerInfo);
        LOGGER.debug("prepare to send heart beat...");
        HeartBeatRequestBody heartBeatRequestBody = CommandBodyWrapper.wrapper(
                application.getWorkerNodeConfig(),
                new HeartBeatRequestBody(remoteWorkerInfo));
        RemotingCommand request = RemotingCommand.createRequestCommand(RemotingProtos.RequestCode.HEART_BEAT.code(),
                heartBeatRequestBody);
        try {
            RemotingCommand response = remotingClient.getRemotingClient().invokeSync(addr, request, 5000);
            if (response != null && RemotingProtos.ResponseCode.HEART_BEAT_SUCCESS == RemotingProtos.ResponseCode
                    .valueOf(response.getCode())) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("发送心跳包异常!", e);
        }
        return false;
    }
}
