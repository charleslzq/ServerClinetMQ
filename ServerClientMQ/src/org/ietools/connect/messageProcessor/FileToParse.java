package org.ietools.connect.messageProcessor;

import java.io.Serializable;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class FileToParse implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7880014040762510519L;
	
	private String taskID;
	private URI fileURI;
	private Set<URI> configURIs;
	
	public FileToParse(String id, URI file){
		taskID = id;
		fileURI = file;
		configURIs = new HashSet<URI>();
	}
	
	
	public void addConfigURI(URI config){
		configURIs.add(config);
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
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("FileToParse\n");
		sb.append("Task ID:\t"+taskID+"\n");
		sb.append("File URI:\t"+fileURI+"\n");
		if(configURIs != null && configURIs.size()>0){
			for(URI uri:configURIs)
				sb.append("Config URI:\t"+fileURI+"\n");
		}
		sb.append("\n");
		
		return sb.toString();
	}
	

}
