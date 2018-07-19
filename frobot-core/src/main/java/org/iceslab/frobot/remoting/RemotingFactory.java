package org.iceslab.frobot.remoting;

public interface RemotingFactory {

    RemotingServer createRemotingServer(RemotingServerConfig remotingServerConfig);

    RemotingClient createRemotingClient(RemotingClientConfig remotingClientConfig);
}
