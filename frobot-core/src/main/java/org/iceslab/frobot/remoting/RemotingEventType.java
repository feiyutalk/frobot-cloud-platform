package org.iceslab.frobot.remoting;

/**
 * 通信事件类型
 */
public enum RemotingEventType {
    /* 建立连接 */
    CONNECT,
    /* 关闭连接 */
    CLOSE,
    /* 读空闲 */
    READER_IDLE,
    /* 写空闲 */
    WRITER_IDLE,
    /* 读写空闲 */
    ALL_IDLE,
    /* 异常 */
    EXCEPTION
}