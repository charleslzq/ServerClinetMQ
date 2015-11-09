package test.queue.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;

public class ExtractionTaskMonitor {
	private static ExtractionTaskMonitor instance;
	private Map<String, ExtractionTask> tasks;
	
	private ExtractionTaskMonitor(){
		tasks = new HashMap<String,  ExtractionTask>();
	}
	
	public static ExtractionTaskMonitor getInstance(){
		if(instance == null)
			instance = new ExtractionTaskMonitor();
		return instance;
	}
	
	public void addNewFile(URI fileURI, Set<URI> configURIs){
		ExtractionTask task = new ExtractionTask(fileURI);
		task.setConfigURIs(configURIs);
		tasks.put(task.getTaskID(), task);
	}
	
	public void process(){
		for(ExtractionTask task:tasks.values()){
			if(task.getState() == TaskState.Task_NEED_TO_DISPATCH){
				BrokerMonitor monitor = BrokerMonitor.getInstance();
				try {
					monitor.askForParse(task);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public ExtractionTask getTask(String id){
		return tasks.get(id);
	}
	
	public boolean contains(String id){
		return tasks.containsKey(id);
	}
	
	public TaskState checkState(String id){
		return tasks.get(id).getState();
	}
}
