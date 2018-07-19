package org.iceslab.frobot.master;

import org.iceslab.frobot.cluster.NodeInitializedException;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.commons.constants.Constants;
import org.iceslab.frobot.commons.utils.db.SQLiteOperation;
import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.iceslab.frobot.remoting.exception.RemotingException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static junit.framework.TestCase.*;


/**
 * Created by Neuclil on 17-7-22.
 */
public class MasterTest {
    private static String MASTER_PATH;
    private static String MASTER_CONFIG_FILE;
    private static String MASTER_WORKSPACE_PATH;
    private static String MASTER_PROJECT_ENGINE_PATH;
    private static String MASTER_TASK_ENGINE_PATH;
    private static final int AVAILABLE_PROCESSOR = Runtime.getRuntime().availableProcessors();
    private Master master;

    @Before
    public void setUp() throws IOException {
        MASTER_PATH =  System.getProperty("user.home") + File.separator +"frobot_master_test";
        MASTER_CONFIG_FILE = ResourcesOperator.makeMasterConfig(MASTER_PATH);
        MASTER_WORKSPACE_PATH = MASTER_PATH + File.separator + "workspace";
        MASTER_PROJECT_ENGINE_PATH = MASTER_WORKSPACE_PATH + File.separator +  "frobot/lib/project_engine";
        MASTER_TASK_ENGINE_PATH = MASTER_WORKSPACE_PATH + File.separator + "frobot/lib/task_engine";
        master = new Master();
        master.setConfigPath(MASTER_CONFIG_FILE);
    }

    @After
    public void tearDown() {
        FileManageUtil.deleteAll(MASTER_PATH);
    }

    @Test
    public void testNormalInitializedConfig() {
        try {
            master.initConfig();
        } catch (NodeInitializedException e) {
            fail("should not reach here.");
        }
        assertEqualsConfig();
    }

    private void assertEqualsConfig() {
        assertNotNull(master.getNodeConfig());
        MasterNodeConfig nodeConfig = master.getNodeConfig();
        assertEquals("localhost", nodeConfig.getIp());
        assertEquals(18100, nodeConfig.getCommandPort());
        assertEquals(18101, nodeConfig.getDataPort());
        assertEquals("localhost:38102", nodeConfig.getRegistryAddress());
        assertEquals(600000, nodeConfig.getInvokeTimeoutMillis());

        assertEquals(System.getProperty("user.home") + "/frobot_master_test/workspace", nodeConfig.getWorkspacePath());
        assertEquals(System.getProperty("user.home") + "/frobot_master_test/workspace/frobot/lib/task_engine", nodeConfig.getTaskEnginePath());

        assertEquals(System.getProperty("user.home") + File.separator + "frobot_master_test/workspace", nodeConfig.getWorkspacePath());
        assertEquals(System.getProperty("user.home") + File.separator + "frobot_master_test/workspace/frobot/lib/project_engine", nodeConfig.getProjectEnginePath());
        assertEquals(System.getProperty("user.home") + File.separator + "frobot_master_test/workspace/frobot/lib/task_engine", nodeConfig.getTaskEnginePath());

        assertEquals("unregistry", nodeConfig.getIdentity());
        assertEquals(NodeType.MASTER, nodeConfig.getNodeType());
        assertEquals(Constants.DEFAULT_CLUSTER_NAME, nodeConfig.getClusterName());
        assertEqualsDefaultProjectEngines(nodeConfig.getDefaultProjectEngines());
        assertEqualsDefaultTaskEngines(nodeConfig.getDefaultTaskEngines());
        assertNotNull(master.getApplication().getMasterNodeConfig());
    }

    private void assertEqualsDefaultTaskEngines(Map<String, Map<String, DefaultTaskEngineInfo>> engines) {
        Map<String, DefaultTaskEngineInfo> spider = engines.get("spider");
        DefaultTaskEngineInfo crawling = spider.get("crawling");
        assertEquals("crawling", crawling.getTaskSimpleName());
        assertEquals("spiderCrawling.jar", crawling.getTaskJarName());
        assertEquals("org.iceslab.spider.CrawlingTaskEngine", crawling.getTaskEngineName());

        DefaultTaskEngineInfo extract = spider.get("extract");
        assertEquals("extract", extract.getTaskSimpleName());
        assertEquals("spiderExtract.jar", extract.getTaskJarName());
        assertEquals("org.iceslab.spider.ExtractTaskEngine", extract.getTaskEngineName());

        DefaultTaskEngineInfo integrate = spider.get("integrate");
        assertEquals("integrate", integrate.getTaskSimpleName());
        assertEquals("spiderIntegrate.jar", integrate.getTaskJarName());
        assertEquals("org.iceslab.spider.IntegrateTaskEngine", integrate.getTaskEngineName());
    }

    private void assertEqualsDefaultProjectEngines(Map<String, DefaultProjectEngineInfo> engines) {
        DefaultProjectEngineInfo spider = engines.get("spider");
        assertEquals("spider", spider.getProjectSimpleName());
        assertEquals("spider.jar", spider.getProjectJarName());
        assertEquals("org.iceslab.spider.SpiderProjectEngine", spider.getProjectEngineName());
    }

    @Test(expected = NodeInitializedException.class)
    public void testAbnormalInitializedConfig() throws NodeInitializedException {
        master.setConfigPath("");
        master.initConfig();
    }

