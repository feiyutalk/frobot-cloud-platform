package org.iceslab.frobot.master.processor;

import org.iceslab.frobot.master.MasterApplication;
import org.iceslab.frobot.remoting.Channel;
import org.iceslab.frobot.remoting.command.RemotingCommand;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos;
import org.iceslab.frobot.remoting.command.protocol.RemotingProtos.RequestCode;
import org.iceslab.frobot.remoting.exception.RemotingCommandException;
import org.iceslab.frobot.remoting.processor.RemotingProcessor;

import java.util.HashMap;
import java.util.Map;

public class MasterRemotingDispatcher implements RemotingProcessor {

	private MasterApplication application;
	private final Map<RequestCode, RemotingProcessor> processors = new HashMap<RequestCode, RemotingProcessor>();

	public MasterRemotingDispatcher(MasterApplication application) {
		this.application = application;
		processors.put(RequestCode.CONNECT, new ConnectProcessor(application));
		processors.put(RequestCode.HEART_BEAT, new HeartBeatProcessor(application));
		processors.put(RequestCode.SUBMIT_PROJECT, new ProjectSubmitProcessor(application));
		processors.put(RequestCode.SUBMIT_TASK, new TaskSubmitProcessor(application));
		processors.put(RequestCode.SUBMIT_FINISH, new ProjectSubmitFinishProcessor(application));
		processors.put(RequestCode.PUSH_TASK_RESULT, new PushTaskResultProcessor(application));
	}

	@Override
	public RemotingCommand processRequest(Channel channel, RemotingCommand request) throws RemotingCommandException {
		RequestCode code = RequestCode.valueOf(request.getCode());
		RemotingProcessor processor = processors.get(code);
		if (processor == null) {
			return RemotingCommand.createResponseCommand(RemotingProtos.ResponseCode.REQUEST_CODE_NOT_SUPPORTED.code(),
					"request code not supported!");
		}
		return processor.processRequest(channel, request);
	}
}