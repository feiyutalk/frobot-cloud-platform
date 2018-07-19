package org.iceslab.frobot.master.processor;

import org.apache.log4j.Logger;

import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.CommandBodyWrapper;
import org.iceslab.frobot.remoting.command.body.request.HeartBeatRequestBody;
import org.iceslab.frobot.remoting.command.body.response.IdResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

/**
 * Worker连接处理器 Worker在注册中心找到Master地址后
 * 需要首先向Master发送一个带有Connect指令的消息,该处理器负责处理这个通信消息
 */
public class ConnectProcessor implements RemotingProcessor {
    private static final Logger LOGGER = Logger.getLogger(ConnectProcessor.class);
    private MasterApplication application;
    private WorkerInfo workerInfo;

    public ConnectProcessor(MasterApplication application) {
        this.application = application;
    }

    /**
     * process first connect worker's request, the process will store the
     * worker's information in the worker queue, and allocate
     * the unique identity to the worker.
     * @param channel
     * @param request
     * @return Response that indicate the process result of  the request.
     * @throws RemotingCommandException
     */
    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request){
        LOGGER.info("ConnectProcessor begin to process request.");
        workerInfo = createWorkerInfoFromRequest(channel, request);
        String allocateIdentity = application.getWorkerManager().add(workerInfo);
        IdResponseBody body = new IdResponseBody(allocateIdentity);
        LOGGER.info("process worker connect successfully, worker queue information: "
                + application.getWorkerManager().showInfo());
        return RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.CONNECT_SUCCESS.code(),
                CommandBodyWrapper.wrapper(application.getMasterNodeConfig(),body));
    }

    /**
     * create the worker information from the request. the method extract worker
     * information from request and add some extra information to construct the
     * worker information that master node need to store.
     * @param channel the remote channel
     * @param request the request that contains worker information
     * @return the worker information.
     */
    private WorkerInfo createWorkerInfoFromRequest(Channel channel, RemotingCommand request){
        HeartBeatRequestBody requestBody = (HeartBeatRequestBody)request.getBody();
        int cpu = requestBody.getCpu();
        int memory = requestBody.getMemory();
        int harddisk = requestBody.getHarddisk();
        WorkerInfo workerInfo = new WorkerInfo.Builder(channel)
                .available(true)
                .running(false)
                .cpu(cpu)
                .memory(memory)
                .harddisk(harddisk)
                .identity(requestBody.getIdentity())
                .build();
        return workerInfo;
    }

    public WorkerInfo getWorkerInfo() {
        return workerInfo;
    }
}
