package org.iceslab.frobot.master.manager.project;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.commons.utils.db.DBOperation;

/**
 * master db operation
 * 
 * @author wanghao
 *
 */
public class TaskDBOperation {

	public static final Logger LOGGER = Logger.getLogger(TaskDBOperation.class);
	private static final String insertTask = "INSERT INTO t_master_task_info"
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

	private static final String selectProjectIDbyTaskID = "SELECT project_ID FROM t_master_task_info WHERE task_ID = ?";

	private static final String selectTaskInfoByTaskIDAndSequence = "SELECT * FROM t_master_task_info WHERE task_ID =? AND sub_task_sequence = ?";

	private static final String selectTaskStatusByTaskIDAndSequence = "SELECT task_status FROM t_master_task_info WHERE task_ID = ? AND sub_task_sequence = ?";

	private static final String selectTaskIDsByProjectID = "SELECT task_ID FROM t_master_task_info WHERE project_ID = ? AND sub_task_sequence = '" + 0 + "'";

	private static final String updateTaskUpdate = "update t_master_task_info SET task_status = ? WHERE task_ID = ?";

	public static boolean addNewTaskInfo(TaskInfo taskInfo) {
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
		DBOperation dbOperation = DBOperation.getDBOperation(DBOperation.MASTER);
		Connection conn = dbOperation.getConnection();
		if (conn == null) {
			LOGGER.error("get connection error!");
			DBOperation.free(conn);
			return false;
		}
		PreparedStatement pstat = null;
		try {
			pstat = conn.prepareStatement(TaskDBOperation.insertTask);
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
			DBOperation.free(pstat);
			DBOperation.free(conn);
		}
	}


	public static String getProjectIDbyTaskID(String taskID) {
		Connection conn = DBOperation.getDBOperation(DBOperation.MASTER).getConnection();
		if (conn == null) {
			LOGGER.error("获取链接失败");
			DBOperation.free(conn);
			return null;
		}
		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {
			pstat = conn.prepareStatement(TaskDBOperation.selectProjectIDbyTaskID);
			pstat.setString(1, taskID);
			rs = pstat.executeQuery();
			if (rs.next()) {
				return rs.getString("project_ID");
			}
		} catch (SQLException e) {
			LOGGER.debug("查询失败！", e);
			return null;
		} finally {
			DBOperation.free(rs);
			DBOperation.free(pstat);
			DBOperation.free(conn);
		}
		return null;
	}

	public static TaskInfo getTaskInfoByTaskIDAndSequence(String taskID, int subTaskSequence) {
		Connection conn = DBOperation.getDBOperation(DBOperation.MASTER).getConnection();
		if (conn == null) {
			LOGGER.error("获取链接失败");
			DBOperation.free(conn);
			return null;
		}
		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {
			pstat = conn.prepareStatement(TaskDBOperation.selectTaskInfoByTaskIDAndSequence);
			pstat.setString(1, taskID);
			pstat.setInt(2, subTaskSequence);
			rs = pstat.executeQuery();
			if (rs.next()) {
				TaskInfo taskInfo = new TaskInfo();
				taskInfo.setTaskID(rs.getString("task_ID"));
				taskInfo.setTaskName(rs.getString("task_name"));
				taskInfo.setProjectID(rs.getString("project_ID"));
				taskInfo.setWorkerNum(rs.getInt("worker_num"));
				taskInfo.setTaskSimpleName(rs.getString("task_simple_name"));
				taskInfo.setTaskJarName(rs.getString("task_jar_name"));
				taskInfo.setTaskEngineJarPath(rs.getString("task_engine_jar_path"));
				taskInfo.setTaskEngineClassNameWithPackageName(
						rs.getString("task_engine_class_name_with_package_name"));
				taskInfo.setTaskConfigFilePath(rs.getString("task_config_file_path"));
				taskInfo.setTaskConfigName(rs.getString("task_config_name"));
				taskInfo.setTaskDataName(rs.getString("task_data_name"));
				taskInfo.setSubTaskSequence(rs.getInt("sub_task_sequence"));
				taskInfo.setDependOn(rs.getString("depend_on"));
				taskInfo.setNeedExtraEngine(rs.getInt("need_extra_engine"));
				taskInfo.setTaskStatus(rs.getInt("task_status"));
				return taskInfo;
			}
			return null;
		} catch (Exception e) {
			LOGGER.debug("getTaskInfoByTaskID 失败", e);
		} finally {
			DBOperation.free(rs);
			DBOperation.free(pstat);
			DBOperation.free(conn);
		}
		return null;
	}

	public static boolean isTaskFinish(String taskID, int subTaskSequence) {
		Connection conn = DBOperation.getDBOperation(DBOperation.MASTER).getConnection();
		if (conn == null) {
			LOGGER.error("获取链接失败");
			DBOperation.free(conn);
		}
		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {
			pstat = conn.prepareStatement(TaskDBOperation.selectTaskStatusByTaskIDAndSequence);
			pstat.setString(1, taskID);
			pstat.setInt(2, subTaskSequence);
			rs = pstat.executeQuery();
			if (rs.next()) {
				if (rs.getInt(1) == TaskInfo.FINISH) {
					return true;
				}
			}
		} catch (SQLException e) {
			LOGGER.debug("查询数据库异常", e);
		} finally {
			DBOperation.free(rs);
			DBOperation.free(pstat);
			DBOperation.free(conn);
		}
		return false;
	}

	public static boolean updateTaskStatus(String taskID, int newStatus) {
		Connection conn = DBOperation.getDBOperation(DBOperation.MASTER).getConnection();
		if (conn == null) {
			LOGGER.error("获取链接失败");
			DBOperation.free(conn);
			return false;
		}
		PreparedStatement pstat = null;
		try {
			conn.setAutoCommit(false);// 设置自动生效不起作用
			pstat = conn.prepareStatement(TaskDBOperation.updateTaskUpdate);
			pstat.setInt(1, newStatus);
			pstat.setString(2, taskID);
			pstat.execute();
			conn.commit();
			return true;
		} catch (SQLException e) {
			LOGGER.debug("(master)TaskDB 更新task发生异常！！！", e);
			return false;
		} finally {
			DBOperation.free(pstat);
			DBOperation.free(conn);
		}
	}

	public static List<String> getTaskIDs(String projectID) {
		Connection conn = DBOperation.getDBOperation(DBOperation.MASTER).getConnection();
		if (conn == null) {
			LOGGER.error("获取链接失败");
			DBOperation.free(conn);
			return null;
		}
		PreparedStatement pstat = null;
		ResultSet rs = null;
		List<String> taskIDs = new ArrayList<>();
		try {
			pstat = conn.prepareStatement(TaskDBOperation.selectTaskIDsByProjectID);
			pstat.setString(1, projectID);
			rs = pstat.executeQuery();
			while (rs.next()) {
				taskIDs.add(rs.getString("task_ID"));
			}
			if (taskIDs.size() == 0) {
				LOGGER.debug("数据为空，查询失败！");
				return null;
			}
			return taskIDs;
		} catch (SQLException e) {
			LOGGER.debug("(master)TaskDB 查询一个project的所有taskID异常！！！", e);
			return null;
		} finally {
			DBOperation.free(rs);
			DBOperation.free(pstat);
			DBOperation.free(conn);
		}
	}
}
