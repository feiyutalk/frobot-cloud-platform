package org.iceslab.frobot.remoting.command.body.response;

import org.iceslab.frobot.remoting.command.body.AbstractRemotingCommandBody;

public class FindAddrResponseBody extends AbstractRemotingCommandBody {
    private String address;

    public FindAddrResponseBody(String address) {
        super();
        this.address = address;
    }

    @Override
    public void checkFields() throws Exception {
        // TODO Auto-generated method stub
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
