package org.iceslab.frobot.remoting.command.protocol;

public final class RemotingProtos {
    private RemotingProtos() {
    }

    public enum RequestCode {
        // 连接
        CONNECT(0),

        REQUEST_MASTER_ADDR(1),

        FAIL_MASTER_ADDR(2),
        // 任务
        HEART_BEAT(100),
        
        SUBMIT_TASK(101),

        PUSH_TASK(102),

        PUSH_TASK_RESULT(103),

        REPORT_STATUS(105),

        CHECK_STATUS(106),

        SUBMIT_PROJECT(108),

        SUBMIT_FINISH(110),

        PUSH_TASK_DATA(111),

    	//系统
    	GET_WORKER_INFO(200);
        private int code;

        private RequestCode(int code) {
            this.code = code;
        }

        public static RequestCode valueOf(int code) {
            for (RequestCode requestCode : RequestCode.values()) {
                if (requestCode.code == code) {
                    return requestCode;
                }
            }
            throw new IllegalArgumentException("can't find the response code !");
        }

        public int code() {
            return this.code;
        }
    }

	public enum ResponseCode {
		/************************* 通信相关	*************************/
        // 成功
		CONNECT_SUCCESS(0),
		// 发生了未捕获异常
		COMMAND_PROCESS_ERROR(1),
		// 由于线程池拥堵，系统繁忙
		SYSTEM_BUSY(2),
		// 请求代码不支持
		REQUEST_CODE_NOT_SUPPORTED(3),
		// 请求参数错误
		REQUEST_PARAM_ERROR(4),
		// 请求地址成功
		REQUEST_ADDR_SUCCESS(5),
		
		REQUEST_ADDR_FAILURE(6),

        /************************* 任务相关	*************************/
		PUSH_TASK_SUCCESS(101),

        PUSH_TASK_FAILED(102),

        TASK_RUN_ERROR(104),

        HEART_BEAT_SUCCESS(105),

        TASK_PULL_SUCCESS(106),

        STATUS_RECEIVE_SUCCESS(107),

        STATUS_RECEIVE_FAILED(108),

        CHECK_STATUS_SUCCESS(109),

        CHECK_STATUS_FAILED(110),

        SUBMIT_PROJECT_SUCCESS(111),

        SUBMIT_PROJECT_FAILURE(112),

        SUBMIT_TASK_SUCCESS(113),

        SUBMIT_TASK_FAILURE(114),

        SUBMIT_PROJECT_FINISH_SUCCESS(115),

        SUBMIT_PROJECT_FINISH_FAILURE(116),

        PUSH_TASK_RESULT_SUCCESS(117),

        PUSH_TASK_RESULT_FAILURE(118),

        PUSH_TASK_DATA_SUCCESS(119),

        PUSH_TASK_DATA_FAILURE(120),
        /*******************  系统相关  ***********************/
		GET_WORKER_INFO_SUCCESS(200),
		GET_WORKER_INFO_FAILURE(201);
        private int code;

        ResponseCode(int code) {
            this.code = code;
        }

        public static ResponseCode valueOf(int code) {
            for (ResponseCode responseCode : ResponseCode.values()) {
                if (responseCode.code == code) {
                    return responseCode;
                }
            }
            throw new IllegalArgumentException("can't find the response code !");
        }

        public int code() {
            return this.code;
        }
    }
}