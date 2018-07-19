package org.iceslab.frobot.remoting.command.body.request;

import org.iceslab.frobot.cluster.RemoteTask;
import org.iceslab.frobot.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-12.
 */
public class TaskPushRequestBody extends AbstractRemotingCommandBody{
    private RemoteTask remoteTask;

    public TaskPushRequestBody(){
    }

    public TaskPushRequestBody(RemoteTask remoteTask){
        this.remoteTask = remoteTask;
    }

    @Override
    public void checkFields() throws Exception {

    }

    public RemoteTask getRemoteTask() {
        return remoteTask;
    }

    public void setRemoteTask(RemoteTask remoteTask) {
        this.remoteTask = remoteTask;
    }
}
