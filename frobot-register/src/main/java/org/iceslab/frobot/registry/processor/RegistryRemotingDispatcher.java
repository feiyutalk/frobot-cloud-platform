package org.iceslab.frobot.registry.processor;

import org.iceslab.frobot.registry.RegistryApplication;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neuclil on 17-4-12.
 */
public class RegistryRemotingDispatcher implements RemotingProcessor {
	
    private final Map<RemotingProtos.RequestCode, RemotingProcessor> processors =
            new HashMap<RemotingProtos.RequestCode, RemotingProcessor>();
    private RegistryApplication application;

    public RegistryRemotingDispatcher(RegistryApplication application) {
        this.application = application;
        processors.put(RemotingProtos.RequestCode.REQUEST_MASTER_ADDR,
                new RequireAddrProcessor(application));
        processors.put(RemotingProtos.RequestCode.FAIL_MASTER_ADDR,
                new ReportFailAddrProcessor(application));
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
        RemotingProtos.RequestCode code = RemotingProtos.RequestCode.valueOf(request.getCode());
        RemotingProcessor processor = processors.get(code);
        if (processor == null) {
            return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.REQUEST_CODE_NOT_SUPPORTED.code(),
            		                                     "request code not supported!");
        }
        return processor.processRequest(channel, request);
    }
}
