package org.iceslab.frobot.commons.utils.db;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.commons.utils.general.DateUtils;
import org.iceslab.frobot.commons.utils.general.StringUtils;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by Neuclil on 17-7-23.
 */
public class SQLiteOperation {
    private static final Logger LOGGER = Logger.getLogger(SQLiteOperation.class);

    private static final String JDBC = "org.sqlite.JDBC";
    public static final String DATABASE_NAME = "frobot.db";
    private static final Semaphore SIGNAL = new Semaphore(1);

    public static final String CREATE_MASTER_PROJECT_TABLE_SQL = "CREATE TABLE IF NOT EXISTS t_master_project_info(\n" +
            "project_ID VARCHAR(128) PRIMARY KEY NOT NULL,\n" +
            "project_name VARCHAR(32) NOT NULL,\n" +
            "project_username VARCHAR(32) NOT NULL,\n" +
            "project_simple_name VARCHAR(32) NOT NULL,\n" +
            "project_jar_name VARCHAR(32) NOT NULL,\n" +
            "project_engine_jar_path VARCHAR(128),\n" +
            "project_engine_class_name_with_package_name VARCHAR(128),\n" +
            "project_config_file_path VARCHAR(128) NOT NULL,\n" +
            "start_type INTEGER NOT NULL,\n" +
            "start_delay_time INTEGER NOT NULL,\n" +
            "priority INTEGER NOT NULL,\n" +
            "project_status INTEGER NOT NULL DEFAULT 1,\n" +
            "need_extra_engine INTEGER NOT NULL,\n" +
            "task_num INTEGER DEFAULT 0,\n" +
            "task_success_num INTEGER DEFAULT 0,\n" +
            "task_failure_num INTEGER DEFAULT 0,\n" +
            "project_start_time VARCHAR(30) DEFAULT NULL,\n" +
            "project_end_time VARCHAR(30) DEFAULT NULL,\n" +
            "project_delete_time VARCHAR(30) DEFAULT NULL,\n" +
            "project_running_time INTEGER NOT NULL DEFAULT 0,\n" +
            "project_deploy_time VARCHAR(30) DEFAULT NULL,\n" +
            "project_update_time VARCHAR(30) DEFAULT NULL,\n" +
            "project_restart_time VARCHAR(30) DEFAULT NULL,\n" +
            "project_stop_time VARCHAR(30) DEFAULT NULL,\n" +
            "project_invalid_exit INTEGER NOT NULL DEFAULT -1,\n" +
            "project_finish_percentage INTEGER DEFAULT 0,\n" +
            "project_remain_time INTEGER DEFAULT 0)";

    public static final String CREATE_MASTER_TASK_TABLE_SQL = "CREATE TABLE IF NOT EXISTS t_master_task_info(\n" +
            "task_ID VARCHAR(128) NOT NULL,\n" +
            "task_name VARCHAR(128) NOT NULL,\n" +
            "project_ID VARCHAR(128) NOT NULL,\n" +
            "worker_num INTEGER NOT NULL,\n" +
            "task_simple_name VARCHAR(128) NOT NULL,task_jar_name VARCHAR(128) NOT NULL,\n" +
            "task_engine_jar_path VARCHAR(128),\n" +
            "task_engine_class_name_with_package_name VARCHAR(128),\n" +
            "task_config_file_path VARCHAR(128) NOT NULL,\n" +
            "task_config_name VARCHAR(128) NOT NULL,\n" +
            "task_data_name VARCHAR(128) DEFAULT NULL,sub_task_sequence INTEGER DEFAULT 0,\n" +
            "depend_on VARCHAR(128) DEFAULT NULL,\n" +
            "need_extra_engine INTEGER NOT NULL,\n" +
            "task_start_time VARCHAR(32) DEFAULT NULL,\n" +
            "task_end_time VARCHAR(32) DEFAULT NULL,\n" +
            "task_delete_time VARCHAR(32) DEFAULT NULL,\n" +
            "task_running_time INTEGER NOT NULL DEFAULT 0,\n" +
            "task_deploy_time VARCHAR(32) DEFAULT NULL,\n" +
            "task_update_time VARCHAR(32) DEFAULT NULL,\n" +
            "task_restart_time VARCHAR(32) DEFAULT NULL,\n" +
            "task_stop_time VARCHAR(32) DEFAULT NULL,\n" +
            "task_remain_time INTEGER DEFAULT NULL,\n" +
            "task_invalid_exit INTEGER NOT NULL DEFAULT -1,\n" +
            "task_status INTEGER NOT NULL DEFAULT 1,\n" +
            "PRIMARY KEY (task_ID,sub_task_sequence))";

