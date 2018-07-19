package org.iceslab.frobot.master;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.commons.constants.Constants;
import org.iceslab.frobot.commons.utils.db.SQLiteOperation;
import org.iceslab.frobot.master.manager.channel.WorkerManager;
import org.iceslab.frobot.master.manager.project.ProjectManager;
import org.iceslab.frobot.master.manager.project.ProjectParser;
import org.iceslab.frobot.master.manager.project.ProjectScheduler;
import org.iceslab.frobot.master.manager.resource.ResourcesManager;
import org.iceslab.frobot.master.processor.MasterRemotingDispatcher;
import org.iceslab.frobot.cluster.NodeInitializedException;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.io.FileSender;
import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.iceslab.frobot.commons.utils.general.StringUtils;

import org.iceslab.frobot.remoting.ChannelEventListener;
import org.iceslab.frobot.remoting.RemotingServerConfig;
import org.iceslab.frobot.remoting.delegate.RemotingServerDelegate;
import org.iceslab.frobot.remoting.exception.RemotingException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

/**
 * Master节点
 *
 * @author Neuclil
 */
public class Master {
    private static final String DEFAULT_MASTER_CONFIG_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_master" + File.separator
                    +"masterconfig.xml";

    private static final int DEFAULT_COMMAND_PORT = 8000;

    private static final int DEFAULT_PROCESSOR_THREAD = 32 +
            Runtime.getRuntime().availableProcessors() * 5;

    private static final String DEFAULT_IDENTITY_PREFIX = "frobot_";

    private static final Logger LOGGER = Logger.getLogger(Master.class);

    private MasterNodeConfig nodeConfig;

    public static MasterApplication application;

    private RemotingServerDelegate remotingServer;

    private ChannelEventListener channelEventListener;

    private AtomicBoolean inited = new AtomicBoolean(false);

    private AtomicBoolean started = new AtomicBoolean(false);

    private String configPath = Master.DEFAULT_MASTER_CONFIG_PATH;

    private FileSender fileSender;

    private WorkerManager workerManager;

    private ProjectParser projectParser;

    private ProjectManager projectManager;

    private ProjectScheduler projectScheduler;

    private ResourcesManager resourcesManager;

    public Master() {
        this.application = MasterApplication.getInstance();
    }

    public void start() {
        init();
        startService();
        startRegister();
    }

    /**
     * init the master node, including init config, init workspace,
     * init remote connect, init data base, init the application class.
     * If the initialize process throws exception, then the program will exit.
     */
    protected void init() {
        try {
            if (inited.compareAndSet(false, true)) {
                LOGGER.debug("master start to initialize.");

                initConfig();

                initWorkSpace();

                initRemoting();

                initDataBase();

                LOGGER.debug("master initialize successfully.");
            }
        } catch (Exception e) {
            LOGGER.error("master initialized error!", e);
            System.exit(-1);
        }
    }


    /**
     * init master node config information. The config will be read from
     * {$USER_HOME}/frobot_master/masterconfig.xml.
     */
    protected void initConfig() throws NodeInitializedException {
        LOGGER.info("master start to initialize config.");
        try {
            Map<String, String> basicConfig = ConfigParser.parseBasicConfig(configPath);
            Map<String, DefaultProjectEngineInfo> defaultProjectEngineInfoConfig =
                    ConfigParser.parseDefaultProjectEngineConfig(configPath);
            Map<String, Map<String, DefaultTaskEngineInfo>> defaultTaskEngineInfoConfig =
                    ConfigParser.parseDefaultTaskEngineConfig(configPath);

            MasterNodeConfig.Builder build = new MasterNodeConfig.Builder();
            nodeConfig = build.ip(basicConfig.get("ip"))
                    .commandPort(Integer.valueOf(basicConfig.get("commandPort")))
                    .dataPort(Integer.valueOf(basicConfig.get("dataPort")))
                    .registryAddress(basicConfig.get("registryAddress"))
                    .rootPath(basicConfig.get("rootPath"))
                    .workspacePath(basicConfig.get("workspacePath"))
                    .projectEnginePath(basicConfig.get("projectEnginePath"))
                    .taskEnginePath(basicConfig.get("taskEnginePath"))
                    .invokeTimeoutMillis(Integer.parseInt(basicConfig.get("invokeTimeoutMillis")))
                    .identity("unregistry")
                    .nodeType(NodeType.MASTER)
                    .clusterName(Constants.DEFAULT_CLUSTER_NAME)
                    .defaultProjectEngines(defaultProjectEngineInfoConfig)
                    .defaultTaskEngines(defaultTaskEngineInfoConfig)
                    .build();
        } catch (XMLFileNotMatchException e) {
            LOGGER.error("master parse node basic config error",e);
            throw new NodeInitializedException();
        }
        application.setMasterNodeConfig(nodeConfig);
        LOGGER.info("master initialized config successfully. " + nodeConfig);
    }

