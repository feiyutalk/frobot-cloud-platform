package org.iceslab.frobot.worker.processor;

import org.iceslab.frobot.worker.WorkerApplication;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos.RequestCode;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.util.HashMap;
import java.util.Map;

public class WorkerRemotingDispatcher implements RemotingProcessor {
    private WorkerApplication application;
    private final Map<RequestCode, RemotingProcessor> processors = new HashMap<RequestCode, RemotingProcessor>();

    public WorkerRemotingDispatcher(WorkerApplication application) {
        this.application = application;
        processors.put(RequestCode.PUSH_TASK, new TaskPushProcessor(application));
        processors.put(RequestCode.PUSH_TASK_DATA, new TaskDataPushProcessor(application));
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
        RequestCode code = RequestCode.valueOf(request.getCode());
        RemotingProcessor processor = processors.get(code);
        if (processor == null) {
            return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.REQUEST_CODE_NOT_SUPPORTED.code(), "request code not supported!");
        }
        return processor.processRequest(channel, request);
    }

}