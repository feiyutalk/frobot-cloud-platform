package org.iceslab.frobot.registry;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.NodeConfig;
import org.iceslab.frobot.cluster.NodeInitializedException;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.registry.processor.RegistryRemotingDispatcher;
import org.iceslab.frobot.commons.constants.Constants;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.support.Pair;
import org.iceslab.frobot.commons.utils.general.RemotingHelper;
import org.iceslab.frobot.remoting.ChannelHandlerListener;
import org.iceslab.frobot.remoting.Future;
import org.iceslab.frobot.remoting.RemotingRegisterConfig;
import org.iceslab.frobot.remoting.codec.Codec;
import org.iceslab.frobot.remoting.codec.FrobotCodec;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.netty.NettyChannel;
import org.iceslab.frobot.remoting.netty.NettyCodecFactory;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Registry is a center for master to register and for worker to find
 * available master to connect.
 *
 * @author Neuclil
 */
public class Registry {
    private static final Logger LOGGER = Logger.getLogger(Registry.class);

    private static final String DEFAULT_CONFIG_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_registry" + File.separator
                    + "registryconfig.xml";

    private RegistryRemotingDelegate registryRemoting;

    private RegistryNodeConfig nodeConfig;

    private RegistryApplication application;

    private AtomicBoolean inited = new AtomicBoolean(false);

    private AtomicBoolean started = new AtomicBoolean(false);

    private String configPath = Registry.DEFAULT_CONFIG_PATH;

    public Registry(RemotingRegisterConfig remotingRegisterConfig) {
        application = RegistryApplication.getInstance();
    }

    public void start() {
        init();
        startService();
    }


    protected void init() {
        try {
            if (inited.compareAndSet(false, true)) {
                LOGGER.debug("registry start to initialize.");

                initConfig();

                initRemoting();

                LOGGER.debug("registry initialize successfully.");
            }
        } catch (Exception e) {
            LOGGER.error("registry initialized error!", e);
            System.exit(-1);
        }
    }

    protected void initConfig() throws NodeInitializedException {
        LOGGER.debug("registry start to initialize config.");
        Map<String, String> registryConfig = null;
        try {
            registryConfig = ConfigParser.parseRegistryConfig(configPath);
        } catch (XMLFileNotMatchException e) {
            LOGGER.error("parse registry config error", e);
            throw new NodeInitializedException();
        }
        RegistryNodeConfig.Builder build = new RegistryNodeConfig.Builder();
        nodeConfig = build.ip(registryConfig.get("ip"))
                .commandPort(Integer.valueOf(registryConfig.get("commandPort")))
                .registerPort(Integer.valueOf(registryConfig.get("registerPort")))
                .invokeTimeoutMillis(Integer.parseInt(registryConfig.get("invokeTimeoutMillis")))
                .identity(UUID.randomUUID().toString())
                .workspacePath(registryConfig.get("workspacePath"))
                .nodeType(NodeType.REGISTRY)
                .clusterName(Constants.DEFAULT_CLUSTER_NAME)
                .build();
        application.setNodeConfig(nodeConfig);
        LOGGER.debug("registry initialized config successfully." + nodeConfig);
    }

    /**
     * initialized remote connection server. The default remote connection frame
     * is netty and the default code/decode is java code/decode. the initialized
     * method set connection parameters to the remote server delegate.
     */
    protected void initRemoting() {
        LOGGER.info("registry start to initialize remote connection server.");
        RemotingRegisterConfig remotingRegisterConfig = new RemotingRegisterConfig();
        if (nodeConfig.getCommandPort() == 0) {
            nodeConfig.setCommandPort(Constants.REGISTRY_DEFAULT_LISETN_PORT);
        }
        remotingRegisterConfig.setNettyPort(nodeConfig.getCommandPort());
        if (nodeConfig.getRegisterPort() == 0) {
            nodeConfig.setRegisterPort(Constants.REGISTRY_DEFAULT_REGISTER_PORT);
        }
        remotingRegisterConfig.setSocketPort(nodeConfig.getRegisterPort());

        this.registryRemoting = new RegistryRemotingDelegate(remotingRegisterConfig);
        LOGGER.info("registry initialized remote successfully." + remotingRegisterConfig);
    }

