package org.iceslab.frobot.remoting.command.body.request;

import org.iceslab.frobot.cluster.Project;
import org.iceslab.frobot.remoting.command.body.AbstractRemotingCommandBody;

public class ProjectSubmitRequestBody extends AbstractRemotingCommandBody {
    private static final long serialVersionUID = 4087700500117659974L;
    private Project project;

    public ProjectSubmitRequestBody() {

    }

    public ProjectSubmitRequestBody(Project project) {
        super();
        this.project = project;
    }

    @Override
    public void checkFields() throws Exception {
        // TODO Auto-generated method stub
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }


}
