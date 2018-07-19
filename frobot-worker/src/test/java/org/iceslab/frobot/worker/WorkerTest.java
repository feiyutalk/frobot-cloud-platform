package org.iceslab.frobot.worker;

import org.iceslab.frobot.cluster.NodeInitializedException;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.commons.constants.Constants;
import org.iceslab.frobot.commons.utils.db.SQLiteOperation;
import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import static junit.framework.TestCase.*;


/**
 * Created by Neuclil on 17-7-23.
 */
public class WorkerTest {
    private static String WORKER_PATH;
    private static String WORKER_CONFIG_FILE;
    private static String WORKER_WORKSPACE_PATH;
    private static String WORKER_TASK_ENGINE_PATH;
    private Worker worker;

    @Before
    public void setUp() throws IOException {
        WORKER_PATH =  System.getProperty("user.home") + File.separator +"frobot_worker_test";
        WORKER_CONFIG_FILE = ResourcesOperator.makeMasterConfig(WORKER_PATH);
        WORKER_WORKSPACE_PATH = WORKER_PATH + File.separator + "workspace";
        WORKER_TASK_ENGINE_PATH = WORKER_WORKSPACE_PATH + File.separator + "frobot/lib/task_engine";
        worker = new Worker();
        worker.setConfigPath(WORKER_CONFIG_FILE);
    }

    @After
    public void tearDown(){
        FileManageUtil.deleteAll(WORKER_PATH);
    }

    @Test
    public void testNormalInitializedConfig() {
        try {
            worker.initConfig();
        } catch (NodeInitializedException e) {
            fail("should not reach here.");
        }
        assertEqualsConfig();
    }

    private void assertEqualsConfig() {
        assertNotNull(worker.getConfig());
        WorkerNodeConfig workerNodeConfig = worker.getConfig();
        assertEquals("localhost", workerNodeConfig.getIp());
        assertEquals(28100, workerNodeConfig.getCommandPort());
        assertEquals(28101, workerNodeConfig.getDataPort());
        assertEquals("localhost:38100", workerNodeConfig.getRegistryAddress());
        assertEquals(60000, workerNodeConfig.getInvokeTimeoutMillis());
        assertEquals(System.getProperty("user.home") + File.separator + "frobot_worker_test/workspace", workerNodeConfig.getWorkspacePath());
        assertEquals(System.getProperty("user.home") + File.separator + "frobot_worker_test/workspace/frobot/lib/task_engine", workerNodeConfig.getTaskEnginePath());
        assertEquals("noid", workerNodeConfig.getIdentity());
        assertEquals(NodeType.WORKER, workerNodeConfig.getNodeType());
        assertEquals(5, workerNodeConfig.getRetryTimes());
        assertEquals(Constants.DEFAULT_CLUSTER_NAME, workerNodeConfig.getClusterName());
        assertNotNull(worker.getApplication().getWorkerNodeConfig());
    }

    @Test(expected = NodeInitializedException.class)
    public void testAbnormalInitializedConfig() throws NodeInitializedException {
        worker.setConfigPath(" ");
        worker.initConfig();
    }

    @Test
    public void testInitWorkspace() throws NodeInitializedException {
        worker.initConfig();
        worker.initWorkSpace();
        File wFile = new File(WORKER_WORKSPACE_PATH);
        assertTrue(wFile.exists());
        assertTrue(wFile.isDirectory());
        assertEquals(wFile.getAbsolutePath(), WORKER_WORKSPACE_PATH);

        File tFile = new File(WORKER_TASK_ENGINE_PATH);
        assertTrue(tFile.exists());
        assertTrue(tFile.isDirectory());
        assertEquals(tFile.getAbsolutePath(), WORKER_TASK_ENGINE_PATH);
    }

    @Test(expected = NodeInitializedException.class)
    public void testinitWorkSpaceWithWrongWSPath() throws NodeInitializedException {
        try {
            worker.initConfig();
        } catch (NodeInitializedException e) {
            fail("should not reach here.");
        }
        worker.setWorkSpacePath("/error/" + WORKER_WORKSPACE_PATH);
        worker.initWorkSpace();
    }

    @Test(expected = NodeInitializedException.class)
    public void testinitWorkspaceWithWrongtaskPath() throws NodeInitializedException {
        try {
            worker.initConfig();
        } catch (NodeInitializedException e) {
            fail("should not reach here.");
        }
        worker.setTaskEnginePath("/error/" + WORKER_TASK_ENGINE_PATH);
        worker.initWorkSpace();
    }

    @Ignore(value = "waite for the DBOperation Class refactory.")
    @Test
    public void testInitDataBase() throws NodeInitializedException, IOException, SQLException {
        worker.initConfig();
        worker.initDataBase();
        File db = new File(WORKER_PATH + File.separator + SQLiteOperation.DATABASE_NAME);
        assertTrue(db.exists());
        assertTrue(db.isFile());
    }

    @Test
    public void testInitRemoting() {
        try {
            worker.initConfig();
        } catch (NodeInitializedException e) {
            fail("should not reach here.");
        }
        worker.initRemoting();
    }

    @Test
    public void testInitApplication() {
        try {
            worker.initConfig();
        } catch (NodeInitializedException e) {
            fail("should not reach here.");
        }
        worker.initApplication();
        assertNotNull(worker.getApplication());
    }

    @Test
    public void testInit() {
        worker.init();
    }

    @Test
    public void testStartRemoting() throws NodeInitializedException {
        worker.initConfig();
        worker.initRemoting();
        worker.startRemoting();
        worker.initApplication();
        assertNotNull(worker.getApplication().getRemotingClient());
    }

    @Test
    public void testStartHeartBeat() throws NodeInitializedException {
        worker.initConfig();
        worker.initRemoting();
        worker.initApplication();
        worker.startHeartBeat();
        assertNotNull(worker.getApplication().getHeartBeatMonitor());
    }
}
