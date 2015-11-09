package test.queue.server;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

public class FileChooser {
	private JFileChooser chooser;
	private FileFilter filter;
	
	public FileChooser(FileFilter fl){
		chooser = new JFileChooser();
		filter = fl;
	}
	
	public List<File> chooseFile(){
		List<File> files = new ArrayList<File>();
		
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setMultiSelectionEnabled(true);
		int open = chooser.showOpenDialog(null);
        if(open == chooser.APPROVE_OPTION){ //打开文件
        	for(File file:chooser.getSelectedFiles())
        		if(filter.accept(file))
        			files.add(file);
        }
        
        return files;
	}
	
	public List<File> chooserFileAndDir(){
		List<File> files = new ArrayList<File>();
		
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		int open = chooser.showOpenDialog(null);
        if(open == chooser.APPROVE_OPTION){ //打开文件
        	for(File file:chooser.getSelectedFiles()){
        		if(file.isFile()){
        			if(filter.accept(file))
        				files.add(file);
        		}else if(file.isDirectory())
        			files.addAll(findFilesInDir(file));
        	}
        }
        
        return files;
	}
	
	public List<File> chooserFileAndDirRecursively(){
		List<File> files = new ArrayList<File>();
		
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setMultiSelectionEnabled(true);
		int open = chooser.showOpenDialog(null);
        if(open == chooser.APPROVE_OPTION){ //打开文件
        	for(File file:chooser.getSelectedFiles()){
        		if(file.isFile()){
        			if(filter.accept(file))
        				files.add(file);
        		}else if(file.isDirectory())
        			files.addAll(findFilesInDirRecursively(file));
        	}
        }
        
        return files;
	}
	
	public List<File> chooseDir(){
		List<File> files = new ArrayList<File>();
		
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int open = chooser.showOpenDialog(null);
        if(open == chooser.APPROVE_OPTION){ //打开文件
        	File dir = chooser.getSelectedFile();
        	files.addAll(findFilesInDir(dir));
        }
        
        return files;
	}
	
	public List<File> chooserDirRecursively(){
		List<File> files = new ArrayList<File>();
		
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int open = chooser.showOpenDialog(null);
        if(open == chooser.APPROVE_OPTION){ //打开文件
        	File dir = chooser.getSelectedFile();
        	files.addAll(findFilesInDirRecursively(dir));
        }
        
        return files;
	}

	private List<File> findFilesInDir(File dir) {
		// TODO Auto-generated method stub
		List<File> files = new ArrayList<File>();
		
		File[] fs = dir.listFiles(filter);
		for(File child:fs){
			if(child.isFile())
        		files.add(child);
		}
		return files;
	}
	
	private List<File> findFilesInDirRecursively(File dir) {
		// TODO Auto-generated method stub
		List<File> files = new ArrayList<File>();
		
		File[] fs = dir.listFiles(filter);
		for(File child:fs){
			if(child.isFile()){
        		files.add(child);
			}else if(child.isDirectory()){
				files.addAll(findFilesInDirRecursively(child));
			}
		}
		return files;
	}
}
