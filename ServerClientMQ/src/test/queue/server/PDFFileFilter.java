package test.queue.server;

import java.io.File;
import java.io.FileFilter;

public class PDFFileFilter implements FileFilter{

	public boolean accept(File file) {
		// TODO Auto-generated method stub
		if(file.isDirectory())
			return true;
		if(file.getName().toLowerCase().endsWith(".pdf"))
			return true;
		
		return false;
	}

}
