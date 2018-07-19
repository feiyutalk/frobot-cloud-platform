package org.iceslab.frobot.commons.utils.general;

import org.apache.log4j.Logger;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.ChannelHandlerListener;
import org.iceslab.frobot.remoting.Future;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.RemotingCommandType;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class RemotingHelper {
    private static final Logger LOGGER = Logger.getLogger(RemotingHelper.class);

    private RemotingHelper(){}

    public static boolean isRequestCommand(RemotingCommand cmd) {
        return cmd.getType() == RemotingCommandType.REQUEST_COMMAND;
    }

    /**
     * 判断该通信实体是否是单向传输的
     *
     * @param cmd
     * @return
     */
    public static boolean isOnewayCommand(RemotingCommand cmd) {
        return cmd.isOneway();
    }

    public static void markResponseType(RemotingCommand cmd) {
        cmd.setType(RemotingCommandType.RESPONSE_COMMAND);
    }

    public static void markOnewayRPC(RemotingCommand cmd) {
        cmd.setOneway(true);
    }

    public static String ipAndPort2Addr(String ip, int port){
        StringBuffer sb = new StringBuffer();
        sb.append(ip);
        sb.append(":");
        sb.append(port);
        String addr = sb.toString();
        return addr;
    }

    public static SocketAddress string2SocketAddress(final String addr) {
        String[] s = addr.split(":");
        return new InetSocketAddress(s[0], Integer.valueOf(s[1]));
    }

    public static RemotingCommandType getRemotingCommandType(RemotingCommand cmd) {
        return cmd.getType();
    }

    public static String parseChannelRemoteAddr(Channel channel) {
        if (null == channel) {
            return "";
        }
        final SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }

            return addr;
        }

        return "";
    }

    public static void closeChannel(Channel channel) {
        final String addrRemote = RemotingHelper.parseChannelRemoteAddr(channel);
        channel.close().addListener(new ChannelHandlerListener() {
            @Override
            public void operationComplete(Future future) throws Exception {
                LOGGER.info("closeChannel:关闭远程连接 " + addrRemote + " " + future.isSuccess());
            }
        });
    }

}
