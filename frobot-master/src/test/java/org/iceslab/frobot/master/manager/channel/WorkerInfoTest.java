package org.iceslab.frobot.master.manager.channel;

import org.iceslab.frobot.remoting.Channel;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertSame;
import static org.easymock.EasyMock.createMock;

/**
 * Created by Neuclil on 17-7-10.
 */
public class WorkerInfoTest {
    private WorkerInfo workerInfo;
    private Channel channel;

    @Test
    public void testCreate(){
        channel = createMock("Channel", Channel.class);
        WorkerInfo.Builder builder  = new WorkerInfo.Builder(channel);
        WorkerInfo workerInfo = builder.running(false)
                                    .available(false)
                                    .cpu(1)
                                    .harddisk(500)
                                    .memory(500)
                                    .tasks(1)
                                    .build();
        assertEquals(false, workerInfo.isRunning());
        assertEquals(false, workerInfo.isAvailable());
        assertEquals(1, workerInfo.getCpu());
        assertEquals(500, workerInfo.getHarddisk());
        assertEquals(500, workerInfo.getMemory());
        assertSame(channel, workerInfo.getChannel());
    }
}
