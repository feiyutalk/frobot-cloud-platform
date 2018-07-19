package org.iceslab.frobot.cloudserver;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.commons.constants.Constants;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.io.FileSender;
import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.iceslab.frobot.remoting.RemotingClientConfig;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.CommandBodyWrapper;
import org.iceslab.frobot.remoting.command.body.request.ProjectSubmitFinishBody;
import org.iceslab.frobot.remoting.command.body.request.ProjectSubmitRequestBody;
import org.iceslab.frobot.remoting.command.body.request.TaskSubmitRequestBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.delegate.RemotingClientDelegate;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Neuclil on 17-4-10.
 */
public class CloudServer {
    private static final Logger LOGGER = Logger.getLogger(CloudServer.class);

    private static final int DEFAULT_COMMAND_PORT = 8000;

    private static final String DEFAULT_CONFIG_PATH =
            System.getProperty("user.home") + File.separator
                    + "frobot_cloudserver" + File.separator
                    + "cloudserverconfig.xml";

    private CloudServerNodeConfig nodeConfig;

    private CloudServerAppContext application;

    private RemotingClientDelegate remotingClient;

    private FileSender fileSender;

    private ConcurrentHashMap<String, MasterInfo> masterinfos = new ConcurrentHashMap<>();

    private AtomicBoolean inited = new AtomicBoolean(false);

    private AtomicBoolean remotingStarted = new AtomicBoolean(false);

    public CloudServer() {
        this.application = CloudServerAppContext.getInstance();
    }

    public void start() {
        try {
            init();

            startRemoting();

            startFileSender();
        } catch (IOException e) {
            LOGGER.error("start cloudserver error!", e);
            System.exit(-1);
        }
    }

    private void init() {
        try {
            if (inited.compareAndSet(false, true)) {
                LOGGER.debug("cloudserver start to initialize.");
                initConfig();

                initRemoting();

                LOGGER.debug("cloudserver initialize successfully.");
            }
        } catch (Exception e) {
            LOGGER.error("cloudserver initialized error!", e);
            System.exit(-1);
        }
    }

    private void initConfig() throws XMLFileNotMatchException {
        LOGGER.debug("cloudserver start to initialize config.");
        Map<String, String> cloudServerConfig = null;
        Map<String, String> master = null;
        try {
            cloudServerConfig = ConfigParser.parseBasicConfig(
                    CloudServer.DEFAULT_CONFIG_PATH);
            master = ConfigParser.parseMasterInfo(CloudServer.DEFAULT_CONFIG_PATH);
        } catch (XMLFileNotMatchException e) {
            LOGGER.error("parse cloud server config error", e);
            throw e;
        }
        CloudServerNodeConfig.Builder builder = new CloudServerNodeConfig.Builder();
        nodeConfig = builder.ip(cloudServerConfig.get("ip"))
                .commandPort(Integer.parseInt(cloudServerConfig.get("commandPort")))
                .dataPort(Integer.parseInt(cloudServerConfig.get("dataPort")))
                .workspacePath(cloudServerConfig.get("workspacePath"))
                .invokeTimeoutMillis(Integer.valueOf(cloudServerConfig.get("invokeTimeoutMillis")))
                .available(true)
                .nodeType(NodeType.CLOUD_SERVER)
                .build();
        MasterInfo masterInfo = new MasterInfo();
        masterInfo.setAddr(master.get("ip") + ":" + master.get("port"));
        masterinfos.put("1", masterInfo);
        application.setCloudServerNodeConfig(nodeConfig);
        LOGGER.debug("cloudserver initialized config successfully." + nodeConfig);
    }

    private void initRemoting() {
        LOGGER.info("cloudserver start to initialize remote connection server.");
        RemotingClientConfig remotingClientConfig = new RemotingClientConfig();
        if (nodeConfig.getCommandPort() == 0) {
            nodeConfig.setCommandPort(DEFAULT_COMMAND_PORT);
        }

        this.remotingClient = new RemotingClientDelegate(remotingClientConfig);
        LOGGER.info("cloudserver initialized remote successfully.");
    }

    private void startRemoting() {
        try {
            if (remotingStarted.compareAndSet(false, true)) {
                LOGGER.info("cloudserver begin to start remoting service.");
                remotingClient.start();
                RemotingProcessor defaultProcessor = getDefaultProcessor();
                if (defaultProcessor != null) {
                    remotingClient.registerDefaultProcessor(defaultProcessor,
                            Executors.newFixedThreadPool(Constants.DEFAULT_PROCESSOR_THREAD));
                }
                LOGGER.info("cloudserver start remoting service successfully!");
            }
        } catch (Exception e) {
            LOGGER.error("cloudserver start remoting service failed! ");
        }
    }

    private void startFileSender() throws IOException {
        LOGGER.debug("cloudserver begin to start file sender service.");
        fileSender = new FileSender();
        fileSender.start(nodeConfig.getDataPort());
        application.setFileSender(fileSender);
        LOGGER.debug("cloudserver start file sender service successfully.");
    }

    public RemotingProcessor getDefaultProcessor() {
        //TODO:
        return null;
    }