    @Test
    public void testInitWorkspace() throws NodeInitializedException {
        master.initConfig();
        master.initWorkSpace();
        File wsFile = new File(MASTER_WORKSPACE_PATH);
        assertTrue(wsFile.exists());
        assertTrue(wsFile.isDirectory());
        assertEquals(wsFile.getAbsolutePath(), master.getWorkspacePath());

        File pFile = new File(MASTER_PROJECT_ENGINE_PATH);
        assertTrue(pFile.exists());
        assertTrue(pFile.isDirectory());
        assertEquals(pFile.getAbsolutePath(), master.getProjectEnginePath());

        File tFile = new File(MASTER_TASK_ENGINE_PATH);
        assertTrue(tFile.exists());
        assertTrue(tFile.isDirectory());
        assertEquals(tFile.getAbsolutePath(), master.getTaskEnginePath());
    }

    @Test(expected = NodeInitializedException.class)
    public void testInitWorkspaceWithErrorWSPath() throws NodeInitializedException {
        try {
            master.initConfig();
        } catch (NodeInitializedException e) {
            fail("should not reach here.");
        }
        master.setWorkspacePath("/error/" + MASTER_PROJECT_ENGINE_PATH);
        master.initWorkSpace();
    }

    @Test(expected = NodeInitializedException.class)
    public void testInitWorkspaceWithErrorPPath() throws NodeInitializedException {
        try {
            master.initConfig();
        } catch (NodeInitializedException e) {
            fail("should not reach here.");
        }
        master.setProjectEnginePath("/error/" + MASTER_PROJECT_ENGINE_PATH);
        master.initWorkSpace();
    }

    @Test(expected = NodeInitializedException.class)
    public void testInitWorkspaceWithErrorTPath() throws NodeInitializedException {
        try {
            master.initConfig();
        } catch (NodeInitializedException e) {
            fail("should not reach here.");
        }
        master.setTaskEnginePath("/error/" + MASTER_TASK_ENGINE_PATH);
        master.initWorkSpace();
    }

    @Test
    public void testInitRemote() throws NodeInitializedException {
        master.initConfig();
        master.initRemoting();
        assertEqualsRemote();
    }

    private void assertEqualsRemote() {
        assertEquals(18100, master.getListenPort());
        assertEquals(32, master.getServerWorkerThreads());
        assertEquals(AVAILABLE_PROCESSOR * 2, master.getServerCallbackExecutorThreads());
        assertEquals(AVAILABLE_PROCESSOR * 2, master.getServerSelectorThreads());
        assertEquals(32, master.getServerOneWaySemaphoreValue());
        assertEquals(64, master.getServerAsyncSemaphoreValue());
        assertEquals(0, master.getReaderIdleTimeSeconds());
        assertEquals(0, master.getWriterIdleTimeSeconds());
        assertEquals(120, master.getServerChannelMaxIdleTimeSeconds());
        assertEquals(1000_000_000, master.getInvokeTimeoutMillis());
    }

    @Test
    public void testInitDataBase() throws IOException, SQLException, NodeInitializedException {
        master.initConfig();
        master.initDataBase();
        File db = new File(MASTER_PATH + File.separator + SQLiteOperation.DATABASE_NAME);
        assertTrue(db.exists());
        assertTrue(db.isFile());
    }

    @Ignore(value = "some thing error in jenkins integration test.")
    @Test
    public void testInit() {
        master.init();
    }

    @Test
    public void testStartService() throws NodeInitializedException {
        master.initConfig();
        master.initRemoting();
        master.startService();
        master.getFileSender().close();
    }

    @Test
    public void testStartWorkerManager() throws NodeInitializedException {
        master.initConfig();
        master.startWorkerManager();
        assertNotNull(master.getWorkerManager());
        assertNotNull(master.getApplication().getWorkerManager());
    }

    @Test
    public void testStartProjectParser() throws NodeInitializedException {
        master.initConfig();
        master.startProjectParser();
        assertNotNull(master.getProjectParser());
        assertNotNull(master.getApplication().getProjectParser());
    }

    @Test
    public void testStartProjectManager() throws NodeInitializedException {
        master.initConfig();
        master.startProjectManager();
        assertNotNull(master.getProjectManager());
        assertNotNull(master.getApplication().getProjectManager());
    }

    @Test
    public void testStartProjectScheduler() throws NodeInitializedException {
        master.initConfig();
        master.startProjectScheduler();
        assertNotNull(master.getProjectScheduler());
        assertNotNull(master.getApplication().getProjectScheduler());
    }

    @Test
    public void testStartSystemManager() throws NodeInitializedException {
        master.initConfig();
        master.startResourcesManager();
        assertNotNull(master.getResourcesManager());
        assertNotNull(master.getApplication().getResourcesManager());
    }

    @Test
    public void testStartRemoting() throws NodeInitializedException, RemotingException {
        master.initConfig();
        master.initRemoting();
        master.startRemoting();
        assertNotNull(master.getRemotingServer());
        assertNotNull(master.getApplication().getRemotingServer());
    }

    @Test
    public void testStartFileSender() throws NodeInitializedException, IOException {
        master.initConfig();
        master.startFileSender();
        assertNotNull(master.getFileSender());
        assertNotNull(master.getApplication().getFileSender());
        master.getFileSender().close();
    }

}