    /**
     * the work space directory, the project engine directory, the task engine
     * directory will be make.
     * the default work space directory is {$USER_HOME}/frobot_master/workspace,
     * which is used to store some information about projects and tasks.
     */
    protected void initWorkSpace() throws NodeInitializedException {
        LOGGER.debug("master start to initialize work space.");
        boolean success = FileManageUtil.generateDirectory(nodeConfig.getWorkspacePath());
        if (!success) {
            LOGGER.error("master initialize work space error.");
            throw new NodeInitializedException("master initialized work space failed.");
        }
        success = FileManageUtil.generateDirectory(nodeConfig.getProjectEnginePath());
        if (!success) {
            LOGGER.error("master initialize project engine path error.");
            throw new NodeInitializedException("master initialized project engine path failed.");
        }
        success = FileManageUtil.generateDirectory(nodeConfig.getTaskEnginePath());
        if (!success) {
            LOGGER.error("master initialize task engine path error.");
            throw new NodeInitializedException("master initialized task engine path failed");
        }
        LOGGER.debug("initialize work space directory successfully! work space path : " + nodeConfig.getWorkspacePath());
        LOGGER.debug("initialize project engine directory successfully! project engine path : " + nodeConfig.getProjectEnginePath());
        LOGGER.debug("initialize task engine directory successfully! task engine path : " + nodeConfig.getTaskEnginePath());
        LOGGER.debug("initialize work space successfully!");
    }

    /**
     * initialized remote connection server. The default remote connection frame
     * is netty and the default code/decode is java code/decode. the initialized
     * method set connection parameters to the remote server delegate.
     */
    protected void initRemoting() {
        LOGGER.info("master start to initialize remote connection server.");
        RemotingServerConfig remotingServerConfig = new RemotingServerConfig();
        if (nodeConfig.getCommandPort() == 0) {
            nodeConfig.setCommandPort(DEFAULT_COMMAND_PORT);
        }
        remotingServerConfig.setListenPort(nodeConfig.getCommandPort());
        this.channelEventListener = new MasterChannelEventListener(application);
        this.remotingServer = new RemotingServerDelegate(remotingServerConfig, channelEventListener);
        LOGGER.info("master initialized remote successfully.");
    }

    /**
     *
     * @throws SQLException
     * @throws IOException
     */
    protected void initDataBase() throws SQLException, IOException {
        LOGGER.info("master start to initialize data base.");
        SQLiteOperation.createTable(nodeConfig.getRootPath(), SQLiteOperation.CREATE_MASTER_PROJECT_TABLE_SQL);
        SQLiteOperation.createTable(nodeConfig.getRootPath(), SQLiteOperation.CREATE_MASTER_TASK_TABLE_SQL);
        LOGGER.info("master initialized data base successfully.");
    }

    /**
     * Start the master service. the method should be called after initialized
     * successful.
     * If the start service process throws exception, then the program will exit.
     */
    protected void startService() {
        try {
            if (started.compareAndSet(false, true)) {
                LOGGER.info("master start to start service.");

                startWorkerManager();

                startProjectParser();

                startProjectManager();

                startProjectScheduler();

                startResourcesManager();

                startRemoting();

                startFileSender();

                LOGGER.info("master start service successfully!");
            }
        } catch (Exception e) {
            LOGGER.error("master start service error!", e);
            System.exit(-1);
        }
    }

    /**
     * start worker manager. make sure the application have the started worker
     * manager that other component in the master node can use.
     */
    protected void startWorkerManager() {
        LOGGER.info("master begin to start worker manager.");
        workerManager = new WorkerManager(application);
        application.setWorkerManager(workerManager);
        workerManager.start();
        LOGGER.info("master start worker manager successful!");
    }

    /**
     * start project parser. make sure the application have the started project
     * parser that other component in the master node can use.
     */
    protected void startProjectParser() {
        LOGGER.info("master begin to start project parser service.");
        projectParser = new ProjectParser(application);
        application.setProjectParser(projectParser);
        projectParser.start();
        LOGGER.info("master start project parser service successfully!");
    }

    /**
     * start project manager. make sure the application have the started project
     * manager that other component in the master node can use.
     */
    protected void startProjectManager() {
        LOGGER.info("master begin to start project manager service.");
        projectManager = ProjectManager.getInstance(application);
        application.setProjectManager(projectManager);
        projectManager.start();
        LOGGER.info("master start project manager service successfully!");
    }

    /**
     * start project scheduler. make sure the application have the started project
     * scheduler that other component in the master node can use.
     */
    protected void startProjectScheduler() {
        LOGGER.info("master begin to start project scheduler service.");
        projectScheduler = new ProjectScheduler(application);
        application.setProjectScheduler(projectScheduler);
        projectScheduler.start();
        LOGGER.info("master start project scheduler service successfully!");
    }

    /**
     * start project scheduler. make sure the application have the started project
     * scheduler that other component in the master node can use.
     */
    protected void startResourcesManager() {
        LOGGER.info("master begin to start resource manager service.");
        resourcesManager = new ResourcesManager(application);
        application.setResourcesManager(resourcesManager);
        LOGGER.info("master start resource manager service successfully!");
    }

