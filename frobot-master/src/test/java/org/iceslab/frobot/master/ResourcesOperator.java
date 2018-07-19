package org.iceslab.frobot.master;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResourcesOperator {

    public static String makeMasterConfig(String parent) throws IOException {
        String config = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<master>\n" +
                "\t<ip>localhost</ip>\n" +
                "\t<commandPort>18100</commandPort>\n" +
                "\t<dataPort>18101</dataPort>\n" +
                "\t<registryAddress>localhost:38102</registryAddress>\n" +
                "\t<invokeTimeoutMillis>600000</invokeTimeoutMillis>\n" +
                "\t<rootPath>"+ System.getProperty("user.home") + File.separator +"frobot_master_test</rootPath>\n"+
                "\t<workspacePath>" + System.getProperty("user.home") + File.separator + "frobot_master_test/workspace</workspacePath>\n" +
                "\t<projectEnginePath>"+ System.getProperty("user.home") + File.separator +"frobot_master_test/workspace/frobot/lib/project_engine</projectEnginePath>\n" +
                "\t<taskEnginePath>"+ System.getProperty("user.home") + File.separator +"frobot_master_test/workspace/frobot/lib/task_engine</taskEnginePath>\n" +
                "\t<defaultProjectEngines>\n" +
                "\t\t<defaultProjectEngine>\n" +
                "\t\t\t<simpleName>spider</simpleName>\n" +
                "\t\t\t<jarName>spider.jar</jarName>\n" +
                "\t\t\t<engineName>org.iceslab.spider.SpiderProjectEngine</engineName>\n" +
                "\t\t\t<defaultTaskEngines>\n" +
                "\t\t\t\t<defaultTaskEngine>\n" +
                "\t\t\t\t    <simpleName>crawling</simpleName>\n" +
                "\t\t\t\t\t<jarName>spiderCrawling.jar</jarName>\n" +
                "\t\t\t\t\t<engineName>org.iceslab.spider.CrawlingTaskEngine</engineName>\n" +
                "\t\t\t\t</defaultTaskEngine>\n" +
                "\t\t\t\t<defaultTaskEngine>\n" +
                "\t\t\t\t    <simpleName>extract</simpleName>\n" +
                "\t\t\t\t\t<jarName>spiderExtract.jar</jarName>\n" +
                "\t\t\t\t\t<engineName>org.iceslab.spider.ExtractTaskEngine</engineName>\n" +
                "\t\t\t\t</defaultTaskEngine>\n" +
                "\t\t\t\t<defaultTaskEngine>\n" +
                "\t\t\t\t    <simpleName>integrate</simpleName>\n" +
                "\t\t\t\t\t<jarName>spiderIntegrate.jar</jarName>\n" +
                "\t\t\t\t\t<engineName>org.iceslab.spider.IntegrateTaskEngine</engineName>\n" +
                "\t\t\t\t</defaultTaskEngine>\n" +
                "\t\t\t</defaultTaskEngines>\n" +
                "\t\t</defaultProjectEngine>\n" +
                "\t</defaultProjectEngines>\n" +
                "</master>\n";
        File dir = new File(parent);
        dir.mkdirs();
        String pathname = parent + File.separator + "masterconfig.xml";
        FileWriter fw = new FileWriter(new File(pathname));
        fw.write(config);
        fw.close();
        return pathname;
    }
}
