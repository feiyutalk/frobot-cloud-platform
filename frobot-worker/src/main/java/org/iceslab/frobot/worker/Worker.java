package org.iceslab.frobot.worker;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.NodeInitializedException;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.cluster.RemoteWorkerInfo;
import org.iceslab.frobot.commons.utils.SystemInfoUtil;
import org.iceslab.frobot.commons.utils.db.SQLiteOperation;
import org.iceslab.frobot.worker.manager.HeartBeatMonitor;
import org.iceslab.frobot.worker.manager.task.TaskReceiver;
import org.iceslab.frobot.worker.processor.WorkerRemotingDispatcher;
import org.iceslab.frobot.commons.constants.Constants;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.CommandBodyWrapper;
import org.iceslab.frobot.remoting.command.body.request.HeartBeatRequestBody;
import org.iceslab.frobot.remoting.command.body.request.NullRequestBody;
import org.iceslab.frobot.remoting.command.body.request.ReportFailMasterRequestBody;
import org.iceslab.frobot.remoting.command.body.response.FindAddrResponseBody;
import org.iceslab.frobot.remoting.command.body.response.IdResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.delegate.RemotingClientDelegate;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *  Worker node, represents worker in master-worker distribute resource. Once the
 *  worker initialize successfully, it will connect to registry for an available
 *  master node address. Then, it will connect to the given master, report
 *  it's information to the master and receives task to execute.
 * @author Neuclil
 */
public class Worker {
    private static final Logger LOGGER = Logger.getLogger(Worker.class);

    private static final  String DEFAULT_WORKER_CONFIG_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_worker" + File.separator
                    +"workerconfig.xml";
    
    private WorkerNodeConfig nodeConfig;
    
    private WorkerApplication application;

    private AtomicBoolean inited = new AtomicBoolean(false);
    
    private AtomicBoolean remotingStarted = new AtomicBoolean(false);
	
    private AtomicBoolean heartbeatStarted = new AtomicBoolean(false);

    private RemotingClientDelegate remotingClient;
   
    private HeartBeatMonitor heartBeatMonitor;
    
	private String configPath = Worker.DEFAULT_WORKER_CONFIG_PATH;
  
    public Worker() {
        this.application = new WorkerApplication();
    }

    /**
     * Start the worker node. There have five steps to finish start
     * step and become available for master to allocate task.
     * <p>
     *     1. initialize worker node config.
     *     2. start remote service
     *     3.
     *
     * </p>
     */
    public void start(){
        init();
        startRemoting();
        findMaster();
        connect();
        startHeartBeat();
    }

    /**
     * init the worker node, including init config, init workspace,
     * init remote service, init the Application class.
     */
    protected void init() {
        try {
            if (inited.compareAndSet(false, true)) {
                LOGGER.debug("worker start to initialize.");
                initConfig();

                initWorkSpace();

                initDataBase();

                initRemoting();

                initApplication();
                LOGGER.debug("worker initialize successfully.");
            }
        } catch (Exception e) {
            LOGGER.error("worker initialized error!", e);
            System.exit(-1);
        }
    }

    /**
     * init worker node config information.
     * The config will be read from
     * {$USER_HOME}/frobot_worker/workerconfig.xml.
     * @throws NodeInitializedException
     */
    protected void initConfig() throws NodeInitializedException {
        LOGGER.debug("worker start to initialize config.");
        Map<String, String> workerConfig = null;
        try {
            workerConfig = ConfigParser.parseBasicConfig(configPath);
        } catch (XMLFileNotMatchException e) {
            LOGGER.error("parse worker node config error",e);
            throw new NodeInitializedException(e);
        }

        WorkerNodeConfig.Builder builder = new WorkerNodeConfig.Builder();
        nodeConfig = builder.ip(workerConfig.get("ip"))
                            .commandPort(Integer.valueOf(workerConfig.get("commandPort")))
                            .dataPort(Integer.valueOf(workerConfig.get("dataPort")))
                            .identity("noid")
                            .registryAddress(workerConfig.get(("registryAddress")))
                            .taskEnginePath(workerConfig.get("taskEnginePath"))
                            .retryTimes(Integer.valueOf(workerConfig.get("retryTimes")))
                            .invokeTimeoutMillis(Integer.valueOf(workerConfig.get("invokeTimeoutMillis")))
                            .rootPath(workerConfig.get("rootPath"))
                            .workspacePath(workerConfig.get("workspacePath"))
                            .nodeType(NodeType.WORKER)
                            .clusterName(Constants.DEFAULT_CLUSTER_NAME)
                            .build();
        application.setWorkerNodeConfig(nodeConfig);
        LOGGER.debug("worker initialized config successfully. " + nodeConfig);
    }

