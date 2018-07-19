package org.iceslab.frobot.master.processor;

import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.master.MasterTestUtils;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;
import org.iceslab.frobot.master.manager.channel.WorkerManager;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.RemotingCommandBody;
import org.iceslab.frobot.remoting.command.body.request.HeartBeatRequestBody;
import org.iceslab.frobot.remoting.command.body.response.NullResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.*;
import static org.easymock.EasyMock.createMock;

/**
 * Created by Neuclil on 17-7-10.
 */
public class HeartBeatProcessorTest {
    private static final String IDENTITY = "frobot-master-1_worker-1";
    private HeartBeatProcessor heartBeatProcessor;
    private RemotingCommand request;
    private HeartBeatRequestBody body;
    private Channel channel;
    private MasterApplication application;

    @Before
    public void setUp(){
        application = MasterTestUtils.getApplication();
        heartBeatProcessor = new HeartBeatProcessor(application);
        channel = createMock("Channel",Channel.class);
        body = new HeartBeatRequestBody();
        body.setCpu(1)
                .setAvailable(true)
                .setRunning(false)
                .setHarddisk(500)
                .setMemory(8)
                .setIdentity(IDENTITY);
        request = RemotingCommand.createRequestCommand(
                RemotingProtos.RequestCode.HEART_BEAT.code(), body);
    }

    @Test
    public void testProcess() throws RemotingCommandException {
        RemotingCommand response = heartBeatProcessor.processRequest(channel, request);
        WorkerInfo hbWorkerInfo = heartBeatProcessor.getWorkerInfo();

        assertEqualsHBWorkerInfo(body, hbWorkerInfo);

        assertEqualsResponse(response);

        assertEqualsWMWorkerInfo(hbWorkerInfo);

        assertTrue(application.getWorkerManager().size() == 1);

        body.setCpu(2)
                .setAvailable(false)
                .setRunning(true)
                .setHarddisk(501)
                .setMemory(9)
                .setIdentity(IDENTITY);
        request = RemotingCommand.createRequestCommand(
                RemotingProtos.RequestCode.HEART_BEAT.code(), body);
        RemotingCommand response2 = heartBeatProcessor.processRequest(channel, request);
        WorkerInfo hbWorkerInfo2 = heartBeatProcessor.getWorkerInfo();

        assertEqualsHBWorkerInfo(body, hbWorkerInfo2);

        assertEqualsResponse(response2);

        assertEqualsWMWorkerInfo(hbWorkerInfo2);

        assertTrue(application.getWorkerManager().size() == 1);
    }

    private void assertEqualsHBWorkerInfo(HeartBeatRequestBody body, WorkerInfo hbWorkerInfo){
        assertEquals(body.isAvailable(), hbWorkerInfo.isAvailable());
        assertEquals(body.isRunning(), hbWorkerInfo.isRunning());
        assertEquals(body.getCpu(), hbWorkerInfo.getCpu());
        assertEquals(body.getMemory(), hbWorkerInfo.getMemory());
        assertEquals(body.getHarddisk(), hbWorkerInfo.getHarddisk());
        assertEquals(NodeType.WORKER, hbWorkerInfo.getNodeType());
        assertEquals(IDENTITY, hbWorkerInfo.getIdentity());
        assertSame(channel, hbWorkerInfo.getChannel());
    }

    private void assertEqualsResponse(RemotingCommand response){
        RemotingCommandBody rpBody = response.getBody();
        assertEquals(NullResponseBody.class, rpBody.getClass());
        assertEquals(RemotingProtos.ResponseCode.HEART_BEAT_SUCCESS.code(),
                response.getCode());
    }

    private void assertEqualsWMWorkerInfo(WorkerInfo workerInfo){
        WorkerManager workerManager = application.getWorkerManager();
        WorkerInfo wmWorkerInfo = workerManager.get(IDENTITY);
        assertNotNull(wmWorkerInfo);
        assertEquals(workerInfo, wmWorkerInfo);
    }
}