    /**
     * start project scheduler. make sure the application have the started project
     * scheduler that other component in the master node can use.
     */
    protected void startRemoting() throws RemotingException {
        LOGGER.info("master begin to start remote service.");
        remotingServer.start();
        application.setRemotingServer(remotingServer);
        RemotingProcessor defaultProcessor = getDefaultProcessor();
        if (defaultProcessor != null) {
            remotingServer.registerDefaultProcessor(defaultProcessor,
                    Executors.newFixedThreadPool(DEFAULT_PROCESSOR_THREAD));
        }
        LOGGER.info("master start remote service successfully.");
    }

    /**
     * start file sender. make sure the application have the started file sender
     * sender that other component in the master node can use.
     */
    protected void startFileSender() throws IOException {
        LOGGER.info("master begin to start file sender service.");
        fileSender = new FileSender();
        fileSender.start(nodeConfig.getDataPort());
        application.setFileSender(fileSender);
        LOGGER.info("master start file sender service successfully.");
    }

    /**
     * registry address registry, which means the master node has initialized and
     * start service successfully and can receive worker connect.
     */
    protected void startRegister() {
        LOGGER.info("master begin to start registry.");
        MasterNodeConfig nodeConfig = application.getMasterNodeConfig();
        String[] split = getRegistryAddress().split(":");
        String ip = split[0];
        int port = Integer.valueOf(split[1]);
        Socket socket = new Socket();
        ObjectOutputStream oos = null;
        BufferedReader br = null;
        try {
            socket.connect(new InetSocketAddress(ip, port));
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(nodeConfig);
            String id = br.readLine();
            if (!StringUtils.isEmpty(id)) {
                nodeConfig.setIdentity(DEFAULT_IDENTITY_PREFIX + id);
                LOGGER.info("master registry successfully. it's id is " + id);
            } else {
                LOGGER.warn("master registry successfully. but it's id is null. set the id to -1");
                id = "-1";
            }
        } catch (IOException e) {
            LOGGER.error("registry error.", e);
            System.exit(1);
        } finally {
            try {
                if (oos != null)
                    oos.close();
            } catch (IOException e) {
                LOGGER.error("close registry connect error!", e);
            }
            try {
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                LOGGER.error("close registry connect error!", e);
            }
        }
    }

    /************************* 	Getter & Setter	*************************/
    protected RemotingProcessor getDefaultProcessor() {
        return new MasterRemotingDispatcher(application);
    }

    public MasterNodeConfig getNodeConfig() {
        return nodeConfig;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }


    public static MasterApplication getApplication() {
        return application;
    }

    public String getWorkspacePath(){
        return nodeConfig.getWorkspacePath();
    }

    protected void setWorkspacePath(String path) {
        nodeConfig.setWorkspacePath(path);
    }

    public String getProjectEnginePath(){
        return nodeConfig.getProjectEnginePath();
    }

    protected void setProjectEnginePath(String path) {
        nodeConfig.setProjectEnginePath(path);
    }

    public String getTaskEnginePath(){
        return nodeConfig.getTaskEnginePath();
    }

    protected void setTaskEnginePath(String path) {
        nodeConfig.setTaskEnginePath(path);
    }

    public int getListenPort() {
        return remotingServer.getListenPort();
    }

    public int getServerWorkerThreads() {
        return remotingServer.getServerWorkerThreads();
    }

    public int getServerCallbackExecutorThreads() {
        return remotingServer.getServerCallbackExecutorThreads();
    }

    public int getServerSelectorThreads() {
        return remotingServer.getServerSelectorThreads();
    }

    public int getServerOneWaySemaphoreValue() {
        return remotingServer.getServerOneWaySemaphoreValue();
    }

    public int getServerAsyncSemaphoreValue() {
        return remotingServer.getServerAsyncSemaphoreValue();
    }

    public int getReaderIdleTimeSeconds() {
        return remotingServer.getReaderIdleTimeSeconds();
    }

    public int getWriterIdleTimeSeconds() {
        return remotingServer.getWriterIdleTimeSeconds();
    }

    public int getServerChannelMaxIdleTimeSeconds() {
        return remotingServer.getServerChannelMaxIdleTimeSeconds();
    }

    public int getInvokeTimeoutMillis() {
        return remotingServer.getInvokeTimeoutMillis();
    }

    public WorkerManager getWorkerManager() {
        return workerManager;
    }

    public ProjectParser getProjectParser() {
        return projectParser;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public ProjectScheduler getProjectScheduler() {
        return projectScheduler;
    }

    public ResourcesManager getResourcesManager() {
        return resourcesManager;
    }

    public RemotingServerDelegate getRemotingServer() {
        return remotingServer;
    }

    public FileSender getFileSender() {
        return fileSender;
    }

    public String getRegistryAddress() {
        return nodeConfig.getRegistryAddress();
    }

    public static void main(String[] args) {
        Master masterNode = new Master();
        masterNode.start();
    }
}
