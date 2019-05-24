package com.uta.classmappinggenerator.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileHelper extends BaseHelper {
	
	private static final Logger log = LoggerFactory.getLogger(FileHelper.class);
	
	public void saveToFile(String content, String baseDir, String fileName, String encoding) {

		verifyFileNameHasValue(fileName, "File name is null");
		
		FileWriter file = makeAbsoluteFileWriter(baseDir, fileName);
		//verifyFileExists(file);
		
		
		try {
			//FileUtils.writeStringToFile(file, content, encoding, true);
			file.append(content); 
			 
            // closing writer connection 
            file.close(); 
		} catch (IOException|UnsupportedCharsetException e) {
			logErrorAndThrowException("Couldn't save input text to file: ", e);
		}
	}	
	
	public void verifyFileExists(File file) {
		
		if (! file.isFile()) {
			logErrorAndThrowException("File does not exist: " + file.getPath()); // + " (Absolutt sti: " + file.getAbsolutePath() + ")");
		}
	}

	public void verifyFileNameHasValue(String fileName, String errorMsg) {
		
		if (StringUtils.isBlank(fileName)) {
			logErrorAndThrowException(errorMsg);
		}
	}
	
	public FileWriter makeAbsoluteFileWriter(String baseDir, String fileName) {
		
		File absBaseDir = makeAbsoluteFile(baseDir);
		File file = new File(absBaseDir+fileName);
		
		/*try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		FileWriter outputfile = null;
		try {
			outputfile = new FileWriter(file, true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		return outputfile;
		
	}
	
	public File makeAbsoluteFile(String baseDir, String fileName) {
		
		File absBaseDir = makeAbsoluteFile(baseDir);
		File file = new File(absBaseDir+fileName);
		
		try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return file;
		
	}

	public File makeAbsoluteFile(String folderName ) {
		
		if (folderName == null) {
			folderName = "";
		}
		
		Path currentRelativePath = Paths.get("");
		String absolutePath = currentRelativePath.toAbsolutePath().toString();
		System.out.println("Current relative path is: " + absolutePath);
		
		final File outputRootDir = new File(absolutePath+"/resources/"+folderName);
		return outputRootDir;
	}

}
