package org.iceslab.frobot.remoting.command.body.request;

import org.iceslab.frobot.cluster.WorkerTaskResult;
import org.iceslab.frobot.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-16.
 */
public class PushTaskResultRequestBody extends AbstractRemotingCommandBody{
    private WorkerTaskResult workerTaskResult;

    public PushTaskResultRequestBody(WorkerTaskResult workerTaskResult) {
        this.workerTaskResult = workerTaskResult;
    }

    @Override
    public void checkFields() throws Exception {

    }

    /************************* 	Getter & Setter	*************************/
    public WorkerTaskResult getWorkerTaskResult() {
        return workerTaskResult;
    }

    public void setWorkerTaskResult(WorkerTaskResult workerTaskResult) {
        this.workerTaskResult = workerTaskResult;
    }
}