    /**
     * Start the registry service. the method should be called after initialized
     * successful.
     * If the start service process throws exception, then the program will exit.
     */
    protected void startService() {
        try {
            if (started.compareAndSet(false, true)) {
                LOGGER.info("registry start to start service.");

                startMasterManager();

                startRemoting();

                LOGGER.info("registry start service successfully!");
            }
        } catch (Exception e) {
            LOGGER.error("registry start service error!", e);
            System.exit(-1);
        }
    }

    protected void startMasterManager() {
        LOGGER.info("registry begin to start master manager.");
        application.setMasterManager(new MasterManager());
        application.getMasterManager().start();
        LOGGER.info("registry start master manager successful!");
    }

    protected void startRemoting() {
        try {

            LOGGER.info("registry begin to start remote service.");
            registryRemoting.start();
            LOGGER.info("registry start remote service successfully.");

        } catch (Exception e) {
            LOGGER.error("registry start remote service error", e);
            throw e;
        }
    }

    public RemotingProcessor getDefaultProcessor() {
        return new RegistryRemotingDispatcher(application);
    }


    private class RegistryRemotingDelegate {
        private ServerBootstrap serverBootstrap;
        /*Netty线程Reactor线程模型需要的两个线程池*/
        private EventLoopGroup bossSelectorGroup;
        private EventLoopGroup workerSelectorGroup;
        /*通信服务Server端的配置信息*/
        protected final RemotingRegisterConfig remotingRegisterConfig;
        /*Master注册时连接的ServerSoket*/
        private ServerSocket serverSocket;
        /*RPC处理器表，针对每种RPC指令，可以注册一个处理器，如果不注册，则采用默认处理器处理. 为每个处理器分配一个线程池，用来处理请求*/
        protected final HashMap<Integer, Pair<RemotingProcessor, ExecutorService>> processorTable =
                new HashMap<Integer, Pair<RemotingProcessor, ExecutorService>>(64);
        /*默认的RPC处理器*/
        protected Pair<RemotingProcessor, ExecutorService> defaultRequestProcessor;

        public RegistryRemotingDelegate(RemotingRegisterConfig remotingRegisterConfig) {
            this.serverBootstrap = new ServerBootstrap();
            this.bossSelectorGroup = new NioEventLoopGroup(1);
            this.workerSelectorGroup = new NioEventLoopGroup(remotingRegisterConfig.getRegisterSelectorThreads());
            this.remotingRegisterConfig = remotingRegisterConfig;
        }

