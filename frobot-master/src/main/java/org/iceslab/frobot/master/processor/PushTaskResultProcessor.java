package org.iceslab.frobot.master.processor;

import org.apache.log4j.Logger;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.cluster.WorkerTaskResult;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.request.PushTaskResultRequestBody;
import org.iceslab.frobot.remoting.command.body.response.NullResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-16.
 */
public class PushTaskResultProcessor implements RemotingProcessor{
    private static final Logger LOGGER = Logger.getLogger(PushTaskResultProcessor.class);
    private MasterApplication application;

    public PushTaskResultProcessor(MasterApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        LOGGER.debug("PushTaskResultProcessor开始处理请求...");
        PushTaskResultRequestBody body = (PushTaskResultRequestBody) request.getBody();
        WorkerTaskResult workerTaskResult = body.getWorkerTaskResult();
        new Thread(new Runnable() {
            @Override
            public void run() {
                application.getProjectManager().processTaskResult(workerTaskResult);
            }
        }).start();
        RemotingCommand response = RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.PUSH_TASK_RESULT_SUCCESS.code(),
                new NullResponseBody());
        LOGGER.debug("PushTaskResultProcessor开始处理完成!");
        return response;
    }
}
