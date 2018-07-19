package org.iceslab.frobot.master.manager.channel;


import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.master.MasterTestUtils;
import org.iceslab.frobot.master.manager.project.ProjectRuntime;
import org.iceslab.frobot.remoting.Channel;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.createMock;

/**
 * Created by Neuclil on 17-7-10.
 */
public class WorkerManagerTest {
    private static final String IDENTITY = "frobot-master-1_worker-1";
    private MasterApplication application;
    private WorkerManager workerManager;
    private Channel channel;
    private WorkerInfo workerInfo;

    @Before
    public void setUp(){
        channel = createMock("Channel", Channel.class);
        application = MasterTestUtils.getApplication();
        workerManager = new WorkerManager(application);
        workerManager.start();
    }

    @Test
    public void testAddWithNullId(){
        WorkerInfo.Builder builder = new WorkerInfo.Builder(channel);
        workerInfo = builder.identity("noid").build();
        String id = workerManager.add(workerInfo);
        assertEquals(IDENTITY, id);
        assertSame(workerInfo, workerManager.get(IDENTITY));
        assertEquals(1, workerManager.size());
    }

    @Test
    public void testAddWithId(){
        WorkerInfo.Builder builder = new WorkerInfo.Builder(channel);
        workerInfo = builder.identity(IDENTITY).build();
        String id = workerManager.add(workerInfo);
        assertEquals(IDENTITY, id);
        assertSame(workerInfo, workerManager.get(IDENTITY));
        assertEquals(1, workerManager.size());

        workerManager.add(workerInfo);
        assertEquals(1, workerManager.size());
    }

    @Test
    public void testRemove(){
        WorkerInfo.Builder builder = new WorkerInfo.Builder(channel);
        workerInfo = builder.identity(IDENTITY).build();
        workerManager.add(workerInfo);
        assertEquals(1, workerManager.size());
        workerManager.remove(channel);
        assertEquals(0, workerManager.size());
    }

    @Test
    public void testRemoveNone(){
        WorkerInfo.Builder builder = new WorkerInfo.Builder(channel);
        workerInfo = builder.identity(IDENTITY).build();

        Channel trChannel = createMock("channel", Channel.class);
        workerManager.add(workerInfo);
        assertEquals(1, workerManager.size());
        workerManager.remove(trChannel);
        assertEquals(1, workerManager.size());
    }

    @Test
    public void testGet(){
        WorkerInfo.Builder builder = new WorkerInfo.Builder(channel);
        workerInfo = builder.identity(IDENTITY).build();
        workerManager.add(workerInfo);
        assertSame(workerInfo, workerManager.get(IDENTITY));
    }

    @Test
    public void testSize(){
        WorkerInfo.Builder builder = new WorkerInfo.Builder(channel);
        workerInfo = builder.identity(IDENTITY).build();
        workerManager.add(workerInfo);
        assertTrue(workerManager.size() == 1);

        WorkerInfo workerInfo2 = builder.identity(IDENTITY+1).build();
        workerManager.add(workerInfo2);
        assertTrue(workerManager.size() == 2);
    }

    @Test
    public void testIsEmpty(){
        assertTrue(workerManager.isEmpty());
        WorkerInfo.Builder builder = new WorkerInfo.Builder(channel);
        workerInfo = builder.identity(IDENTITY).build();
        workerManager.add(workerInfo);
        assertFalse(workerManager.isEmpty());
    }

    @Test
    public void testGetResource(){
        ProjectRuntime.Builder pBuilder = new ProjectRuntime.Builder();
        ProjectRuntime projectRuntime = pBuilder
                .cpu(2)
                .hardDisk(2)
                .memory(2)
                .workerNum(2)
                .build();
        WorkerInfo.Builder wBuilder = new WorkerInfo.Builder(channel);

        WorkerInfo workerInfo1 = wBuilder
                .identity(IDENTITY+1)
                .cpu(3)
                .harddisk(3)
                .memory(3)
                .available(true)
                .running(false)
                .build();

        WorkerInfo workerInfo2 = wBuilder
                .identity(IDENTITY+2)
                .cpu(1)
                .harddisk(3)
                .memory(5)
                .available(true)
                .running(false)
                .build();

        workerManager.add(workerInfo1);
        workerManager.add(workerInfo2);
        List<String> resources1 = workerManager.requireWorkerResources(projectRuntime);
        assertNull(resources1);

        WorkerInfo workerInfo3 = wBuilder
                .identity(IDENTITY+3)
                .cpu(3)
                .harddisk(4)
                .memory(5)
                .build();
        workerManager.add(workerInfo3);
        List<String> resources2 = workerManager.requireWorkerResources(projectRuntime);
        assertNotNull(resources2);
        assertTrue(2==resources2.size());
        assertTrue(IDENTITY+1 == resources2.get(0) ||
                    IDENTITY+3 == resources2.get(0));
        assertTrue(resources2.get(0)!= resources2.get(1));
        assertTrue(IDENTITY+1 == resources2.get(1) ||
                IDENTITY+3 == resources2.get(1));
    }

    @Test
    public void testGetWorkers(){
        WorkerInfo.Builder builder = new WorkerInfo.Builder(channel);
        WorkerInfo workerInfo1 = builder.identity(IDENTITY+1).build();
        WorkerInfo workerInfo2 = builder.identity(IDENTITY+2).build();
        workerManager.add(workerInfo1);
        workerManager.add(workerInfo2);
        List<String> identities = Arrays.asList(new String[]{IDENTITY+1,IDENTITY+2});
        List<WorkerInfo> workers = workerManager.getWorkers(identities);
        assertTrue(workers.size() == 2);
        assertTrue(workerInfo1 == workers.get(0) ||
        workerInfo2 == workers.get(0));
        assertTrue(workers.get(0) != workers.get(1));
        assertTrue(workerInfo1 == workers.get(1) ||
                workerInfo2 == workers.get(1));
    }
}
