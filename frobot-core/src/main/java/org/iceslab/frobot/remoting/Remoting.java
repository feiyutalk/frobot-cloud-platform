package org.iceslab.frobot.remoting;

import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.exception.RemotingException;
import org.iceslab.frobot.remoting.exception.RemotingSendRequestException;
import org.iceslab.frobot.remoting.exception.RemotingTimeoutException;
import org.iceslab.frobot.remoting.exception.RemotingTooMuchRequestException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.util.concurrent.ExecutorService;

public interface Remoting {

    public void start() throws RemotingException;

    public void shutdown();

    public void processRequestCommand(final Channel channel, final RemotingCommand cmd);

    public void processResponseCommand(Channel channel, RemotingCommand cmd);

    public void invokeOneway(final Channel channel, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingTooMuchRequestException, RemotingTimeoutException,
            RemotingSendRequestException;

    public RemotingCommand invokeSync(final Channel channel, final RemotingCommand request, final long timeoutMillis)
            throws InterruptedException, RemotingSendRequestException, RemotingTimeoutException;

    public void invokeAsync(final Channel channel, final RemotingCommand request, final long timeoutMillis,
                            final AsyncCallback asyncCallback) throws InterruptedException, RemotingTooMuchRequestException,
            RemotingTimeoutException, RemotingSendRequestException;

    public void registerDefaultProcessor(final RemotingProcessor processor, final ExecutorService executor);
}