    /**
     * the work space directory and task engine directory will be make.
     * the default work space directory is {$USER_HOME}/frobot_master/workspace,
     * which is used to store some information about tasks.
     * @throws NodeInitializedException
     */
    protected void initWorkSpace() throws NodeInitializedException {
        LOGGER.debug("worker start to initialize work space.");
        boolean success = FileManageUtil.generateDirectory(nodeConfig.getWorkspacePath());
        if(!success) {
        	LOGGER.error("worker initialize work space error.");
        	throw new NodeInitializedException("worker initialize work space failed.");
        }
        success = FileManageUtil.generateDirectory(nodeConfig.getTaskEnginePath());
        if(!success) {
        	LOGGER.error("worker initialize taskEnginePath error.");
        	throw new NodeInitializedException("worker initialize task engine path failed.");
        }
        LOGGER.debug("initialize work space directory successfully! work space path : "+nodeConfig.getWorkspacePath());
        LOGGER.debug("initialize task engine directory successfully! task engine path :"+nodeConfig.getTaskEnginePath());
        LOGGER.debug("initialize work space successfully!");
    }

    /**
     * .....
     * Initialize data base.
     * @throws NodeInitializedException
     */
    protected void initDataBase() throws NodeInitializedException, IOException, SQLException {
        LOGGER.debug("worker start to initialize data base.");
        SQLiteOperation.createTable(nodeConfig.getRootPath(), SQLiteOperation.CREATE_WORKER_TASK_TABLE_SQL);
        LOGGER.debug("worker initialize data base successfully!");
    }

    /**
     * initialized remote connection server. The default remote connection frame is 
     * netty and the default code/decode is java code/decode.
     * The initialized method including initializing the remote client delegate and
     * initializing the HeartBeatMonitor.
     */
    protected void initRemoting() {
        LOGGER.debug("start initializing remote service...");
        this.remotingClient = new RemotingClientDelegate();
        this.heartBeatMonitor = new HeartBeatMonitor(application);
        LOGGER.debug("init remoting service over.");
    }

    /**
     * 3. init Application
     */
    protected void initApplication() {
        LOGGER.debug("start initializing Application...");
        application.setRemotingClient(remotingClient);
        application.setHeartBeatMonitor(heartBeatMonitor);
        application.setTaskReceiver(new TaskReceiver(application));
        LOGGER.info("init Applicationover.");
    }

    /**
     * start remoting service
     */
    protected void startRemoting() {
        try {
            if (remotingStarted.compareAndSet(false, true)) {
                LOGGER.info("worker begin to start remoting service.");
                remotingClient.start();
                RemotingProcessor defaultProcessor = getDefaultProcessor();
                if (defaultProcessor != null) {
                    remotingClient.registerDefaultProcessor(defaultProcessor,
                            Executors.newFixedThreadPool(Constants.DEFAULT_PROCESSOR_THREAD));
                }
                LOGGER.info("worker start remoting service successfully!");
            }
        } catch (Exception e) {
            LOGGER.error("worker start remoting service failed! ");
        }
    }


