package org.ietools.connect.messageProcessor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;

public class MessageProcessor {
	private static MessageProcessor instance;
	private Map<String, MessageHandler> processors;
	private Session session;
	private boolean topic;
	private String name;
	
	private MessageProcessor(){
		processors = new HashMap<String, MessageHandler>();
		session = null;
	}
	
	
	
	public boolean isTopic() {
		return topic;
	}



	public void setTopic(boolean topic) {
		this.topic = topic;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public static MessageProcessor getInstance(){
		if(instance == null)
			instance = new MessageProcessor();
		return instance;
	}
	
	public void init(boolean server){
		Properties properties = new Properties();
		FileInputStream fis;
		try {
			fis = new FileInputStream("resources/config.xml");
			properties.loadFromXML(fis);
			String location;
			if(server){
				topic = true;
				name = properties.getProperty("TopicName");
				location = properties.getProperty("ServerMessageProcessor");
			}else{
				topic = false;
				name = properties.getProperty("QueueName");
				location = properties.getProperty("ClientMessageProcessor");
			}
			fis = new FileInputStream(location);
			properties = new Properties();
			properties.loadFromXML(fis);
			Enumeration<?> names = properties.propertyNames();
	        for ( Object name : Collections.list( names ) ){
	        	String type = name.toString();
	            String handlerClassName = properties.getProperty( type );
	            Class<?> klass;
				try {
					klass = Class.forName( handlerClassName );
					MessageHandler handler = (MessageHandler)klass.newInstance();
		            registerMessageHandler(type, handler);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            
	        }
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void process(Message msg) throws JMSException, UnknownHostException{
		String type = msg.getJMSType();
		if(msg instanceof MapMessage)
			type = "Map/"+type;
		else if(msg instanceof ObjectMessage)
			type = "Object/"+type;
		else if(msg instanceof TextMessage)
			type = "Text/"+type;
		else if(msg instanceof StreamMessage)
			type = "Stream/"+type;
		else if(msg instanceof BytesMessage)
			type = "Bytes/"+type;
		if(processors.containsKey(type)){
			processors.get(type).handleMessage(msg);
			if(session != null)
				processors.get(type).respond(session, msg);
		}
		else{
			System.out.println("Undefined message type "+type);
		}
		
	}
	
	public void registerMessageHandler(String messageType, MessageHandler handler){
		if(processors.containsKey(messageType)){
			System.out.println("Handler for this type already exists!");
			return;
		}
		
		handler.setContext(this);
		processors.put(messageType, handler);
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}
	
	
}
