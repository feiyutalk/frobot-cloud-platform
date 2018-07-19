package org.iceslab.frobot.remoting.command.body;

import org.iceslab.frobot.cluster.NodeConfig;

public class CommandBodyWrapper {

    private CommandBodyWrapper(){}

    public static <T extends AbstractRemotingCommandBody> T wrapper(NodeConfig config, T commandBody) {
        commandBody.setNodeType(config.getNodeType().name());
        commandBody.setIdentity(config.getIdentity());
        return commandBody;
    }

}