package org.ietools.connect.messageProcessor;

import java.net.UnknownHostException;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;

public abstract class MessageHandler {
	protected MessageProcessor context = null;
	
	public abstract void handleMessage(Message msg) throws JMSException;
	public abstract Message generateMessage(Session session, Message msg) throws JMSException, UnknownHostException;

	public void respond(Session session, Message msg) throws JMSException, UnknownHostException {
		// TODO Auto-generated method stub
		boolean topic = context.isTopic();
		String name = context.getName();
		if(topic){
			Destination des = session.createTopic(name);
			MessageProducer producer = session.createProducer(des);
			Message response = generateMessage(session, msg);
			if(response != null)
				producer.send(response);		
		}else{
			Destination des = session.createQueue(name);
			MessageProducer producer = session.createProducer(des);
			Message response = generateMessage(session, msg);
			if(response != null)
				producer.send(response);
		}
	}
	
	protected void setContext(MessageProcessor ctx){
		context = ctx;
	}
	
	protected MessageProcessor getContext(){
		return context;
	}
	
	
	
}	
