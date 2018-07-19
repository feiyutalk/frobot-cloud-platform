package org.iceslab.frobot.remoting.command.body.response;

import org.iceslab.frobot.remoting.command.body.AbstractRemotingCommandBody;

/**
 * Created by Neuclil on 17-4-7.
 */
public class IdResponseBody extends AbstractRemotingCommandBody {
    private String allocateIdentity;

    public IdResponseBody(String id) {
        this.allocateIdentity = id;
    }

    @Override
    public void checkFields() throws Exception {

    }

    public String getAllocateIdentity() {
        return allocateIdentity;
    }

    public void setAllocateIdentity(String allocateIdentity) {
        this.allocateIdentity = allocateIdentity;
    }
}
