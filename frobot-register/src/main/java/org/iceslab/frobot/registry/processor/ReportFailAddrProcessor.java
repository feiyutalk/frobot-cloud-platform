package org.iceslab.frobot.registry.processor;

import org.apache.log4j.Logger;
import org.iceslab.frobot.registry.RegistryApplication;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.request.ReportFailMasterRequestBody;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

/**
 * Created by Neuclil on 17-4-18.
 */
public class ReportFailAddrProcessor implements RemotingProcessor {
    private static final Logger LOGGER = Logger.getLogger(ReportFailAddrProcessor.class);
    private RegistryApplication application;

    public ReportFailAddrProcessor(RegistryApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request)
            throws RemotingCommandException {
        LOGGER.debug("ReportFailAddrProcessor正在处理请求...");
        ReportFailMasterRequestBody body = (ReportFailMasterRequestBody) request.getBody();
        String failMasterAddr = body.getFailMasterAddr();
        application.getMasterManager().removeMaster(failMasterAddr);
        LOGGER.debug("ReportFailAddrProcessor处理请求完成!");
        return null;
    }
}
