package org.iceslab.frobot.worker.processor;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.RemoteTask;
import org.iceslab.frobot.worker.WorkerApplication;
import org.iceslab.frobot.commons.io.FileReceiver;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.CommandBodyWrapper;
import org.iceslab.frobot.remoting.command.body.request.TaskPushRequestBody;
import org.iceslab.frobot.remoting.command.body.response.NullResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.io.File;

/**
 * Created by Neuclil on 17-7-14.
 */
public class TaskDataPushProcessor implements RemotingProcessor {
    private static final Logger LOGGER = Logger.getLogger(TaskPushProcessor.class);
    private WorkerApplication application;

    public TaskDataPushProcessor(WorkerApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
        TaskPushRequestBody body = (TaskPushRequestBody) request.getBody();
        RemoteTask remoteTask = body.getRemoteTask();
        LOGGER.debug("TaskDataPushProcessor : " + remoteTask);
        String savePath = application.getWorkerNodeConfig().getWorkspacePath() + File.separator
                + remoteTask.getUserName() + File.separator
                + remoteTask.getProjectName() + File.separator
                + remoteTask.getTaskID() + File.separator
                + remoteTask.getSubTaskSequence() + File.separator
                + "data";
        File file = new File(savePath);
        if (!file.exists())
            file.mkdirs();
        if (file.isDirectory()) {
            FileReceiver.receiveFile(savePath, remoteTask.getIp(),
                    remoteTask.getPort());
        } else {
            LOGGER.error("目录创建失败！");
            RemotingCommand response = RemotingCommand.createResponseCommand(
                    RemotingProtos.ResponseCode.PUSH_TASK_DATA_FAILURE.code(),
                    new NullResponseBody());
            return response;
        }
        return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.PUSH_TASK_DATA_SUCCESS.code(),
                CommandBodyWrapper.wrapper(application.getWorkerNodeConfig(),new NullResponseBody()));
    }
}
