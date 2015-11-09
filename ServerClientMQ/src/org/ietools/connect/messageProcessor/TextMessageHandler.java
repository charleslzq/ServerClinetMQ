package org.ietools.connect.messageProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

public class TextMessageHandler extends MessageHandler {

	public void handleMessage(Message msg) throws JMSException {
		TextMessage message = (TextMessage)msg;
		String fileName = message.getText();
			
		System.out.println(fileName);
	}

	@Override
	public Message generateMessage(Session session, Message msg) throws JMSException, UnknownHostException {
		// TODO Auto-generated method stub
		TextMessage respond = session.createTextMessage();
		respond.setJMSType("respond");
		respond.setText("Nice to meet you!");
		
		String IP = InetAddress.getLocalHost().getHostAddress();
		respond.setStringProperty("IP", IP);
		return respond;
	}

	

	

}
