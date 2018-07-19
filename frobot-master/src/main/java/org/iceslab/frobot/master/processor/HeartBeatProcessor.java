package org.iceslab.frobot.master.processor;

import org.apache.log4j.Logger;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.cluster.NodeType;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.CommandBodyWrapper;
import org.iceslab.frobot.remoting.command.body.request.HeartBeatRequestBody;
import org.iceslab.frobot.remoting.command.body.response.NullResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

/**
 * Heart beat request processor.
 * @auther Neuclil
 */
public class HeartBeatProcessor implements RemotingProcessor {
    private static final Logger LOGGER = Logger.getLogger(HeartBeatProcessor.class);
    private MasterApplication application;
    private WorkerInfo workerInfo;

    public HeartBeatProcessor(MasterApplication application) {
        this.application = application;
    }

    /**
     *
     * @param channel channel related to the request
     * @param request request embody the heart beat information.
     * @return response command represents process result.
     * @throws RemotingCommandException
     */
    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
        HeartBeatRequestBody body = (HeartBeatRequestBody) request.getBody();
        LOGGER.info("HeartBeatProcessor receive heart beat and start to process it, the information :" + body.toString());
        workerInfo = getInfo(channel, body);
        application.getWorkerManager().add(workerInfo);
        LOGGER.info("HeartBeatProcessor update worker information successfully. Now, the worker pool: " + application.getWorkerManager().showInfo());
        RemotingCommand response = RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.HEART_BEAT_SUCCESS.code(),
                CommandBodyWrapper.wrapper(application.getMasterNodeConfig(),new NullResponseBody()));
        return response;
    }

    private WorkerInfo getInfo(Channel channel, HeartBeatRequestBody body){
        WorkerInfo.Builder build = new WorkerInfo.Builder(channel);
        WorkerInfo workerInfo = build.available(true)
                .cpu(body.getCpu())
                .harddisk(body.getHarddisk())
                .memory(body.getMemory())
                .running(body.isRunning())
                .available(body.isAvailable())
                .identity(body.getIdentity())
                .nodeType(NodeType.WORKER)
                .build();
        return workerInfo;
    }

    public WorkerInfo getWorkerInfo() {
        return workerInfo;
    }
}
