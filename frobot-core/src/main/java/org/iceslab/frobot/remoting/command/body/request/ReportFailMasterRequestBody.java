package org.iceslab.frobot.remoting.command.body.request;

import org.iceslab.frobot.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-18.
 */
public class ReportFailMasterRequestBody extends AbstractRemotingCommandBody {
    private String failMasterAddr;

    public ReportFailMasterRequestBody(String failMasterAddr) {
        this.failMasterAddr = failMasterAddr;
    }

    @Override
    public void checkFields() throws Exception {

    }

    /************************* 	Getter & Setter	*************************/
    public String getFailMasterAddr() {
        return failMasterAddr;
    }

    public void setFailMasterAddr(String failMasterAddr) {
        this.failMasterAddr = failMasterAddr;
    }
}
