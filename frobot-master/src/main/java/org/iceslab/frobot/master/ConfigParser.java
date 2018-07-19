package org.iceslab.frobot.master;

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

    public static Map<String, String> parseBasicConfig(String masterConfigFile) throws XMLFileNotMatchException {
        Map<String, String> result = null;
        try {
            result = new HashMap<>();
            XMLParseUtil masterConfig = XMLParseUtil.createReadRoot(masterConfigFile);
            result.put("ip", masterConfig.getChild("ip").getTextData());
            result.put("commandPort", masterConfig.getChild("commandPort").getTextData());
            result.put("dataPort", masterConfig.getChild("dataPort").getTextData());
            result.put("registryAddress", masterConfig.getChild("registryAddress").getTextData());
            result.put("invokeTimeoutMillis", masterConfig.getChild("invokeTimeoutMillis").getTextData());
            result.put("rootPath", masterConfig.getChild("rootPath").getTextData());
            result.put("workspacePath", masterConfig.getChild("workspacePath").getTextData());
            result.put("projectEnginePath", masterConfig.getChild("projectEnginePath").getTextData());
            result.put("taskEnginePath", masterConfig.getChild("taskEnginePath").getTextData());
        } catch (IOException e) {
            LOGGER.error("failure to read default projectEngines information from masterconfig.xml", e);
            throw new XMLFileNotMatchException();
        }
        return result;
    }

    public static Map<String, DefaultProjectEngineInfo> parseDefaultProjectEngineConfig(String file)
            throws XMLFileNotMatchException {
        try {
            Map<String, DefaultProjectEngineInfo> defaultProjectEngine = new HashMap<>();
            XMLParseUtil projectInfoFile = XMLParseUtil.createReadRoot(file);
            XMLParseUtil defaultProjectEnginesItem = projectInfoFile.getChild("defaultProjectEngines");
            XMLParseUtil[] defaultProjectEngineItems = defaultProjectEnginesItem.getChildren("defaultProjectEngine");
            for (XMLParseUtil defaultProjectEngineItem : defaultProjectEngineItems) {
                DefaultProjectEngineInfo defaultProjectEngineInfo = new DefaultProjectEngineInfo();
                defaultProjectEngineInfo.setProjectSimpleName(defaultProjectEngineItem.getChild("simpleName").getTextData());
                defaultProjectEngineInfo.setProjectJarName(defaultProjectEngineItem.getChild("jarName").getTextData());
                defaultProjectEngineInfo.setProjectEngineName(defaultProjectEngineItem.getChild("engineName").getTextData());
                defaultProjectEngine.put(defaultProjectEngineItem.getChild("simpleName").getTextData(),
                        defaultProjectEngineInfo);
            }
            return defaultProjectEngine;
        } catch (IOException e) {
            LOGGER.error("failure to read default projectEngines information from masterconfig.xml", e);
            throw new XMLFileNotMatchException();
        }
    }

    public static Map<String, Map<String, DefaultTaskEngineInfo>> parseDefaultTaskEngineConfig(String file)
            throws XMLFileNotMatchException {
        try {
            Map<String, Map<String, DefaultTaskEngineInfo>> taskEnginesOfProjectEngines = new HashMap<>();
            XMLParseUtil masterfile = XMLParseUtil.createReadRoot(file);
            XMLParseUtil defaultProjectEnginesItem = masterfile.getChild("defaultProjectEngines");
            XMLParseUtil[] defaultProjectEngineItems = defaultProjectEnginesItem.getChildren("defaultProjectEngine");
            for (XMLParseUtil defaultProjectEngineItem : defaultProjectEngineItems) {
                Map<String, DefaultTaskEngineInfo> defaultEngines = new HashMap<>();
                XMLParseUtil simpleName = defaultProjectEngineItem.getChild("simpleName");
                String projectSimpleName = simpleName.getTextData();
                XMLParseUtil defaultTaskEnginesItem = defaultProjectEngineItem.getChild("defaultTaskEngines");
                XMLParseUtil[] defaultTaskEngineItems = defaultTaskEnginesItem.getChildren("defaultTaskEngine");
                for (XMLParseUtil defaultTaskEngineItem : defaultTaskEngineItems) {
                    DefaultTaskEngineInfo defaultTaskEngineInfo = new DefaultTaskEngineInfo();
                    defaultTaskEngineInfo.setTaskSimpleName(defaultTaskEngineItem.getChild("simpleName").getTextData());
                    defaultTaskEngineInfo.setTaskJarName(defaultTaskEngineItem.getChild("jarName").getTextData());
                    defaultTaskEngineInfo.setTaskEngineName(defaultTaskEngineItem.getChild("engineName").getTextData());
                    defaultEngines.put(defaultTaskEngineItem.getChild("simpleName").getTextData(),
                            defaultTaskEngineInfo);
                }
                taskEnginesOfProjectEngines.put(projectSimpleName, defaultEngines);
            }
            return taskEnginesOfProjectEngines;
        } catch (IOException e) {
            LOGGER.error("failure to read default taskEngines information from masterconfig.xml", e);
            throw new XMLFileNotMatchException();
        }
    }
}