    public static final String CREATE_WORKER_TASK_TABLE_SQL = "CREATE TABLE IF NOT EXISTS t_worker_task_info(\n" +
            "task_ID VARCHAR(128) NOT NULL,\n" +
            "sub_task_sequence INTEGER DEFAULT 0,\n" +
            "user_name VARCHAR(32) NOT NULL,\n" +
            "project_name VARCHAR(32) NOT NULL,\n" +
            "task_simple_name VARCHAR(32) NOT NULL,\n" +
            "task_jar_name VARCHAR(32) NOT NULL,\n" +
            "task_engine_jar_path VARCHAR(128),\n" +
            "task_engine_class_name_with_package_name VARCHAR(128),\n" +
            "task_config_file_path VARCHAR(128) NOT NULL,\n" +
            "depend_on VARCHAR(32) DEFAULT NULL,\n" +
            "input_data_file_path VARCHAR(128) NOT NULL,\n" +
            "output_result_file_path VARCHAR(128) NOT NULL,\n" +
            "task_start_time VARCHAR(30) DEFAULT NULL,\n" +
            "task_end_time VARCHAR(30) DEFAULT NULL,\n" +
            "task_delete_time VARCHAR(30) DEFAULT NULL,\n" +
            "task_running_time INTEGER NOT NULL DEFAULT 0,\n" +
            "task_deploy_time VARCHAR(30) DEFAULT NULL,\n" +
            "task_update_time VARCHAR(30) DEFAULT NULL,\n" +
            "task_restart_time VARCHAR(30) DEFAULT NULL,\n" +
            "task_stop_time VARCHAR(30) DEFAULT NULL,\n" +
            "task_remain_time INTEGER DEFAULT NULL,\n" +
            "task_invalid_exit INTEGER NOT NULL DEFAULT -1,\n" +
            "task_status INTEGER NOT NULL DEFAULT 1,\n" +
            "PRIMARY KEY (task_ID,sub_task_sequence))\n";

    private static final String INSERT_PROJECT_SQL = "INSERT INTO t_master_project_info"
            + "(project_ID,"
            + "project_name,"
            + "project_username,"
            + "project_config_file_path,"
            + "project_simple_name,"
            + "project_jar_name,"
            + "project_engine_jar_path,"
            + "project_engine_class_name_with_package_name,"
            + "start_type,"
            + "start_delay_time,"
            + "priority,"
            + "project_status,"
            + "project_deploy_time,"
            + "need_extra_engine)"
            + "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SELECT_PROJECT_SQL =
            "SELECT * FROM t_master_project_info WHERE project_ID = ?";

    private static final String SELECT_TASKIDS_SQL = "SELECT task_ID FROM t_master_task_info WHERE project_ID = ? AND sub_task_sequence = '" + 0 + "'";

    private static final String SELECT_WAITING_PROJECT_SQL = "SELECT * FROM t_master_project_info WHERE project_status = ? LIMIT 0,1";

    private static final String UPDATE_PROJECT_STATUS_SQL ="UPDATE t_master_project_info SET project_status = ? WHERE project_ID = ?";

    private static final String INSERT_TASK_SQL = "INSERT INTO t_master_task_info"
            + "(task_ID,"
            + "task_name,"
            + "project_ID,"
            + "worker_num,"
            + "task_simple_name,"
            + "task_jar_name,"
            + "task_engine_jar_path,"
            + "task_engine_class_name_with_package_name,"
            + "task_config_file_path,"
            + "task_config_name,"
            + "task_data_name,"
            + "depend_on,"
            + "sub_task_sequence,"
            + "need_extra_engine)"
            + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    private static final String SELECT_TASK_BY_ID_SQL = "SELECT * FROM t_master_task_info WHERE task_ID = ? AND sub_task_sequence = ?";

