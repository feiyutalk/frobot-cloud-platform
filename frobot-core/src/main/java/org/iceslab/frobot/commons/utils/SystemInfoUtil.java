package org.iceslab.frobot.commons.utils;

import org.iceslab.frobot.cluster.RemoteWorkerInfo;
import oshi.json.SystemInfo;
import oshi.json.util.PropertiesUtil;

import javax.json.*;
import java.util.Map;
import java.util.Properties;

/**
 * Created by loggerhead on 3/8/17.
 */
public class SystemInfoUtil {
    //配置系统信息的输出
    private static final String properties_path = "oshi.json.properties";
    private static SystemInfo info = null;
    private static Properties properties = null;

    public static JsonObject getAllInfo() {
        JsonObject systemInfo = SystemInfoUtil.getJson();
        JsonObject tasksInfo = getTasksInfo();
        JsonObjectBuilder infoBuilder = Json.createObjectBuilder();
        for (Map.Entry<String, JsonValue> entry : systemInfo.entrySet()) {
            infoBuilder.add(entry.getKey(), entry.getValue());
        }
        infoBuilder.add("tasks", tasksInfo);

        return infoBuilder.build();
    }

    public static RemoteWorkerInfo getWorkerInfo() {
        JsonObject j = getAllInfo();
        JsonObject hardware = j.getJsonObject("hardware");
        JsonObject processor = hardware.getJsonObject("processor");
        JsonArray fileStores = j.getJsonObject("operatingSystem")
                .getJsonObject("fileSystem")
                .getJsonArray("fileStores");

        int workingTasks = j.getJsonObject("tasks")
                .getInt("working");

        int cpu = processor.getInt("logicalProcessorCount");
        // in byte
        // TODO
        int freeMemory = hardware.getJsonObject("memory")
                .getInt("available");
        if(freeMemory < 0 )
            freeMemory = Integer.MAX_VALUE;

        // in byte
        // WARNING: maybe overflow
        int freeDiskSpace = 0;
        for (JsonValue v: fileStores) {
            // WARNING: maybe overflow even use `getInt` here
            freeDiskSpace += ((JsonObject) v).getInt("usableSpace");
        }
        // TODO
        if(freeDiskSpace < 0)
            freeDiskSpace = Integer.MAX_VALUE;

        // TODO: need tune
        boolean running = workingTasks > 0;
        boolean available = workingTasks == 0;

        return new RemoteWorkerInfo(cpu,
                freeDiskSpace,
                freeMemory,
                running,
                available);
    }

    // TODO: consider move to a util function
    public static JsonObject getTasksInfo() {
        // TODO: unfinished
        return Json.createObjectBuilder().add("working", 0).build();
    }

    private static SystemInfo getSystemInfo() {
        if (info == null) {
            info = new SystemInfo();
        }
        return info;
    }

    private static Properties getProperties() {
        if (properties == null) {
            properties = PropertiesUtil.loadProperties(properties_path);
        }
        return properties;
    }

    public static JsonObject getJson() {
        return getSystemInfo().toJSON(getProperties());
    }

    public static String getPrettyJsonString() {
        return getSystemInfo().toPrettyJSON(getProperties());
    }
}
