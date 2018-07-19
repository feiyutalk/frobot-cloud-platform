package org.iceslab.frobot.remoting.processor;

import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;

public interface RemotingProcessor {
    RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException;
}
