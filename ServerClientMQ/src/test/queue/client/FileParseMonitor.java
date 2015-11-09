package test.queue.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.ietools.pdftable.PdfTableFinder.PDFTableFinder;
import org.ietools.pdftable.PdfTableFinder.TableConfig;

import test.DocumentTest.DummyTableConfig;

public class FileParseMonitor {
	private static FileParseMonitor instance;
	private Map<String, FileParseTask> monitor;
	
	private FileParseMonitor(){
		monitor = new HashMap<String, FileParseTask>();
	}
	
	public static FileParseMonitor getInstance(){
		if(instance == null)
			instance = new FileParseMonitor();
		return instance;
	}
	
	public boolean contains(String id){
		return monitor.containsKey(id);
	}
	
	public void addNewFileTask(String id, String path){
		if(contains(id))
			return;
		
		FileParseTask task = new FileParseTask(id, path);
		monitor.put(id, task);
		test();
	}
	
	public void updateFileState(String id, FileParseState state){
		if(!contains(id))
			return;
		
		FileParseTask task = monitor.get(id);
		/*StringBuilder sb = new StringBuilder();
		sb.append("ID:\t"+id+"\n");
		sb.append("Path:\t"+task.getFilePath()+"\n");
		sb.append("Before update:\t"+task.getState()+"\n");*/
		task.setState(state);
		/*sb.append("After update:\t"+task.getState()+"\n");
		sb.append("Time:\t"+task.getLastUpdate()+"\n");
		System.out.println(sb.toString());*/
	}
	
	public FileParseTask findTask(String id){
		if(!contains(id))
			return null;
		return monitor.get(id);
	}
	
	public List<FileParseTask> getFileOfState(FileParseState state){
		List<FileParseTask> tasks = new ArrayList<FileParseTask>();
		
		Iterator<String> it = monitor.keySet().iterator();
		while(it.hasNext()){
			String id = it.next();
			FileParseTask task = monitor.get(id);
			if(task.getState() == state)
				tasks.add(task);
		}
		
		return tasks;
	}
	
	public FileParseTask removeTask(String id){
		if(!contains(id))
			return null;
		return monitor.remove(id);
	}
	
	public void test(){
		List<FileParseTask> tasks = getFileOfState(FileParseState.WAIT_FILE);
		if(tasks.size()>0){
			for(FileParseTask task : tasks)
				System.out.println(task.toString());
		}
	}
	
	public void process(){
		List<FileParseTask> tasks = getFileOfState(FileParseState.WAIT_FILE);
		while(tasks.size()>0){
			FileParseTask task = tasks.get(0);
			File file = new File(task.getFilePath());
			updateFileState(task.getID(),FileParseState.WAIT_PARSE);
			
			PDDocument pdd = null;
			try {
				pdd = PDDocument.load(file);
				if(pdd.isEncrypted()){
					updateFileState(task.getID(),FileParseState.PARSE_ERROR);
					task.setString("Document Encrypted");
				}else{
					updateFileState(task.getID(),FileParseState.PARSING);
					@SuppressWarnings("unchecked")
					List<PDPage> pages = pdd.getDocumentCatalog().getAllPages();
		    		List<TableConfig> configs = new ArrayList<TableConfig>();
		    		configs.add(new DummyTableConfig());
		    		//configs.add(new AnotherDummyTableConfig());
		    		
		    		PDFTableFinder finder = new PDFTableFinder(pages, configs);
		    		String prefix = file.getAbsolutePath().substring(0, file.getAbsolutePath().length()-4);
		    		finder.process();
		    		
		    		finder.writeToXML(prefix);
		    		updateFileState(task.getID(),FileParseState.PARSED);
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				monitor.remove(task.getID());
				monitor.put(task.getID(), task);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				updateFileState(task.getID(),FileParseState.PARSE_ERROR);
				task.setString("Unable to write to xml");
			} finally{
				try {
					pdd.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			tasks = getFileOfState(FileParseState.WAIT_FILE);
		}
	}

}
