package org.iceslab.frobot.registry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Neuclil on 17-7-22.
 */
public class ResourcesOperator {
    public static String makeRegistryConfig(String parent) throws IOException {
        String config = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<registry>\n" +
                "\t<ip>localhost</ip>\n" +
                "\t<commandPort>38100</commandPort>\n" +
                "\t<registerPort>38102</registerPort>\n" +
                "\t<invokeTimeoutMillis>60000</invokeTimeoutMillis>\n" +
                "\t<workspacePath>" + System.getProperty("user.home") + File.separator + "frobot_registry_test/workspace</workspacePath>\n" +
                "</registry>";

        File dir = new File(parent);
        dir.mkdirs();
        String pathname = parent + File.separator + "registryconfig.xml";
        FileWriter fw = new FileWriter(new File(pathname));
        fw.write(config);
        fw.close();
        return pathname;
    }
}
