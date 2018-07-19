package org.iceslab.frobot.commons.utils.db;

import org.iceslab.frobot.commons.utils.general.FileManageUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.sql.SQLException;

import static junit.framework.TestCase.assertTrue;

/**
 * Created by Neuclil on 17-7-23.
 */
public class SQLiteOperationTest {
    private static final String PROJECT_INFO_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS t_master_project_info"
            + " (project_ID VARCHAR(128) PRIMARY KEY NOT NULL,"
            + " project_name VARCHAR(32) NOT NULL,"
            + " project_username VARCHAR(32) NOT NULL,"
            + " project_simple_name VARCHAR(32) NOT NULL,"
            + " project_jar_name VARCHAR(32) NOT NULL,"
            + " project_engine_jar_path VARCHAR(128),"
            + " project_engine_class_name_with_package_name VARCHAR(128),"
            + " project_config_file_path VARCHAR(128) NOT NULL,"
            + " start_type INTEGER NOT NULL,"
            + " start_delay_time INTEGER NOT NULL,"
            + " priority INTEGER NOT NULL,"
            + " project_status INTEGER NOT NULL DEFAULT 1,"
            + " need_extra_engine INTEGER NOT NULL,"
            + " task_num INTEGER DEFAULT 0,"
            + " task_success_num INTEGER DEFAULT 0,"
            + " task_failure_num INTEGER DEFAULT 0,"
            + " project_start_time VARCHAR(30) DEFAULT NULL,"
            + " project_end_time VARCHAR(30) DEFAULT NULL,"
            + " project_delete_time VARCHAR(30) DEFAULT NULL,"
            + " project_running_time INTEGER NOT NULL DEFAULT 0,"
            + " project_deploy_time VARCHAR(30) DEFAULT NULL,"
            + " project_update_time VARCHAR(30) DEFAULT NULL,"
            + " project_restart_time VARCHAR(30) DEFAULT NULL,"
            + " project_stop_time VARCHAR(30) DEFAULT NULL,"
            + " project_invalid_exit INTEGER NOT NULL DEFAULT -1,"
            + " project_finish_percentage INTEGER DEFAULT 0,"
            + " project_remain_time INTEGER DEFAULT 0)";

    private static final String TASK_INFO_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS t_master_task_info"
            + " (task_ID VARCHAR(128) NOT NULL,"
            + " task_name VARCHAR(128) NOT NULL,"
            + " project_ID VARCHAR(128) NOT NULL,"
            + " worker_num INTEGER NOT NULL,"
            + " task_simple_name VARCHAR(128) NOT NULL,"
            + " task_jar_name VARCHAR(128) NOT NULL,"
            + " task_engine_jar_path VARCHAR(128),"
            + " task_engine_class_name_with_package_name VARCHAR(128),"
            + " task_config_file_path VARCHAR(128) NOT NULL,"
            + " task_config_name VARCHAR(128) NOT NULL,"
            + " task_data_name VARCHAR(128) DEFAULT NULL,"
            + " sub_task_sequence INTEGER DEFAULT 0,"
            + " depend_on VARCHAR(128) DEFAULT NULL,"
            + " need_extra_engine INTEGER NOT NULL,"
            + " task_start_time VARCHAR(32) DEFAULT NULL,"
            + " task_end_time VARCHAR(32) DEFAULT NULL,"
            + " task_delete_time VARCHAR(32) DEFAULT NULL,"
            + " task_running_time INTEGER NOT NULL DEFAULT 0,"
            + " task_deploy_time VARCHAR(32) DEFAULT NULL,"
            + " task_update_time VARCHAR(32) DEFAULT NULL,"
            + " task_restart_time VARCHAR(32) DEFAULT NULL,"
            + " task_stop_time VARCHAR(32) DEFAULT NULL,"
            + " task_remain_time INTEGER DEFAULT NULL,"
            + " task_invalid_exit INTEGER NOT NULL DEFAULT -1,"
            + " task_status INTEGER NOT NULL DEFAULT 1,"
            + " PRIMARY KEY (task_ID,sub_task_sequence))";

    private final String DIR_PATH = System.getProperty("user.home") + File.separator
            + "sqlite_test";
    private final String JDBC_PATH = "jdbc:sqlite:" + System.getProperty("user.home") + File.separator
            + "sqlite_test";
    private final String NAME = "sqlite.db";

    @Before
    public void setUp(){
        File file = new File(DIR_PATH);
        if(!file.exists())
            file.mkdirs();
    }

    @After
    public void tearDown(){
        FileManageUtil.deleteAll(DIR_PATH);
    }

    @Test
    public void testCreateTable() throws SQLException {
        SQLiteOperation.createTable(JDBC_PATH, NAME, PROJECT_INFO_TABLE_SQL);
        SQLiteOperation.createTable(JDBC_PATH, NAME, TASK_INFO_TABLE_SQL);
        File db = new File(DIR_PATH + File.separator + NAME);
        assertTrue(db.exists());
        assertTrue(db.isFile());
    }
}
