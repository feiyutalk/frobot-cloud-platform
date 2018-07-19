package org.iceslab.frobot.master.processor;

import org.iceslab.frobot.master.MasterTestUtils;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.RemotingCommandBody;
import org.iceslab.frobot.remoting.command.body.request.HeartBeatRequestBody;
import org.iceslab.frobot.remoting.command.body.response.IdResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.easymock.EasyMock.createMock;

/**
 * Created by Neuclil on 17-7-9.
 */
public class ConnectProcessorTest {
    private ConnectProcessor connectProcessor;
    private RemotingCommand request;
    private HeartBeatRequestBody body;
    private Channel channel;

    @Before
    public void setUp(){
        connectProcessor = new ConnectProcessor(MasterTestUtils.getApplication());
        channel = createMock("Channel",Channel.class);
        body = new HeartBeatRequestBody();
        body.setCpu(1).setHarddisk(500).setMemory(8).setIdentity("noid");
        request = RemotingCommand.createRequestCommand(
                RemotingProtos.RequestCode.CONNECT.code(), body);
    }

    @Test
    public void testProcess(){
        RemotingCommand response = connectProcessor.processRequest(channel, request);
        WorkerInfo workerInfo = connectProcessor.getWorkerInfo();
        assertEquals(true, workerInfo.isAvailable());
        assertEquals(false, workerInfo.isRunning());
        assertEquals(1, workerInfo.getCpu());
        assertEquals(8, workerInfo.getMemory());
        assertEquals(500, workerInfo.getHarddisk());
        assertEquals("frobot-master-1_worker-1", workerInfo.getIdentity());

        RemotingCommandBody body = response.getBody();
        assertEquals(IdResponseBody.class, body.getClass());
        IdResponseBody idBody = (IdResponseBody) body;
        assertEquals("frobot-master-1_worker-1", idBody.getAllocateIdentity());
        assertEquals(RemotingProtos.ResponseCode.CONNECT_SUCCESS.code(),
                response.getCode());
    }
}