    /**
     * submit the given project to master.
     *
     * @param userName       user name of the project.
     * @param projectName    the project name.
     */
    public void submit(String userName, String projectName, int taskNum) {
        Project project = null;
        try {
            project = createProject(userName, projectName, taskNum);
        } catch (IOException e) {
            LOGGER.error("create project error, check the project_config.xml is right.");
            return;
        }

        RemotingProtos.ResponseCode submitProjectResponseCode = submitProject(project);
        if (submitProjectResponseCode == RemotingProtos.ResponseCode.SUBMIT_PROJECT_FAILURE) {
            LOGGER.error("submit project to master error! " + project.getUserName() + " : "
                    + project.getProjectName());
            return;
        }
        LOGGER.info("submit project to master successfully! " + project.getUserName() + " : "
                + project.getProjectName());

        for (int i = 0; i < project.getTaskNum(); i++) {
            RemotingProtos.ResponseCode submitTaskResponseCode =
                    submitTasks(project, i);
            if (submitTaskResponseCode == RemotingProtos.ResponseCode.SUBMIT_TASK_FAILURE) {
                LOGGER.error("submit task to master error! " + project.getUserName());
                return;
            }
        }
        LOGGER.info("submit tasks to master successfully! " + project.getUserName());

        RemotingProtos.ResponseCode submitFinishResponseCode = submitFinish(project);
        if (submitFinishResponseCode == RemotingProtos.ResponseCode.SUBMIT_PROJECT_FINISH_SUCCESS) {
            LOGGER.info("submit project to master finish. " + project.getUserName() + " : "
                    + project.getProjectName());
        } else {
            LOGGER.warn("submit project to master error! " + project.getUserName() + " : "
                    + project.getProjectName());
        }
    }

    private RemotingProtos.ResponseCode submitTasks(Project project, int taskIndex) {
        String taskJarFile = CloudServerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                + project.getUserName() + File.separator
                + project.getProjectName() + File.separator
                + "lib" + File.separator
                + "task_engine" + File.separator
                + project.getTaskJarName()[taskIndex];

        Thread thread = null;
        TaskSubmitRequestBody body = new TaskSubmitRequestBody(project);
        RemotingCommand request = RemotingCommand.createRequestCommand(
                RemotingProtos.RequestCode.SUBMIT_TASK.code(),
                CommandBodyWrapper.wrapper(
                        application.getCloudServerNodeConfig(), body));
        if (project.isPersonalProjectEngine()){
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    fileSender.sendFile(taskJarFile);
                }
            });
            thread.start();
        }
        RemotingCommand response = remotingClient.invokeSync(
                masterinfos.get("1").getAddr(),
                request);
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return RemotingProtos.ResponseCode.valueOf(response.getCode());
    }

    private RemotingProtos.ResponseCode submitProject(Project project) {
        String projectJarFile = getProjectJarFile(project);

        project.setIp(nodeConfig.getIp());
        project.setPort(nodeConfig.getDataPort());
        Thread thread = null;
        ProjectSubmitRequestBody body = new ProjectSubmitRequestBody(project);
        RemotingCommand request = RemotingCommand.createRequestCommand(
                RemotingProtos.RequestCode.SUBMIT_PROJECT.code(),
                CommandBodyWrapper.wrapper(application.getCloudServerNodeConfig(), body));
        if (project.isPersonalProjectEngine()) {
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    fileSender.sendFile(projectJarFile);
                }
            });
            thread.start();
        }
        RemotingCommand response = remotingClient.invokeSync(
                masterinfos.get("1").getAddr(), request);//TODO: use loadbalance to get master addr

        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return RemotingProtos.ResponseCode.valueOf(response.getCode());
    }

    private RemotingProtos.ResponseCode submitFinish(Project project) {
        RemotingCommand request = RemotingCommand.createRequestCommand(
                RemotingProtos.RequestCode.SUBMIT_FINISH.code(),
                CommandBodyWrapper.wrapper(
                        application.getCloudServerNodeConfig(),
                        new ProjectSubmitFinishBody(project)));
        RemotingCommand response = this.remotingClient.invokeSync(masterinfos.get("1").getAddr(), request);
        return RemotingProtos.ResponseCode.valueOf(response.getCode());
    }

    private String getProjectJarFile(Project project) {
        return CloudServerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                + project.getUserName() + File.separator
                + project.getProjectName() + File.separator
                + "lib" + File.separator
                + "project_engine" + File.separator
                + project.getProjectJarName();
    }

    private Project createProject(String userName, String projectName, int taskNum) throws IOException {
        String projectConfigPath = CloudServerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                                    + userName + File.separator
                                    + projectName + File.separator
                                    + "config" + File.separator
                                    + "project_config.xml";
        Project project = ConfigParser.parseProjectConfig(projectConfigPath);
        project.setTaskNum(taskNum);
        Map<String, String> projectConfig = FileManageUtil.getFileFromFolder(
                CloudServerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                        + project.getUserName() + File.separator
                        + project.getProjectName() + File.separator
                        + "config", "xml");
        File fileConfig = new File(projectConfig.get("project_config.xml"));
        byte[] config = FileManageUtil.getBytesFromFile(fileConfig);
        String[] hashValues = new String[2];
        project.setHashValues(hashValues);
        project.setProjectConfigFile(config);

        Map<String, byte[]> taskConfigs = new HashMap<>();
        Map<String, String> taskConfig = FileManageUtil.getFileFromFolder(
                CloudServerNodeConfig.DEFAULT_WORKSPACE_PATH + File.separator
                        + project.getUserName() + File.separator
                        + project.getProjectName() + File.separator
                        + "config", "xml");
        Set<Map.Entry<String, String>> entrySetTaskConfig = taskConfig.entrySet();
        Iterator<Map.Entry<String, String>> iteratorTaskConfig = entrySetTaskConfig.iterator();
        while (iteratorTaskConfig.hasNext()) {
            Map.Entry<String, String> next = iteratorTaskConfig.next();
            taskConfigs.put(next.getKey(), FileManageUtil.getBytesFromFile(new File(next.getValue())));
        }
        project.setTaskConfigFiles(taskConfigs);
        return project;
    }

    public static void main(String[] args) {
        CloudServer cloudServer = new CloudServer();
        cloudServer.start();
        String userName = "conghuai";
        String projectName = "szds";
        int taskNum = 1;
        cloudServer.submit(userName, projectName, taskNum);
    }
}
