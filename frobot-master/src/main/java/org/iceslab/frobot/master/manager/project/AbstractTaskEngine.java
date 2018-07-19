package org.iceslab.frobot.master.manager.project;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.cluster.TaskInfo;
import org.iceslab.frobot.cluster.RemoteTask;
import org.iceslab.frobot.master.manager.channel.WorkerInfo;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.CommandBodyWrapper;
import org.iceslab.frobot.remoting.command.body.request.TaskPushRequestBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos.ResponseCode;

public abstract class AbstractTaskEngine implements TaskEngine {
    private final Logger LOGGER = Logger.getLogger(AbstractTaskEngine.class);

    @Override
    public final List<TaskInfo> init(List<TaskInfo> taskInfos) {
        doInit(taskInfos);
        int index = 1;
        List<TaskInfo> subTasks = new ArrayList<>();
        for (TaskInfo taskInfo : taskInfos) {
            String dependOn = taskInfo.getDependOn();
            if (dependOn.equals("nodepend")) {// 不依赖于其他task
                TaskUtils.divideTaskDataToSubTaskData(taskInfo);
            }
            TaskUtils.ConfigBackupToSubTask(taskInfo);
            for (int i = 1; i <= taskInfo.getWorkerNum(); i++) {
                TaskInfo subTask = TaskDBOperation.getTaskInfoByTaskIDAndSequence(taskInfo.getTaskID(), 0);
                subTask.setSubTaskSequence(i);
                subTask.setTaskConfigFilePath(taskInfo.getTaskConfigFilePath() + File.separator + i);
                subTask.setWorkerNum(1);
                if (index == 1)
                    subTasks.add(subTask);
                TaskDBOperation.addNewTaskInfo(subTask);
            }
            index++;
        }
        return subTasks;
    }

    protected abstract void doInit(List<TaskInfo> taskInfos);

    @Override
    public final boolean start(List<TaskInfo> subTasks, List<WorkerInfo> workers, final MasterApplication application) {
        LOGGER.info("AbstractTaskEngine start.");
        /* 遍历subTask */
        for (TaskInfo subTask : subTasks) {
            RemoteTask remoteTask = TaskUtils.getRemoteTaskByTaskInfo(subTask);
            remoteTask.setIp(application.getMasterNodeConfig().getIp());
            remoteTask.setPort(application.getMasterNodeConfig().getDataPort());
            WorkerInfo worker = workers.get(subTask.getSubTaskSequence() - 1);
            RemotingCommand request = RemotingCommand.createRequestCommand(
                    RemotingProtos.RequestCode.PUSH_TASK.code(),
                    CommandBodyWrapper.wrapper(
                            application.getMasterNodeConfig(),
                            new TaskPushRequestBody(remoteTask)));
            Thread thread = null;

            RemotingCommand requestData = RemotingCommand.createRequestCommand(
                    RemotingProtos.RequestCode.PUSH_TASK_DATA.code(),
                    CommandBodyWrapper.wrapper(
                            application.getMasterNodeConfig(),
                            new TaskPushRequestBody(remoteTask)));

            String datafile = subTask.getTaskConfigFilePath() + File.separator + subTask.getTaskDataName();
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    application.getFileSender().sendFile(datafile);
                }
            });
            thread.start();
            RemotingCommand response2 = application.getRemotingServer().invokeSync(
                    worker.getChannel(), requestData);
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String filePath = application.getMasterNodeConfig().getWorkspacePath() + File.separator
                    + remoteTask.getUserName() + File.separator
                    + remoteTask.getProjectName() + File.separator
                    + "lib" + File.separator
                    + "task_engine" + File.separator
                    + subTask.getTaskJarName();
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    application.getFileSender().sendFile(filePath);
                }
            });
            thread.start();
            RemotingCommand response = application.getRemotingServer().invokeSync(
                    worker.getChannel(), request);
            if (thread != null) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (response.getCode() == ResponseCode.PUSH_TASK_FAILED.code()) {
                return false;
            }
        }
        LOGGER.info("AbstractTaskEngine start successfully!");
        return true;
    }

    public void stop() {
        System.out.println("taskEngine.stop()");
    }

    public void resume() {
        System.out.println("taskEngine.resume()");
    }

    public void pause() {
        System.out.println("taskEngine.pause()");
    }
}
