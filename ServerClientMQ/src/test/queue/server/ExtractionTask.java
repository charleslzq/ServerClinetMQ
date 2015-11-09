package test.queue.server;

import java.net.URI;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ExtractionTask {
	private String taskID;
	private URI fileURI;
	private Set<URI> configURIs;
	private TaskState state;
	private Date lastUpdate;
	private String extracterIP;
	private String checkerIP;
	
	public ExtractionTask(URI uri){
		taskID = UUID.randomUUID().toString();
		fileURI = uri;
		configURIs = new HashSet<URI>();
		state = TaskState.Task_NEED_TO_DISPATCH;
		lastUpdate = new Date();
	}
	
	public void addConfig(URI uri){
		configURIs.add(uri);
	}

	public String getTaskID() {
		return taskID;
	}

	public void setTaskID(String taskID) {
		this.taskID = taskID;
	}

	public URI getFileURI() {
		return fileURI;
	}

	public void setFileURI(URI fileURI) {
		this.fileURI = fileURI;
	}

	public Set<URI> getConfigURIs() {
		return configURIs;
	}

	public void setConfigURIs(Set<URI> configURIs) {
		this.configURIs = configURIs;
	}

	public TaskState getState() {
		return state;
	}

	public void setState(TaskState state) {
		this.state = state;
		lastUpdate = new Date();
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public String getExtracterIP() {
		return extracterIP;
	}

	public void setExtracterIP(String extracterIP) {
		this.extracterIP = extracterIP;
	}

	public String getCheckerIP() {
		return checkerIP;
	}

	public void setCheckerIP(String checkerIP) {
		this.checkerIP = checkerIP;
	}
	
	
	
}
