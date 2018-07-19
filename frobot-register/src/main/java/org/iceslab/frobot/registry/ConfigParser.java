package org.iceslab.frobot.registry;

import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.utils.general.XMLParseUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Neuclil on 17-7-22.
 */
class ConfigParser {

    /**
     *
     * @param file
     * @return
     * @throws XMLFileNotMatchException
     */
    public static Map<String, String> parseRegistryConfig(String file) throws XMLFileNotMatchException {
        Map<String, String> result = null;
        try {
            result = new HashMap<String, String>();
            XMLParseUtil root = XMLParseUtil.createReadRoot(file);
            String rootNode = root.getType();
            if (rootNode.equals("registry")) {
                result.put("ip", root.getChild("ip").getTextData());
                result.put("commandPort", root.getChild("commandPort").getTextData());
                result.put("registerPort", root.getChild("registerPort").getTextData());
                result.put("invokeTimeoutMillis", root.getChild("invokeTimeoutMillis").getTextData());
                result.put("workspacePath", root.getChild("workspacePath").getTextData());
            } else {
                throw new XMLFileNotMatchException();
            }
        } catch (IOException e) {
            throw new XMLFileNotMatchException();
        }
        return result;
    }
}
