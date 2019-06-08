package com.uta.classmappinggenerator.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;
import java.util.StringTokenizer;

import com.uta.classmappinggenerator.util.FileHelper;

public class AutoClassNameMappingGenerator  {
	
	ClassLoader classLoader = getClass().getClassLoader();	
	File folder = new File(classLoader.getResource("test").getFile());
	
	static String fileName = null;
	protected static Stack<String> stack=new Stack<String>();
	
	static FileHelper fileHelper = new FileHelper();
	
	String line = "";
	StringTokenizer stringTokens;
	StringTokenizer stringTokensTemp;
	BufferedReader br = null;
	boolean startProcessingStackElements= false;
	
	/** 
	 * This method is used to do class name mapping for 
	 * identifying which class should be invoked to process
	 * the tag.
	 */
	public void classnameMapping() {
		
		String nodeChildrenString = "";
		
		try{
			String token = "";
			String tempToken = "";
			String prevToken = "(ROOT";
			br = new BufferedReader(new FileReader(new File(fileName)));
			while ((line=br.readLine())!=null){
				
		        stringTokens=new StringTokenizer(line);
		        stringTokensTemp=new StringTokenizer(line);
		        while (stringTokens.hasMoreTokens()){
		            token=stringTokens.nextToken();
		            tempToken = token;
		            String top="";
		            if (token.indexOf(")")<0) {
		            	if(!token.trim().equals(""))
		            		stack.push(token);
		                prevToken = tempToken;
		            	continue; // with next input token
		            } else if(prevToken.indexOf("(")>=0 && token.indexOf(")")>=0) {
		            	
		            	int countClosedBrace = getClosedBraceCount(token);
		            	if(countClosedBrace>1) { //end of children
		            		nodeChildrenString = "";
		            		
		            		top=stack.pop();
			                if (top.indexOf("(")>=0) {
			                	top = top.substring(1); // remove ( from top and push it for children
			                	if(!token.trim().equals(""))
			                		stack.push(top);
			                	token = token.substring(token.indexOf(")") + 1);
			                } 			                
		            		while (!stack.isEmpty()){ // loop though to get a new class name
				                top=stack.pop();
				                if (top.indexOf("(")>=0) { // In this case new class name will be generated
				                	nodeChildrenString = " " + top + nodeChildrenString;
				                	top = top.substring(1); 
				                	if(!top.trim().equals(""))
				                		stack.push(top);
				                	if(token.contains("))"))
				                		if(!token.substring(token.indexOf(")") + 1).trim().equals(""))	
				                			stack.push(token.substring(token.indexOf(")") + 1));
				                    break;
				                }
				                
				                nodeChildrenString = " (" + top + nodeChildrenString;
				            }

	            			System.out.println("nodeChildrenString is "+nodeChildrenString);
	            			writeToFile(nodeChildrenString);
	            			String lastElementOfStack = stack.pop();
	            			while(lastElementOfStack.indexOf(")")>=0) {
	            				stack.push(lastElementOfStack);
	            				resolveAllPossibleParentChildrenNodes(stack); //resolve parents to the possible level
	            				lastElementOfStack = stack.pop();
	            			} 
	            			if(lastElementOfStack.indexOf(")")<0){
	            				stack.push(lastElementOfStack);
	            			}
		            	} else if(countClosedBrace==1) { //not end of children
		            		top=stack.pop();
			                if (top.indexOf("(")>=0) {
			                	top = top.substring(1); // remove ( from top and push it for children
			                	if(!top.trim().equals(""))
			                		stack.push(top);
			                }
		            		prevToken = tempToken;
			                continue;
		            	}
		            }		            
		            prevToken = tempToken;
		       	}
		      }
		} catch (Exception e) {
			  e.printStackTrace();
		  } finally {
			  if(br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		  }
		
	}
	
	private void resolveAllPossibleParentChildrenNodes(Stack<String> stackToProcess) {
		//System.out.println("stack is "+stack);
		String top="";
		String tempTop="";
		String nodeChildrenString="";
		int countClosedBrace = 0;
		boolean parentFound = false;
		boolean startAgain = true;
		
		while (!stack.isEmpty()){
			
            if(startAgain && !stackStillHasClosingBrace()) { //If it is a new parent and children nodes and stack is invalid
            	return; // stack invalid
            } else { //stack is still valid
            	top=stack.pop();
            	if (startAgain) { //If it is new parent and children nodes
            		startAgain = false;
		            if(top.indexOf(")")>=0) {
		            	nodeChildrenString = "";
		            	parentFound = false;
		            	countClosedBrace = getClosedBraceCount(top);
		            	if(countClosedBrace>1) {
		            		tempTop = top.substring(1);
		            		continue;
		            	} else if(countClosedBrace==1) {
		            		tempTop = "";
		            		continue;
		            	} 
		            } 
            	} else {
            		if (top.indexOf("(")>=0) {
		            	parentFound = true;
		            	startAgain = true;
		            	
		            	nodeChildrenString = " " + top + nodeChildrenString;
		            	
	            		System.out.println("nodeChildrenString is "+nodeChildrenString);
	            		writeToFile(nodeChildrenString);
		            	
		            	top = top.substring(1); 
		            	stack.push(top);	
		            	
		            	if(!tempTop.equals("")) {
		        			stack.push(tempTop);
		        		}
		            	
		            	
		            } else if (top.indexOf("(")<0 && top.indexOf(")")<0 ){
		            	nodeChildrenString = " (" + top + nodeChildrenString;
		            	continue;
		            }
		            if(!parentFound && !top.contains(")") && !top.contains("("))
		            	nodeChildrenString = " (" + top + nodeChildrenString;
            	} 
            }
        }
		//System.out.println("End of method");	
	}
	
	private void writeToFile(String nodeChildrenString) {
		if(nodeChildrenString.startsWith(" "))
			nodeChildrenString = nodeChildrenString.trim();
		String content = "\n'"+ nodeChildrenString+"', " + "unclassifiedTag";
		
		String outputFilename = "\\cse5328-training-data.arff";
		
		fileHelper.saveToFile(content, "result", outputFilename, "UTF-8");

	}

	private boolean stackStillHasClosingBrace() {
		boolean hasClosingBrace = false;
		Iterator<String> value = stack.iterator(); 
		while (value.hasNext()){
			if(value.next().indexOf(")")>=0) {
				hasClosingBrace = true;
				break;
			}
		}
		return hasClosingBrace;
	}

	static int getClosedBraceCount(String string) { 

        int count = 0;
        for (int i = 0; i < string.length(); i++) {  
			if (string.charAt(i) == ')')  
                count++;                  
        }
		return count; 
    } 

	public static void getFileToProcess(File folder){

		if(folder == null) {
			try {
				throw new FileNotFoundException("Folder " + folder + " does not exist.");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		for (final File file : folder.listFiles()) {
			if (file.isDirectory()) {
				getFileToProcess(file);
			} else {		
				try{
					fileName = getFileWithRelativePath(folder, file);			
				  } catch (Exception e) {
					  e.printStackTrace();
				  }
			}
		}
	}
	
	private static String getFileWithRelativePath(final File folder, final File file) {
		return folder + "\\" + file.getName();
	}
	
	 public static void main(String[] args){
		 
		 AutoClassNameMappingGenerator classNameMappingGenerator = new AutoClassNameMappingGenerator();
		 AutoClassNameMappingGenerator.getFileToProcess(classNameMappingGenerator.folder);
		 classNameMappingGenerator.classnameMapping();
	 }

}
