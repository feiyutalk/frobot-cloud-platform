package org.iceslab.frobot.worker.manager.task;

public enum TaskReturnCode {

    TASKSTATUS_WAIT(601),

    TASKSTATUS_RUN(602),

    TASKSTATUS_STOP(600),

    TASKSTATUS_FINISH(603),

    TASKSTATUS_DELETE(604),

    TASKSTATUS_DISCONNECTION(605),

    TASKSTATUS_PAUSE(606),

    /*=================TASK Deploy Return Code =============*/
    TASKDEPLOY_AREDYEXIST(200),

    TASKDEPLOY_EXITEDTASK(201),

    TASKDEPLOY_FAIL(202),

    TASKDEPLOY_SUCCESS(203),
    /*=================TASK Deploy Return Code =============*/
    
	TASKSAVE_FAILURE(802);

    private final int code;

    private TaskReturnCode(int code) {
        this.code = code;
    }

    public static TaskReturnCode valueOf(int code) {
        for (TaskReturnCode taskReturnCode : TaskReturnCode.values()) {
            if (taskReturnCode.code == code) {
                return taskReturnCode;
            }
        }
        throw new IllegalArgumentException("can't find the task return code!");
    }

    public int code() {
        return this.code;
    }
}
