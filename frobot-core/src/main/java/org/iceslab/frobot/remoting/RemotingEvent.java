package org.iceslab.frobot.remoting;

/**
 * 通信事件
 */
public class RemotingEvent {
    /* 通信事件类型 */
    private final RemotingEventType type;
    /* 通信事件远程地址 */
    private final String remoteAddr;
    /* 通信事件连接的通道 */
    private final Channel channel;

    /**
     * 构造函数
     * @param type
     * @param remoteAddr
     * @param channel
     */
    public RemotingEvent(RemotingEventType type, String remoteAddr, Channel channel) {
        this.type = type;
        this.remoteAddr = remoteAddr;
        this.channel = channel;
    }

    /************************* 	Getter & Setter	*************************/
    public RemotingEventType getType() {
        return type;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public Channel getChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return "RemotingEvent{" +
                "type=" + type +
                ", remoteAddr='" + remoteAddr + '\'' +
                ", channel=" + channel +
                '}';
    }
}