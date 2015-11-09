package org.ietools.connect.messageProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import test.queue.client.FileParseMonitor;

public class FileToParseMessageHandler extends MessageHandler {

	@Override
	public void handleMessage(Message msg) throws JMSException {
		// TODO Auto-generated method stub
		ObjectMessage message = (ObjectMessage)msg;
		
		/*System.out.println("File To Parse:");
		for(Object name:Collections.list(message.getMapNames())){
			System.out.println(name.toString()+":\t"+message.getObject(name.toString()).toString());
		}*/
		FileToParse file = (FileToParse)message.getObject();
		System.out.println(file);
		//FileParseMonitor.getInstance().addNewFileTask(id, filePath);
		/*System.out.println("Host Name:\t"+hostName);
		System.out.println("IP:\t"+ip);
		System.out.println("File Path:\t"+filePath);*/
	}
	

	

	@Override
	public Message generateMessage(Session session, Message msg) throws JMSException, UnknownHostException {
		// TODO Auto-generated method stub
		Message respond = session.createMapMessage();
		respond.setStringProperty("ID", msg.getStringProperty("GUID"));
		String IP = InetAddress.getLocalHost().getHostAddress();
		respond.setStringProperty("IP", IP);
		respond.setBooleanProperty("RequireFile", true);
		return respond;
	}

}
