package org.iceslab.frobot.commons.constants;

import java.io.File;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public interface Constants {
    /********************    			 通信相关   				 ********************/
    //通信处理器的线程池，默认的线程池大小
    int DEFAULT_PROCESSOR_THREAD = 32 + Runtime.getRuntime().availableProcessors() * 5;
    //同步阻塞的最大时间
    int DEFAULT_INVOKE_TIMEOUT_MILLIS = 20000;
    //默认最大的Buffer大小，Netty解码时需要使用
    int DEFAULT_BUFFER_SIZE = 16 * 1024;

    int REGISTRY_DEFAULT_LISETN_PORT = 8000;

    int REGISTRY_DEFAULT_REGISTER_PORT = 8002;

    String DEFAULT_CLUSTER_NAME = "frobot_cluster";
}