    private SQLiteOperation() {
    }

    public static boolean addTaskInfo(String path, TaskInfo taskInfo) {
        if (taskInfo.getTaskID() == null ||
                taskInfo.getTaskName() == null ||
                taskInfo.getProjectID() == null ||
                taskInfo.getTaskSimpleName() == null ||
                taskInfo.getTaskJarName() == null ||
                taskInfo.getTaskConfigFilePath() == null ||
                taskInfo.getTaskConfigName() == null) {
            LOGGER.error("the information of taskInfo is not completed. some fields is null");
            return false;
        }
        Connection conn = getConnection(path);
        if (conn == null) {
            LOGGER.error("get connection error!");
            return false;
        }
        PreparedStatement pstat = null;
        try {
            pstat = conn.prepareStatement(INSERT_TASK_SQL);
            pstat.setString(1, taskInfo.getTaskID());
            pstat.setString(2, taskInfo.getTaskName());
            pstat.setString(3, taskInfo.getProjectID());
            pstat.setInt(4, taskInfo.getWorkerNum());
            pstat.setString(5, taskInfo.getTaskSimpleName());
            pstat.setString(6, taskInfo.getTaskJarName());
            pstat.setString(7, taskInfo.getTaskEngineJarPath());
            pstat.setString(8, taskInfo.getTaskEngineClassNameWithPackageName());
            pstat.setString(9, taskInfo.getTaskConfigFilePath());
            pstat.setString(10, taskInfo.getTaskConfigName());
            pstat.setString(11, taskInfo.getTaskDataName());
            pstat.setString(12, taskInfo.getDependOn());
            pstat.setInt(13, taskInfo.getSubTaskSequence());
            pstat.setInt(14, taskInfo.getNeedExtraEngine());
            pstat.execute();
            return true;
        } catch (SQLException e) {
            LOGGER.debug("add task into t_master_task_info occurs exception", e);
            return false;
        } finally {
            free(pstat);
            free(conn);
        }
    }

