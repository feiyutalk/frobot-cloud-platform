package org.iceslab.frobot.master.processor;

import org.apache.log4j.Logger;
import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.commons.io.FileReceiver;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.body.request.ProjectSubmitRequestBody;
import org.iceslab.frobot.remoting.command.body.response.NullResponseBody;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.io.File;

/**
 * @auther Neuclil
 */
public class ProjectSubmitProcessor implements RemotingProcessor {
    private static final Logger LOGGER = Logger.getLogger(ProjectSubmitProcessor.class);
    private MasterApplication application;

    public ProjectSubmitProcessor(MasterApplication application) {
        this.application = application;
    }

    @Override
    public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
        ProjectSubmitRequestBody body = request.getBody();
        Project project = body.getProject();
        LOGGER.info("ProjectSubmitProcessor start to receive project: " + project);
        if (project.isPersonalProjectEngine()) {
            String savePath =
                    application.getMasterNodeConfig().getWorkspacePath() + File.separator
                    + project.getUserName() + File.separator
                    + project.getProjectName() + File.separator
                    + "lib" + File.separator
                    + "project_engine";

            File file = new File(savePath);
            if (!file.exists())
                file.mkdirs();
            if (file.isDirectory()) {
                FileReceiver.receiveFile(savePath, project.getIp(),
                        project.getPort());
            } else {
                LOGGER.error("make directory error.");
                RemotingCommand response = RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.SUBMIT_PROJECT_FAILURE.code(),
                        new NullResponseBody());
                return response;
            }
        }
        RemotingCommand response = RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.SUBMIT_PROJECT_SUCCESS.code(),
                new NullResponseBody());
        return response;
    }

}

