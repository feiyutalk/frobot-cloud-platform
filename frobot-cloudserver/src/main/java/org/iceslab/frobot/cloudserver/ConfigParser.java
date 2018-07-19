package org.iceslab.frobot.cloudserver;

import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.commons.exception.XMLFileNotMatchException;
import org.iceslab.frobot.commons.utils.general.XMLParseUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Neuclil on 17-7-22.
 */
class ConfigParser {

    public static Map<String, String> parseBasicConfig(String file) throws XMLFileNotMatchException {
        Map<String, String> result = new HashMap<>();
        try {
            XMLParseUtil root = XMLParseUtil.createReadRoot(file);
            String rootNode = root.getType();
            if (rootNode.equals("cloudserver")) {
                XMLParseUtil ipItem = root.getChild("ip");
                result.put("ip", ipItem.getTextData());
                XMLParseUtil commandPortItem = root.getChild("commandPort");
                result.put("commandPort", commandPortItem.getTextData());
                XMLParseUtil dataPortItem = root.getChild("dataPort");
                result.put("dataPort", dataPortItem.getTextData());
                XMLParseUtil invokeTimeoutMillisItem = root.getChild("invokeTimeoutMillis");
                result.put("invokeTimeoutMillis", invokeTimeoutMillisItem.getTextData());
                XMLParseUtil workspacePathItem = root.getChild("workspacePath");
                result.put("workspacePath", workspacePathItem.getTextData());
            } else {
                throw new XMLFileNotMatchException();
            }
        } catch (IOException e) {
            throw new XMLFileNotMatchException();
        }
        return result;
    }

    public static Map<String, String> parseMasterInfo(String file) throws XMLFileNotMatchException {
        Map<String, String> info = new HashMap<>();
        try {
            XMLParseUtil masterInfoFile = XMLParseUtil.createReadRoot(file);
            XMLParseUtil master = masterInfoFile.getChild("master");
            info.put("ip", master.getChild("ip").getTextData());
            info.put("port", master.getChild("port").getTextData());
        } catch (IOException e) {
            throw new XMLFileNotMatchException();
        }
        return info;
    }

    public static Project parseProjectConfig(String file) throws IOException {
        Project project = new Project();
        XMLParseUtil root = XMLParseUtil.createReadRoot(file);
        project.setUserName(root.getString("userName"));
        project.setProjectName(root.getString("projectName"));
        project.setProjectSimpleName(root.getString("projectSimpleName"));
        project.setPersonalProjectEngine(Boolean.valueOf(root.getString("personalProjectEngine")));
        project.setProjectJarName(root.getString("projectJarName"));
        project.setProjectEngineClassWithPackageName(root.getString("projectEngineClassWithPackageName"));
        project.setStartType(root.getString("startType"));
        project.setStartDelayTime(Long.valueOf(root.getString("startDelayTime")));
        XMLParseUtil[] tasks = root.getChildren("task");
        String[] tasksJarName = new String[tasks.length];
        for(int i=0; i<tasks.length; i++){
            tasksJarName[i] = tasks[i].getString("taskJarName");
        }
        project.setTaskJarName(tasksJarName);
        project.setProjectId(project.getUserName() + "_" + project.getProjectName());
        return project;
    }
}
