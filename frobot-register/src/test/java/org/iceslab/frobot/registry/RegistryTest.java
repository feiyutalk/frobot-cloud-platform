package org.iceslab.frobot.registry;

import org.iceslab.frobot.cluster.NodeInitializedException;
import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.iceslab.frobot.remoting.RemotingRegisterConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class RegistryTest {
    private static String REGISTRY_PATH;
    private static String REGISTRY_CONFIG_FILE;
    private static String REGISTRY_WORKSPACE_PATH;
    private static final int AVAILABLE_PROCESSOR = Runtime.getRuntime().availableProcessors();
    private Registry registry;
    private RemotingRegisterConfig remotingRegisterConfig;

    @Before
    public void setUp() throws IOException {
        REGISTRY_PATH = System.getProperty("user.home") + File.separator + "frobot_registry_test";
        REGISTRY_WORKSPACE_PATH = REGISTRY_PATH + File.separator + "workspace";
        REGISTRY_CONFIG_FILE = ResourcesOperator.makeRegistryConfig(REGISTRY_PATH);
        remotingRegisterConfig = new RemotingRegisterConfig();
        registry = new Registry(remotingRegisterConfig);
        registry.setConfigPath(REGISTRY_CONFIG_FILE);
    }

    @After
    public void tearDown(){
        FileManageUtil.deleteAll(REGISTRY_PATH);
    }

    @Test
    public void testNormalInitializedConfig() throws NodeInitializedException {
        registry.initConfig();
        assertEqualsConfig();
    }

    private void assertEqualsConfig() {
        assertNotNull(registry.getNodeConfig());
        RegistryNodeConfig nodeConfig = registry.getNodeConfig();
        assertEquals("localhost", nodeConfig.getIp());
        assertEquals(38100, nodeConfig.getCommandPort());
        assertEquals(38102, nodeConfig.getRegisterPort());
        assertEquals(60000, nodeConfig.getInvokeTimeoutMillis());
        assertEquals(System.getProperty("user.home") + File.separator + "frobot_registry_test/workspace", nodeConfig.getWorkspacePath());
        assertNotNull(registry.getApplication().getNodeConfig());
    }

    @Test(expected = NodeInitializedException.class)
    public void testAbnormalInitializedConfig() throws NodeInitializedException {
        registry.setConfigPath("");
        registry.initConfig();
    }

    @Test
    public void testInitRemoting() throws NodeInitializedException {
        registry.initConfig();
        registry.initRemoting();
        assertEqualsRemote();
    }

    private void assertEqualsRemote() {
        assertNotNull(remotingRegisterConfig);
        assertEquals(8000, remotingRegisterConfig.getNettyPort());
        assertEquals(8002, remotingRegisterConfig.getSocketPort());
    }

    @Test
    public void testInit() {
        registry.init();
    }

}