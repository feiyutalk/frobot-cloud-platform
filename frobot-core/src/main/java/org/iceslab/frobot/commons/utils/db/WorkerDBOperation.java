package org.iceslab.frobot.commons.utils.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.WorkerTaskInfo;

public class WorkerDBOperation {
	public static final Logger LOGGER = Logger.getLogger(WorkerDBOperation.class);

	private static final String insertTaskWorker = "INSERT INTO t_worker_task_info"
													+ "(task_ID,"
													+ "sub_task_sequence,"
													+ "project_name,"
													+ "user_name,"
													+ "task_simple_name,"
													+ "task_jar_name,"
													+ "task_engine_jar_path,"
													+ "task_engine_class_name_with_package_name,"
													+ "task_config_file_path,"
													+ "depend_on,"
													+ "input_data_file_path,"
													+ "output_result_file_path) "
													+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String selectTaskInfoByTaskIDandSequenceWorker ="SELECT * FROM t_worker_task_info WHERE task_ID = ? AND sub_task_sequence = ?";

	public static boolean addSubTask(WorkerTaskInfo workerTaskInfo) {
		if(workerTaskInfo.getTaskID() == null ||
				workerTaskInfo.getUserName() == null ||
				workerTaskInfo.getProjectName() == null ||
				workerTaskInfo.getTaskSimpleName() == null ||
				workerTaskInfo.getTaskJarName() == null ||
				workerTaskInfo.getTaskConfigFilePath() == null ||
				workerTaskInfo.getInputDateFilePath() == null ||
				workerTaskInfo.getOutputResultFilePath() == null) {
			LOGGER.error("the information of workerTaskInfor is not completed. some fields is null");
			return false;
		}
		Connection conn = DBOperation.getDBOperation(DBOperation.WORKER).getConnection();
		if (conn == null) {
			LOGGER.error("get connection error");
			return false;
		}
		PreparedStatement pstat = null;
		try {
			pstat = conn.prepareStatement(WorkerDBOperation.insertTaskWorker);
			pstat.setString(1,workerTaskInfo.getTaskID());
			pstat.setInt(2,workerTaskInfo.getSubTaskSequence());
			pstat.setString(3,workerTaskInfo.getProjectName());
			pstat.setString(4,workerTaskInfo.getUserName());
			pstat.setString(5,workerTaskInfo.getTaskSimpleName());
			pstat.setString(6,workerTaskInfo.getTaskJarName());
			pstat.setString(7,workerTaskInfo.getTaskEngineJarPath());
			pstat.setString(8,workerTaskInfo.getTaskEngineClassNameWithPackageName());
			pstat.setString(9,workerTaskInfo.getTaskConfigFilePath());
			pstat.setString(10,workerTaskInfo.getDependOn());
			pstat.setString(11,workerTaskInfo.getInputDateFilePath());
			pstat.setString(12,workerTaskInfo.getOutputResultFilePath());
			pstat.execute();
		} catch (SQLException e) {
			LOGGER.debug("add task to t_worker_task_info table error!", e);
			return false;
		} finally {
			DBOperation.free(pstat);
			DBOperation.free(conn);
		}
		return true;
	}

	public static boolean hasTaskExisted(String taskID,int subTaskSequence) {
		Connection conn = DBOperation.getDBOperation(DBOperation.WORKER).getConnection();
		if (conn == null) {
			LOGGER.error("get connection error");
			DBOperation.free(conn);
			return false;
		}
		PreparedStatement pstat = null;
		ResultSet rs = null;
		try {
			pstat = conn.prepareStatement(WorkerDBOperation.selectTaskInfoByTaskIDandSequenceWorker);
			pstat.setString(1, taskID);
			pstat.setInt(2, subTaskSequence);
			rs = pstat.executeQuery();
			boolean isExist = rs.next();
			return isExist;
		} catch (Exception e) {
			LOGGER.error("query worker db error", e);
			return false;
		} finally {
			DBOperation.free(rs);
			DBOperation.free(pstat);
			DBOperation.free(conn);
		}
	}
}