    public static boolean addTaskInfos(String path, Map<TaskInfo, String> taskInfos) {
        Set<TaskInfo> taskInfoList = taskInfos.keySet();
        Iterator<TaskInfo> iterator = taskInfoList.iterator();// 先迭代出来
        TaskInfo taskInfo;
        while (iterator.hasNext()) {
            taskInfo = iterator.next();
            if (isTaskExisted(path, taskInfo.getTaskID(), taskInfo.getSubTaskSequence())) {
                continue;
            }else {
                boolean success = addTaskInfo(path, taskInfo);
                if(success) {
                    continue;
                }else {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isTaskExisted(String path, String taskID, int subTaskSequence) {
        Connection conn = getConnection(path);
        if (conn == null) {
            LOGGER.error("get connection error!");
            return false;
        }
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            pstat = conn.prepareStatement(SELECT_TASK_BY_ID_SQL);
            pstat.setString(1, taskID);
            pstat.setInt(2, subTaskSequence);
            rs = pstat.executeQuery();
            boolean isExist = rs.next();
            return isExist;
        } catch (Exception e) {
            LOGGER.error("query task info error!", e);
            return false;
        } finally {
            free(rs);
            free(pstat);
            free(conn);
        }
    }

    public static boolean updateProjectStatus(String path, String projectId, int newStatus) {
        if(!isProjectExist(path, projectId)) {
            LOGGER.error("project not exist! update error");
            return false;
        }
        if(newStatus == Project.WAITING ||
                newStatus == Project.RUNNING ||
                newStatus == Project.STOP ||
                newStatus == Project.FINISH ||
                newStatus == Project.PAUSE ||
                newStatus == Project.DELETE ||
                newStatus == Project.DISCONNECTION) {
            Connection conn = getConnection(path);
            if (conn == null) {
                LOGGER.error("get connection error");
                return false;
            }
            PreparedStatement pstat = null;
            try {
                conn.setAutoCommit(false);
                pstat = conn.prepareStatement(UPDATE_PROJECT_STATUS_SQL);
                pstat.setInt(1, newStatus);
                pstat.setString(2, projectId);
                pstat.execute();
                conn.commit();
                return true;
            } catch (SQLException e) {
                LOGGER.error("update project status in db error", e);
                return false;
            } finally {
                free(pstat);
                free(conn);
            }
        }else {
            LOGGER.error("the projectStatus you input is error");
        }
        return false;
    }

    /**
     *
     * @param path
     * @return
     */
    public static Project getFirstWaitingProject(String path) {
        Connection conn = getConnection(path);
        if (conn == null) {
            LOGGER.error("get connection error");
            return null;
        }
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            pstat = conn.prepareStatement(SELECT_WAITING_PROJECT_SQL);
            pstat.setInt(1, Project.WAITING);
            rs = pstat.executeQuery();
            if (rs.next()) {
                int startDelayTime = rs.getInt("start_delay_time");
                String projectDeployTime = rs.getString("project_deploy_time");
                boolean isProjectExecutable = delayTimeOver(projectDeployTime, startDelayTime);
                if (isProjectExecutable) {
                    Project project = new Project()
                            .setProjectId(rs.getString("project_ID"))
                            .setProjectName(rs.getString("project_name"))
                            .setUserName(rs.getString("project_username"))
                            .setProjectSimpleName(rs.getString("project_simple_name"))
                            .setProjectJarName(rs.getString("project_jar_name"))
                            .setPersonalProjectEngine(rs.getInt("need_extra_engine")==1?true:false)
                            .setProjectJarPath(rs.getString("project_engine_jar_path"))
                            .setProjectEngineClassWithPackageName(rs.getString("project_engine_class_name_with_package_name"))
                            .setProjectRootDirectory(rs.getString("project_config_file_path"))
                            .setStartType(rs.getInt("start_type")+"")
                            .setStartDelayTime(rs.getInt("start_delay_time"))
                            .setPriority(rs.getInt("priority"))
                            .setProjectStatus(rs.getInt("project_status"));
                    return project;
                }
            }
            return null;
        } catch (Exception e) {
            LOGGER.warn("find out the first waiting, executable project from db error", e);
        } finally {
            free(rs);
            free(pstat);
            free(conn);
        }
        return null;
    }

    protected static boolean delayTimeOver(String projectDeployTime, int startDelayTime) {
        boolean result;
        String currentTime = DateUtils.getCurrentTime();
        java.util.Date currentDate = DateUtils.toDateTime(currentTime);
        java.util.Date projectDate = DateUtils.toDateTime(projectDeployTime);
        java.util.Date projectDateNew = DateUtils.dateAdd(projectDate, startDelayTime);
        if (DateUtils.before(projectDateNew, currentDate)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public static void createTable(String path, String sql) throws SQLException {
        createTable(path, null, sql);
    }

    public static List<String> getTasksId(String path, String id) {
        Connection conn = getConnection(path);
        if (conn == null) {
            LOGGER.error("get connection error");
            return null;
        }
        PreparedStatement pstat = null;
        ResultSet rs = null;
        List<String> taskIDs = new ArrayList<>();
        try {
            pstat = conn.prepareStatement(SELECT_TASKIDS_SQL);
            pstat.setString(1, id);
            rs = pstat.executeQuery();
            while (rs.next()) {
                taskIDs.add(rs.getString("task_ID"));
            }
            if (taskIDs.size() == 0) {
                LOGGER.warn("data is null, query error");
                return null;
            }
            return taskIDs;
        } catch (SQLException e) {
            LOGGER.error("(master)TaskDB query project's task ids error!", e);
            return null;
        } finally {
            free(rs);
            free(pstat);
            free(conn);
        }
    }

    public static boolean addProject(String path, Project project) {
        if (isProjectExist(path, project.getProjectId())) {
            LOGGER.warn("project exist, add error");
            return false;
        }
        if (project.getProjectId() == null ||
                project.getProjectName() == null ||
                project.getUserName() == null ||
                project.getProjectSimpleName() == null ||
                project.getProjectJarName() == null ||
                project.getProjectRootDirectory() == null) {
            LOGGER.error("the basic information of project is not completed. some field is null");
            return false;
        }
        Connection conn = getConnection(path, null);
        if (conn == null) {
            LOGGER.error("get connection error");
            return false;
        }
        PreparedStatement pstat = null;
        try {
            pstat = conn.prepareStatement(INSERT_PROJECT_SQL);
            pstat.setString(1, project.getProjectId());
            pstat.setString(2, project.getProjectName());
            pstat.setString(3, project.getUserName());
            pstat.setString(4, project.getProjectRootDirectory());
            pstat.setString(5, project.getProjectSimpleName());
            pstat.setString(6, project.getProjectJarName());
            pstat.setString(7, project.getProjectJarPath());
            pstat.setString(8, project.getProjectEngineClassWithPackageName());
            pstat.setInt(9, Integer.valueOf(project.getStartType()));
            pstat.setInt(10, (int) project.getStartDelayTime());
            pstat.setInt(11, project.getPriority());
            pstat.setInt(12, project.getProjectStatus());
            pstat.setString(13, DateUtils.getCurrentTime());
            pstat.setInt(14, project.isPersonalProjectEngine() == true ? 1 : 0);
            pstat.execute();
        } catch (SQLException e) {
            LOGGER.error("add project to t_master_project_info error", e);
            return false;
        } finally {
            free(pstat);
            free(conn);
        }
        return true;
    }

    public static boolean isProjectExist(String path, String id) {
        Connection conn = getConnection(path, null);
        if (conn == null) {
            LOGGER.error("get connection error");
            return false;
        }
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            pstat = conn.prepareStatement(SELECT_PROJECT_SQL);
            pstat.setString(1, id);
            rs = pstat.executeQuery();
            boolean isExist = rs.next();
            return isExist;
        } catch (Exception e) {
            LOGGER.error("judge whether the project exists in the db", e);
            return false;
        } finally {
            free(rs);
            free(pstat);
            free(conn);
        }
    }

    public static void createTable(String path, String name, String sql)
            throws SQLException {
        Connection conn = getConnection(path, name);
        Statement stat = null;
        try {
            stat = conn.createStatement();
            stat.executeUpdate(sql);
        } catch (SQLException e) {
            LOGGER.error("init master database error！", e);
            throw e;
        } finally {
            free(stat);
            free(conn);
        }
    }

    private static Connection getConnection(String path) {
        return getConnection(path, null);
    }

    private static Connection getConnection(String path, String name) {
        Connection conn = null;
        if (StringUtils.isEmpty(name))
            name = DATABASE_NAME;
        if (!path.startsWith("jdbc:sqlite:"))
            path = "jdbc:sqlite:" + path;
        try {
            SIGNAL.acquire();
            Class.forName(JDBC);
            conn = DriverManager.getConnection(path + File.separator + name);
        } catch (ClassNotFoundException e) {
            LOGGER.error("get connection error！", e);
            free(conn);
        } catch (SQLException e) {
            LOGGER.error("get connection error！", e);
            free(conn);
        } catch (InterruptedException e) {
            LOGGER.error("get connection error！", e);
            free(conn);
        }
        return conn;
    }

    private static void free(Connection conn) {
        try {
            if (conn != null)
                conn.close();
            SIGNAL.release();
        } catch (SQLException e) {
            LOGGER.error("close Connection error!", e);
        }
    }

    private static void free(PreparedStatement stat) {
        try {
            if (stat != null)
                stat.close();
        } catch (SQLException e) {
            LOGGER.error("close PreparedStatement error!", e);
        }
    }

    private static void free(Statement stat) {
        try {
            if (stat != null)
                stat.close();
        } catch (SQLException e) {
            LOGGER.error("close Statement error!", e);
        }
    }

    private static void free(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            LOGGER.error("close ResultSet error!", e);
        }
    }

}
