package org.iceslab.frobot.remoting;

public interface Future {

    boolean isSuccess();

	Throwable cause();
}
