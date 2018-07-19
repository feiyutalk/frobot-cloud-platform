package org.iceslab.frobot.registry.processor;

import org.apache.log4j.Logger;
import org.iceslab.frobot.registry.RegistryApplication;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.CommandBodyWrapper;
import org.iceslab.frobot.remoting.command.body.response.FindAddrResponseBody;
import org.iceslab.frobot.remoting.command.body.response.NullResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

public class RequireAddrProcessor implements RemotingProcessor {
    private static final Logger LOGGER = Logger.getLogger(RequireAddrProcessor.class);
    private RegistryApplication application;

    public RequireAddrProcessor(RegistryApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
        LOGGER.debug("RequireAddrProcessor开始分配Master地址...");
        RemotingCommand response = null;
        if (application.getMasterManager().isEmpty()) {
            response = RemotingCommand.createResponseCommand(
                    RemotingProtos.ResponseCode.REQUEST_ADDR_FAILURE.code(),
                    CommandBodyWrapper.wrapper(
                            application.getNodeConfig(),
                            new NullResponseBody()));
            LOGGER.warn("没有可分配的Master地址！");
        } else {
            String masterAddr = application.getMasterManager().findMaster();
            FindAddrResponseBody body = new FindAddrResponseBody(masterAddr);
            response = RemotingCommand.createResponseCommand(
                    RemotingProtos.ResponseCode.REQUEST_ADDR_SUCCESS.code(),
                    CommandBodyWrapper.wrapper(
                            application.getNodeConfig(),
                            body));
            LOGGER.debug("给Worker分配Master地址成功！" + body.getAddress());
        }
        return response;
    }
}
