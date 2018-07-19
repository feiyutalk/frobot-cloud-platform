package org.iceslab.frobot.commons.utils.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import org.iceslab.frobot.commons.constants.Constants;

public class DBOperation {
	private static final Logger LOGGER = Logger.getLogger(DBOperation.class);

	public static final int WORKER = 0;
	public static final String MASTER = "master";
	
	private static final String MJDBC = "org.sqlite.JDBC";
	private static final String MDATABASE_NAME = "frobot.db";
	private static final String MJDBC_PATH_WORKER = "jdbc:sqlite:" + System.getProperty("user.home") + File.separator
												+ "frobot_worker" + File.separator;
	private static final String MJDBC_PATH_MASTER = "jdbc:sqlite:" + System.getProperty("user.home") + File.separator
												+ "frobot_master" + File.separator;
	private static final Semaphore M_SIGNAL = new Semaphore(1);

	private String jdbc;
	private String path;
	private String name;
	
	private static DBOperation workerOperation;
	private static DBOperation masterOperation;
	public static DBOperation getDBOperation(int type) {
		if(type != DBOperation.WORKER) 
			return null;
		if (workerOperation == null) {
			synchronized (DBOperation.class) {
				if (workerOperation == null) {
					workerOperation = new DBOperation(MJDBC, MJDBC_PATH_WORKER, MDATABASE_NAME);
				}
			}
		}
		return workerOperation;
	}

	public static DBOperation getDBOperation(String type) {
		if (!type.equals(DBOperation.MASTER))
			return null;
		if (masterOperation == null) {
			synchronized (DBOperation.class) {
				if (masterOperation == null) {
					masterOperation = new DBOperation(MJDBC, MJDBC_PATH_MASTER, MDATABASE_NAME);
				}
			}
		}
		return masterOperation;
	}
	
	public DBOperation(String jdbc, String path, String name) {
		this.jdbc = jdbc;
		this.path = path;
		this.name = name;
	}

	public Connection getConnection() {
		Connection conn = null;
		try {
			M_SIGNAL.acquire();
			Class.forName(jdbc);
			conn = DriverManager.getConnection(path + name);
		} catch (ClassNotFoundException e) {
			LOGGER.error("get connection error！", e);
		} catch (SQLException e) {
			LOGGER.error("get connection error！", e);
		} catch (InterruptedException e) {
			LOGGER.error("get connection error！", e);
		}
		return conn;
	}

	public static void free(Connection conn) {
		try {
			if (conn != null)
				conn.close();
			M_SIGNAL.release();
		} catch (SQLException e) {
			LOGGER.error("close Connection error!", e);
		}
	}

	public static void free(PreparedStatement stat) {
		try {
			if (stat != null)
				stat.close();
		} catch (SQLException e) {
			LOGGER.error("close PreparedStatement error!", e);
		}
	}
	
	public static void free(Statement stat) {
		try {
			if (stat != null)
				stat.close();
		} catch (SQLException e) {
			LOGGER.error("close statement error!", e);
		}
	}

	public static void free(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (SQLException e) {
			LOGGER.error("close ResultSet error!", e);
		}
	}
}
