package org.iceslab.frobot.worker;

import org.apache.log4j.Logger;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.utils.general.XMLParseUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neuclil on 17-7-22.
 */
class ConfigParser {
    private static Logger LOGGER = Logger.getLogger(ConfigParser.class);

    /**
     *
     * @param file
     * @return
     * @throws XMLFileNotMatchException
     */
    public static Map<String, String> parseBasicConfig(String file) throws XMLFileNotMatchException {
        try {
            Map<String, String> result = new HashMap<>();
            XMLParseUtil root = XMLParseUtil.createReadRoot(file);
            result.put("ip", root.getChild("ip").getTextData());
            result.put("commandPort", root.getChild("commandPort").getTextData());
            result.put("dataPort", root.getChild("dataPort").getTextData());
            result.put("registryAddress", root.getChild("registryAddress").getTextData());
            result.put("invokeTimeoutMillis", root.getChild("invokeTimeoutMillis").getTextData());
            result.put("rootPath", root.getChild("rootPath").getTextData());
            result.put("workspacePath", root.getChild("workspacePath").getTextData());
            result.put("taskEnginePath", root.getChild("taskEnginePath").getTextData());
            result.put("retryTimes", root.getChild("retryTimes").getTextData());
            return result;
        } catch (IOException e) {
            LOGGER.error("failure to read basic information from workerconfig.xml", e);
            throw new XMLFileNotMatchException();
        }
    }
}
