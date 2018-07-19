package org.iceslab.frobot.remoting.delegate;

import org.apache.log4j.Logger;
import org.iceslab.frobot.remoting.*;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.exception.RemotingException;
import org.iceslab.frobot.remoting.netty.NettyRemotingServer;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import javax.annotation.processing.Processor;
import java.util.concurrent.ExecutorService;

/**
 * 通信服务代理对象
 */
public class RemotingServerDelegate {
    /* 日志 */
    private static final Logger LOGGER = Logger.getLogger(RemotingServerDelegate.class);
    /* 真正通信的实体对象 默认底层采用NettyRPC框架 */
    private RemotingServer remotingServer;
    /* 通信服务端配置信息 */
    private RemotingServerConfig config;

    /**
     * 构造函数
     */
    public RemotingServerDelegate() {
        config = new RemotingServerConfig();
        remotingServer = new NettyRemotingServer(config);
    }

    /**
     * 构造函数
     */
    public RemotingServerDelegate(RemotingServer remotingServer) {
        this.remotingServer = remotingServer;
    }

    public RemotingServerDelegate(RemotingServerConfig config) {
        this.config = config;
        remotingServer = new NettyRemotingServer(config);
    }

    public RemotingServerDelegate(RemotingServerConfig config,
                                  ChannelEventListener listener) {
        this.config = config;
        this.remotingServer = new NettyRemotingServer(config, listener);
    }

    public void start() throws RemotingException {
        remotingServer.start();
    }

    public void registerServerProcessor(int requestCode, RemotingProcessor processor, ExecutorService executor) {
        remotingServer.registerServerProcessor(requestCode, processor, executor);
    }

    public void registerDefaultProcessor(RemotingProcessor processor, ExecutorService executor) {
        remotingServer.registerDefaultProcessor(processor, executor);
    }

    public RemotingCommand invokeSync(Channel channel, RemotingCommand request) {
        try {
            return remotingServer.invokeSync(channel, request, config.getInvokeTimeoutMillis());
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    public void invokeAsync(Channel channel, RemotingCommand request, AsyncCallback asyncCallback) {
        try {
            remotingServer.invokeAsync(channel, request, config.getInvokeTimeoutMillis(), asyncCallback);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void invokeOneway(Channel channel, RemotingCommand request) {
        try {
            remotingServer.invokeOneway(channel, request, config.getInvokeTimeoutMillis());
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void shutdown() {
        remotingServer.shutdown();
    }

    public int getListenPort() {
        return config.getListenPort();
    }

    public int getServerWorkerThreads() {
        return config.getServerWorkerThreads();
    }

    public int getServerCallbackExecutorThreads() {
        return config.getServerCallbackExecutorThreads();
    }

    public int getServerSelectorThreads() {
        return config.getServerSelectorThreads();
    }

    public int getServerOneWaySemaphoreValue() {
        return config.getServerOnewaySemaphoreValue();
    }

    public int getServerAsyncSemaphoreValue() {
        return config.getServerAsyncSemaphoreValue();
    }

    public int getReaderIdleTimeSeconds() {
        return config.getReaderIdleTimeSeconds();
    }

    public int getWriterIdleTimeSeconds() {
        return config.getWriterIdleTimeSeconds();
    }

    public int getServerChannelMaxIdleTimeSeconds() {
        return config.getServerChannelMaxIdleTimeSeconds();
    }

    public int getInvokeTimeoutMillis() {
        return config.getInvokeTimeoutMillis();
    }
}
