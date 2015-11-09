package org.ietools.connect.messageProcessor;

import java.net.URI;
import java.net.UnknownHostException;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import test.queue.server.ExtractionTask;
import test.queue.server.ExtractionTaskMonitor;
import test.queue.server.TaskState;

public class RequireTaskMessageHandler extends MessageHandler {

	@Override
	public void handleMessage(Message msg) throws JMSException {
		// TODO Auto-generated method stub
		MapMessage mssge = (MapMessage)msg;
		String taskID = mssge.getString("Task ID");
		String ip = mssge.getString("Receiver");
		System.out.println(taskID+":"+ip);
	}

	@Override
	public Message generateMessage(Session session, Message msg)
			throws JMSException, UnknownHostException {
		// TODO Auto-generated method stub
		ExtractionTaskMonitor monitor = ExtractionTaskMonitor.getInstance();
		MapMessage mssge = (MapMessage)msg;
		String taskID = mssge.getString("Task ID");
		String ip = mssge.getString("Receiver");
		
		
		ObjectMessage message = session.createObjectMessage();

		message.setStringProperty("Task ID", taskID);
		message.setStringProperty("Receiver", ip);
		
		if(monitor.contains(taskID)){
			ExtractionTask task = monitor.getTask(taskID);
			if(task.getState()==TaskState.Task_NEED_TO_DISPATCH){
				URI fileURI = task.getFileURI();
				FileToParse file = new FileToParse(taskID, fileURI);
				file.setConfigURIs(task.getConfigURIs());
			
				message.setObject(file);
			
				message.setJMSType("FileToParse");
			
				task.setState(TaskState.TASK_DISPATCHED);
				return message;
			}
		}
		
		message.setJMSType("ParseRequestDenied");
		return message;
	}

}