    /**
     * get the MasterAddress from registry center.
     */
    protected void findMaster() {
        RemotingCommand response;
        int count = 1;
        do {
            LOGGER.debug("Worker start find available MasterAddress...for " + count + " times");
            count++;
            RemotingCommand request = RemotingCommand.createRequestCommand(
                    RemotingProtos.RequestCode.REQUEST_MASTER_ADDR.code(),
                    CommandBodyWrapper.wrapper(nodeConfig, new NullRequestBody()));
            response = application.getRemotingClient().invokeSync(
                   application.getWorkerNodeConfig().getRegistryAddress(),
                    request);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (response == null || response.getCode() != RemotingProtos.ResponseCode.REQUEST_ADDR_SUCCESS.code());
        FindAddrResponseBody body = (FindAddrResponseBody) response.getBody();
        application.setMasterAddress(body.getAddress());
        LOGGER.debug("Worker get the MasterAddress :" + application.getMasterAddress());
    }

    /**
     * connect to the Master,and get the id distributed from Master.
     */
    protected void connect() {
        LOGGER.debug("Worker try to connect the Master...");
        RemoteWorkerInfo remoteWorkerInfo = SystemInfoUtil.getWorkerInfo();
        RemotingCommand request = null;
        RemotingCommand response = null;
        int count = 1;
        do{
            try{
                HeartBeatRequestBody heartBeatRequestBody = new HeartBeatRequestBody();
                heartBeatRequestBody.setWorkerInfo(remoteWorkerInfo);
                request = RemotingCommand.createRequestCommand(
                        RemotingProtos.RequestCode.CONNECT.code(),
                        CommandBodyWrapper.wrapper(nodeConfig,heartBeatRequestBody));
                response = application.getRemotingClient().invokeSync(
                        application.getMasterAddress(),
                        request);
                count++;
                if(response == null){
                    report();
                    count = 0;
                    continue;
                }
                if(response.getCode() ==
                        RemotingProtos.ResponseCode.CONNECT_SUCCESS.code()){
                    IdResponseBody IdBody =(IdResponseBody) response.getBody();
                    application.getWorkerNodeConfig().setIdentity(IdBody.getAllocateIdentity());
                    LOGGER.debug("connect Master success:" + application.getMasterAddress()
                            + ",the identity is : " + IdBody.getAllocateIdentity());
                    return;
                }
                if(count == nodeConfig.getRetryTimes()){
                    report();
                    count = 0;
                    continue;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }while(count<nodeConfig.getRetryTimes());
    }

    protected void report(){
        LOGGER.debug("connect to Master"+application.getMasterAddress()+"failed! Report the info to registry and request the address again!");
        RemotingCommand reportRequest = RemotingCommand.createRequestCommand(
                RemotingProtos.RequestCode.FAIL_MASTER_ADDR.code(),
                CommandBodyWrapper.wrapper(
                        application.getWorkerNodeConfig(),
                        new ReportFailMasterRequestBody(application.getMasterAddress())));
        remotingClient.invokeSync(nodeConfig.getRegistryAddress(), reportRequest);
        findMaster();
    }

    /**
     * start monitor heart beat
     */
    protected void startHeartBeat(){
        if(heartbeatStarted.compareAndSet(false, true)){
            try{
                LOGGER.debug("start monitoring heart beat....");
                heartBeatMonitor.start();
                LOGGER.debug("heartBeatMonitor start successfully!");
            }catch(Exception e){
                LOGGER.error("heartBeatMonitor start failed!",e);
            }
        }
    }

    private RemotingProcessor getDefaultProcessor() {
    	return new WorkerRemotingDispatcher(application);
    }

	public void setConfigPath(String configPath) {
		this.configPath = configPath;
	}

	public WorkerNodeConfig getConfig() {
		return nodeConfig;
	}

	public WorkerApplication getApplication() {
		return application;
	}
	
    public AtomicBoolean getRemotingStarted() {
		return remotingStarted;
	}

	public AtomicBoolean getHeartbeatStarted() {
		return heartbeatStarted;
	}

	public void setTaskEnginePath(String path) {
		  nodeConfig.setTaskEnginePath(path);
	}

	public void setWorkSpacePath(String path) {
		nodeConfig.setWorkspacePath(path);
	}

    public static void main(String[] args) {
        Worker workerNode = new Worker();
        workerNode.start();
    }
}
