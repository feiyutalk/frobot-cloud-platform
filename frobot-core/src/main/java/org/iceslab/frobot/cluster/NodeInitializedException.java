package org.iceslab.frobot.cluster;

/**
 * Created by Neuclil on 17-7-9.
 */
public class NodeInitializedException extends Exception{
    public NodeInitializedException(String message) {
        super(message);
    }

    public NodeInitializedException(Throwable cause) {
        super(cause);
    }

    public NodeInitializedException() {
        super();
    }
}
