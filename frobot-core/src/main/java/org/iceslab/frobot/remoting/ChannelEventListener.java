package org.iceslab.frobot.remoting;

/**
 * 通信管道事件的监听器
 */
public interface ChannelEventListener {
    /* 建立通信连接 */
    public void onChannelConnect(final String remoteAddr, final Channel channel);
    /* 关闭通信连接 */
    public void onChannelClose(final String remoteAddr, final Channel channel);
    /* 发生通信异常 */
    public void onChannelException(final String remoteAddr, final Channel channel);
    /* 发生通信闲置 比如 对方长时间没有发生读、写事件 */
    public void onChannelIdle(IdleState idleState, final String remoteAddr, final Channel channel);

}