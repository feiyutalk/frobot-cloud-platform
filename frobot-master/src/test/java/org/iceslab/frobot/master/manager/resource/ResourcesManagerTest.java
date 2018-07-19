package org.iceslab.frobot.master.manager.resource;

import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.master.MasterTestUtils;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;
import org.iceslab.frobot.master.manager.project.ProjectRuntime;
import org.iceslab.frobot.remoting.Channel;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.easymock.EasyMock.createMock;

/**
 * Created by Neuclil on 17-7-10.
 */
public class ResourcesManagerTest {
    private static final String IDENTITY = "frobot-master-1_worker-1";
    private MasterApplication application;
    private Channel channel;
    private WorkerInfo workerInfo;
    private ResourcesManager resourcesManager;

    @Before
    public void setUp(){
        channel = createMock("Channel", Channel.class);
        application = MasterTestUtils.getApplication();
        resourcesManager = new ResourcesManager(application);
    }

    @Test
    public void testGetResources(){
        ProjectRuntime.Builder builder = new ProjectRuntime.Builder();

        TaskInfo taskInfo1 = new TaskInfo();
        taskInfo1.setTaskID("1");
        TaskInfo taskInfo2 = new TaskInfo();
        taskInfo2.setTaskID("2");
        Map<TaskInfo, String> taskConfig = new HashMap<>();
        taskConfig.put(taskInfo1,"-1");
        taskConfig.put(taskInfo2,"1");

        ProjectRuntime projectRuntime = builder
                .cpu(2)
                .hardDisk(2)
                .memory(2)
                .workerNum(2)
                .taskInfos(taskConfig)
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

        application.getWorkerManager().add(workerInfo1);
        application.getWorkerManager().add(workerInfo2);
        boolean success = resourcesManager.requireResources(projectRuntime);
        assertFalse(success);

        WorkerInfo workerInfo3 = wBuilder
                .identity(IDENTITY+2)
                .cpu(3)
                .harddisk(3)
                .memory(5)
                .available(true)
                .running(false)
                .build();
        application.getWorkerManager().add(workerInfo3);
        success = resourcesManager.requireResources(projectRuntime);
        assertTrue(success);

        List<WorkerInfo> workers = resourcesManager.getWorkers("1");
        assertTrue(2 == workers.size());
        workers = resourcesManager.getWorkers("2");
        assertTrue(2 == workers.size());
    }
}
