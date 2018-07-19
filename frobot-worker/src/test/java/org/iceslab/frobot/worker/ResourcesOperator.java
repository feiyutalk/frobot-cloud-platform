package org.iceslab.frobot.worker;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResourcesOperator {

    public static String makeMasterConfig(String parent) throws IOException {
        String config = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<worker>\n" +
                "\t<ip>localhost</ip>\n" +
                "\t<commandPort>28100</commandPort>\n" +
                "\t<dataPort>28101</dataPort>\n" +
                "\t<registryAddress>localhost:38100</registryAddress>\n" +
                "\t<invokeTimeoutMillis>60000</invokeTimeoutMillis>\n" +
                "\t<rootPath>" + System.getProperty("user.home") + File.separator + "frobot_worker_test</rootPath>\n" +
                "\t<workspacePath>" + System.getProperty("user.home") + File.separator + "frobot_worker_test/workspace</workspacePath>\n" +
                "\t<taskEnginePath>" + System.getProperty("user.home") + File.separator + "frobot_worker_test/workspace/frobot/lib/task_engine</taskEnginePath>\n" +
                "\t<retryTimes>5</retryTimes>\n" +
                "</worker>\n";
        File dir = new File(parent);
        dir.mkdirs();
        String pathname = parent + File.separator + "masterconfig.xml";
        FileWriter fw = new FileWriter(new File(pathname));
        fw.write(config);
        fw.close();
        return pathname;
    }
}