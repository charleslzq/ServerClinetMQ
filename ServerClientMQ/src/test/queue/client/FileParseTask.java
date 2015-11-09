package test.queue.client;

import java.util.Date;

public class FileParseTask {
	private String ID;
	private String filePath;
	private FileParseState state;
	private Date lastUpdate;
	private long parseTime;
	private String note;
	
	public FileParseTask(String id, String file){
		ID = id;
		filePath = file;
		state = FileParseState.WAIT_FILE;
		lastUpdate = new Date();
		parseTime = -1;
		note = "";
	}
	
	public String getID() {
		return ID;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public FileParseState getState() {
		return state;
	}
	
	public void setState(FileParseState state) {
		this.state = state;
		Date newDate = new Date();
		if(state == FileParseState.PARSED)
			parseTime = newDate.getTime() - lastUpdate.getTime();
		lastUpdate = newDate;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}
	
	public void setString(String s){
		note = s;
	}

	public long getParseTime() {
		return parseTime;
	}

	public String getNote() {
		return note;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("File Parse Task:");
		sb.append("Task ID:\t"+ID+"\n");
		sb.append("File URI:\t"+filePath+"\n");
		sb.append("Parse State:\t"+state+"\n");
		if(state == FileParseState.PARSED)
			sb.append("Parse Time:\t"+parseTime+"\n");
		sb.append("Note:\t"+note+"\n");
		sb.append("Last Update:\t"+lastUpdate+"\n");
		sb.append("\n");
		
		return sb.toString();
	}
	

}
