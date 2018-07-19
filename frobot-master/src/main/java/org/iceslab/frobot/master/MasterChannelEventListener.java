package org.iceslab.frobot.master;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.ChannelEventListener;
import org.iceslab.frobot.remoting.IdleState;

/**
 * The Listener is used for remote event. for now, it's only listen the close event.
 * for future, it will support other remote event.
 * @auther Neuclil
 */
public class MasterChannelEventListener implements ChannelEventListener{
    private MasterApplication application;

    public MasterChannelEventListener(MasterApplication application){
        this.application = application;
    }

    @Override
    public void onChannelConnect(String remoteAddr, Channel channel) {
        throw new UnsupportedOperationException();
    }

    /**
     * If the on close remote event take place, the method will be call.
     * The method will remove the channel from worker queue through worker manager.
     * @param remoteAddr remote address that the event related to
     * @param channel  remote channel that the event related to.
     */
    @Override
    public void onChannelClose(String remoteAddr, Channel channel) {
        application.getWorkerManager().remove(channel);
    }

    @Override
    public void onChannelException(String remoteAddr, Channel channel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void onChannelIdle(IdleState idleState, String remoteAddr, Channel channel) {
        throw new UnsupportedOperationException();
    }
}
