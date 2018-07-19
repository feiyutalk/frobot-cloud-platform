package org.iceslab.frobot.remoting.command.body.request;

import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-12.
 */
public class ProjectSubmitFinishBody extends AbstractRemotingCommandBody{
    private Project project;

    public ProjectSubmitFinishBody(){

    }

    public ProjectSubmitFinishBody(Project project){
        this.project = project;
    }

    @Override
    public void checkFields() throws Exception {

    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