        /**
         * 处理请求，具体步骤如下
         * 1. 从处理器Map中取出该request指令的处理器，如果没有，用默认的处理器
         * 2. 新建一个线程，该线程负责将指令交给处理器去做。
         */
        private void processRequestCommand(final org.iceslab.frobot.remoting.Channel channel,
                                           final RemotingCommand cmd) {
            /**
             * 根据通信请求的命令从处理器表中取出对应的处理器，可能为空，如果为空
             * 则使用默认的处理器
             */
            final Pair<RemotingProcessor, ExecutorService> matched = this.processorTable
                    .get(cmd.getCode());
            final Pair<RemotingProcessor, ExecutorService> pair = null == matched
                    ? this.defaultRequestProcessor : matched;
            if (pair != null) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            final RemotingCommand response = pair.getKey()
                                    .processRequest(channel, cmd);
                            if (!RemotingHelper.isOnewayCommand(cmd)) {
                                if (response != null) {
                                    response.setOpaque(cmd.getOpaque());
                                    try {
                                        channel.writeAndFlush(response).addListener(
                                                new ChannelHandlerListener() {

                                                    @Override
                                                    public void operationComplete(
                                                            Future future)
                                                            throws Exception {
                                                        if (!future.isSuccess()) {
                                                            LOGGER.error(
                                                                    "发送通信响应到"
                                                                            + RemotingHelper
                                                                            .parseChannelRemoteAddr(
                                                                                    channel)
                                                                            + "失败",
                                                                    future.cause());
                                                            LOGGER.error(
                                                                    cmd.toString());
                                                            LOGGER.error(response
                                                                    .toString());
                                                        }
                                                    }
                                                });
                                    } catch (Exception e) {
                                        LOGGER.error("处理请求结束，但是发送响应失败", e);
                                        LOGGER.error(cmd.toString());
                                        LOGGER.error(response.toString());
                                    }
                                } else {
                                    //收到了请求，但是没有给予反馈，可能有一些请求不需要反馈
                                }
                            }
                        } catch (Exception e) {
                            LOGGER.error("处理请求出现异常", e);
                            LOGGER.error(cmd.toString());

                        /*如果是单向的RPC，就不反馈 否则反馈处理器处理请求错误*/
                            if (!RemotingHelper.isOnewayCommand(cmd)) {
                                final RemotingCommand response = RemotingCommand
                                        .createResponseCommand(
                                                RemotingProtos.ResponseCode.COMMAND_PROCESS_ERROR
                                                        .code(),
                                                e.toString());
                                response.setOpaque(cmd.getOpaque());
                                channel.writeAndFlush(response);
                            }
                        }
                    }
                };

                try {
                /* 将Runnable任务提交给线程池处理 */
                    pair.getValue().submit(run);
                } catch (RejectedExecutionException e) {
                /* 线程池拒绝执行任务，可能线程池处于饱和状态 */
                    LOGGER.warn(RemotingHelper.parseChannelRemoteAddr(channel)
                            + ", 任务过多，处理器线程池处于饱和，拒绝执行该任务, RejectedExecutionException "
                            + pair.getKey().toString() + " 请求指令: " + cmd.getCode());
                    if (!RemotingHelper.isOnewayCommand(cmd)) {
                        final RemotingCommand response = RemotingCommand
                                .createResponseCommand(
                                        RemotingProtos.ResponseCode.SYSTEM_BUSY
                                                .code(),
                                        "该Master的处理器处于饱和，请尝试连接其他处理器");
                        response.setOpaque(cmd.getOpaque());
                        channel.writeAndFlush(response);
                    }
                }
            } else {
            /* 处理器为空，一般不会出现这种情况 */
                String error = " 请求指令 " + cmd.getCode() + " 不支持";
                final RemotingCommand response = RemotingCommand
                        .createResponseCommand(
                                RemotingProtos
                                        .ResponseCode
                                        .REQUEST_CODE_NOT_SUPPORTED
                                        .code(),
                                error);
                response.setOpaque(cmd.getOpaque());
                channel.writeAndFlush(response);
                LOGGER.error(
                        RemotingHelper.parseChannelRemoteAddr(channel) + error);
            }
        }

        /**
         * 关闭通信端服务，主要有:
         * 1. 关闭定时器
         * 2. 关闭事件处理器
         * 3. 关闭RPC
         * 4. 关闭公用线程池
         */
        public void shutdown() {
            try {
                this.bossSelectorGroup.shutdownGracefully();
            } catch (Exception e) {
                LOGGER.error("注册中心关闭异常", e);
            }
        }

        /**
         * 根据通信命令注册响应的处理器
         */
        private void registerProcessor(int requestCode, RemotingProcessor processor, ExecutorService executor) {
            ExecutorService executorThis = executor;
            if (null == executor) {
                executorThis = Executors.newCachedThreadPool();
            }

            Pair<RemotingProcessor, ExecutorService> pair = new Pair<RemotingProcessor, ExecutorService>(processor,
                    executorThis);
            this.processorTable.put(requestCode, pair);
        }

        private Codec getCodec() {
            return new FrobotCodec();
        }

        public void registerDefaultProcessor(RemotingProcessor processor,
                                             ExecutorService executor) {
            this.defaultRequestProcessor = new Pair<RemotingProcessor, ExecutorService>(processor, executor);
        }

        /**
         * Netty 通信处理Handler
         *
         * @author Neuclil
         */
        class NettyServerHandler extends SimpleChannelInboundHandler<RemotingCommand> {
            @Override
            protected void channelRead0(ChannelHandlerContext ctx, RemotingCommand cmd) throws Exception {
                if (RemotingHelper.isRequestCommand(cmd)) {
                    LOGGER.debug("收到请求指令:" + RemotingProtos.RequestCode.valueOf(cmd.getCode()));
                    processRequestCommand(new NettyChannel(ctx), cmd);
                } else {
                    LOGGER.warn("收到通信响应消息！");
                }
            }
        }

        /**
         * Netty 连接处理Handler
         *
         * @author Neuclil
         */
        class NettyConnectManageHandler extends ChannelDuplexHandler {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                org.iceslab.frobot.remoting.Channel channel = new NettyChannel(ctx);
                String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
                LOGGER.debug("Worker连接成功: " + remoteAddress);
                super.channelActive(ctx);
            }

            @Override
            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                org.iceslab.frobot.remoting.Channel channel = new NettyChannel(ctx);
                String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
                LOGGER.info("Worker断开链接: " + remoteAddress);
                super.channelInactive(ctx);
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                org.iceslab.frobot.remoting.Channel channel = new NettyChannel(ctx);
                String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
                LOGGER.warn("SERVER: exceptionCaught," + remoteAddress);
                LOGGER.warn("SERVER: exceptionCaught exception." + cause);
                super.exceptionCaught(ctx, cause);
                RemotingHelper.closeChannel(channel);
            }

            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                if (evt instanceof IdleStateEvent) {
                    IdleStateEvent event = (IdleStateEvent) evt;
                    org.iceslab.frobot.remoting.Channel channel = new NettyChannel(ctx);
                    String remoteAddress = RemotingHelper.parseChannelRemoteAddr(channel);
                    if (event.state().equals(io.netty.handler.timeout.IdleState.ALL_IDLE)) {
                        ChannelFuture future = ctx.channel().close();
                    }
                }
                ctx.fireUserEventTriggered(evt);
            }
        }

        public void start() {
            nettyStart();

            RemotingProcessor defaultProcessor = getDefaultProcessor();
            if (defaultProcessor != null) {
                registryRemoting.registerDefaultProcessor(defaultProcessor,
                        Executors.newFixedThreadPool(Constants.DEFAULT_PROCESSOR_THREAD));
            }
            LOGGER.debug("Registry通信服务开启成功!");

            try {
                serverSocket = new ServerSocket();
                serverSocket.bind(new InetSocketAddress(this.remotingRegisterConfig.getSocketPort()));
            } catch (IOException e) {
                LOGGER.error("Registry通信服务启动失败!", e);
            }
            try {
                while (true) {
                    Socket master = serverSocket.accept();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            handle(master);
                        }
                    }).start();
                }
            } catch (Exception e) {
                LOGGER.error("Master注册出错!", e);
            }
        }

        /**
         * 启动通信服务，主要有:
         * 1. 启动通信传输这一块功能
         * 2. 启动事件监听器 （未使用）
         * 3. 启动responseTable扫描定时器
         */
        private void nettyStart() {
        /*编码解码器工厂类，该类可以获得编码解码器，默认的编码解码器采用Java序列化方式*/
            final NettyCodecFactory nettyCodecFactory = new NettyCodecFactory(getCodec());
        /*Netty引导程序启动流程*/
            this.serverBootstrap.group(this.bossSelectorGroup, this.workerSelectorGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 65535)//最大连接数量
                    .localAddress(new InetSocketAddress(this.remotingRegisterConfig.getNettyPort()))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(nettyCodecFactory.getEncoder())//注册编码器
                                    .addLast(nettyCodecFactory.getDecoder())//注册解码器
                                    .addLast(new IdleStateHandler(remotingRegisterConfig.getReaderIdleTimeSeconds(),
                                            remotingRegisterConfig.getWriterIdleTimeSeconds(),
                                            remotingRegisterConfig.getServerChannelMaxIdleTimeSeconds()))//注册事件监听器
                                    .addLast(new NettyConnectManageHandler())//注册连接处理器
                                    .addLast(new NettyServerHandler());//注册服务端处理器
                        }

                    });
            try {
                this.serverBootstrap.bind().sync();
                LOGGER.debug("Register通信服务启动");
                LOGGER.debug("Register通信服务配置 : " + remotingRegisterConfig);
            } catch (Exception e) {
                LOGGER.error("Register启动失败!");
            }
        }

        private void handle(Socket socket) {
            ObjectInputStream ois = null;
            PrintWriter pw = null;
            try {
                LOGGER.debug("Master开始注册....");
                ois = new ObjectInputStream(socket.getInputStream());
                pw = new PrintWriter(socket.getOutputStream(), true);

                NodeConfig config = (NodeConfig) ois.readObject();
                String addr = RemotingHelper.ipAndPort2Addr(
                        config.getIp(),
                        config.getCommandPort());

                String id = application.getMasterManager().register(addr, config);

                pw.println(id);

                LOGGER.debug("Master注册成功！");
                LOGGER.debug("Master注册时配置信息:" + config);
            } catch (Exception e) {
                LOGGER.error("注册失败！", e);
            } finally {
                try {
                    if (ois != null)
                        ois.close();
                    if (socket != null)
                        socket.close();
                } catch (IOException e) {
                    LOGGER.error("Register关闭连接异常！", e);
                }
            }
        }
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public RegistryNodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public RegistryApplication getApplication() {
        return application;
    }

    public static void main(String[] args) {
        Registry registry =
                new Registry(new RemotingRegisterConfig());
        registry.start();
    }
}
