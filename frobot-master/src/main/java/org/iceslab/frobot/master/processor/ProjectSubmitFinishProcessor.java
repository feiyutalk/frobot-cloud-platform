package org.iceslab.frobot.master.processor;

import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.cluster.ProjectReturnCode;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.master.ProjectInfo;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.CommandBodyWrapper;
import org.iceslab.frobot.remoting.command.body.request.ProjectSubmitFinishBody;
import org.iceslab.frobot.remoting.command.body.response.NullResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by Neuclil on 17-4-12.
 */
public class ProjectSubmitFinishProcessor implements RemotingProcessor {
    private MasterApplication application;

    private ThreadPoolExecutor executor;

    public ProjectSubmitFinishProcessor(MasterApplication application) {
        this.application = application;
        executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
        ProjectSubmitFinishBody body = request.getBody();
        Project project = body.getProject();
        ProjectReturnCode projectReturnCode = application.getProjectParser().parseAndSaveProject(project);

        if (projectReturnCode == ProjectReturnCode.PROJECTDEPLOY_SUCCESS &&
                Integer.valueOf(project.getStartType()) == ProjectInfo.START_ATONCE) {//立即启动
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    application.getProjectManager().processProject(project);
                }
            });
        }

        RemotingCommand response = RemotingCommand.createResponseCommand(
                RemotingProtos.ResponseCode.SUBMIT_PROJECT_FINISH_SUCCESS.code(),
                CommandBodyWrapper.wrapper(
                        application.getMasterNodeConfig(),
                        new NullResponseBody()));
        return response;
    }
}
