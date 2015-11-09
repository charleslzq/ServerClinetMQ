package org.ietools.connect.messageProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

public class AskToParseMessageHandler extends MessageHandler {

	@Override
	public void handleMessage(Message msg) throws JMSException {
		// TODO Auto-generated method stub
		MapMessage message = (MapMessage)msg;
		System.out.println(message.getString("Task ID"));
	}

	@Override
	public Message generateMessage(Session session, Message msg)
			throws JMSException, UnknownHostException {
		// TODO Auto-generated method stub
		MapMessage message = (MapMessage)msg;
		String taskID = message.getString("Task ID");
		String ip = InetAddress.getLocalHost().getHostAddress();
		MapMessage response = session.createMapMessage();
		response.setJMSType("RequireTask");
		response.setString("Task ID", taskID);
		response.setString("Receiver", ip);
		return response;
	}

}
