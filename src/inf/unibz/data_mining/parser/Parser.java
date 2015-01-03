package inf.unibz.data_mining.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {
	
	private final static String[] UNUSEFULWORDS = {"the", "a", "an", "and", "or", "is", "are", "not", "in", "from", 
								"by", "to", "version", "name", "archive", "resources", "last", "modified",
								"at", "on", "over", "about", "between", "among", "inside", "you", "he", "she", "we", "it", "they",
								"my", "mine", "your", "yours", "him", "his", "her", "its", "our", "ours", "them", "ther", "theirs",
								"wpr", "com", "us", "uk", "of", "when", "where", "what", "which", "with", "that", "why", "who", "has", "have", "been",
								"dmn", "unh", "edu"};
	
	public static void main(String[] args) throws IOException {
		System.out.println("------------------ FILE'S PARSER FOR TEXT CLASSIFICATION -------------------");
		System.out.println("All the file will be cleaned from punctuation, unuseful spaces and newlines.");
		String finalPath = "./20news_parsed";
		File workDir = new File(finalPath);
		if (workDir.isDirectory()){
			System.out.println("Folder ./20news_parsed already presents, it will be deleted with ll its content.");
			deleteDirectory(workDir);
		}
		boolean checkDir = new File(finalPath).mkdir();
		if(!checkDir){
			System.out.println("Creation folder failed.\nThe system will exit.");
			System.exit(0);
		}			
		System.out.println(finalPath+" directory created.");
		
		String path = "C:\\Users\\Simone\\Dropbox\\Master\\Data Mining\\Project\\news20\\20_newsgroup";
		File folders = new File(path);
		ArrayList<String> folderList = new ArrayList<String>(Arrays.asList(folders.list()));
		System.out.println("Start parsing each file...");
		for(String n : folderList){			
			File currentFolder = new File(path+"\\"+n);
			ArrayList<String> currentList = new ArrayList<String>(Arrays.asList(currentFolder.list()));
			System.out.println("Current folder: "+currentFolder.getName()/*+"\n#files: "+currentList.size()*/);
			boolean newFolder = new File(finalPath+"/"+n).mkdir();
			if(!newFolder){
				System.out.println("Creation folder failed.\nThe system will exit.");
				System.exit(0);
			}
			System.out.println(finalPath+"/"+n+" directory created.");
			for(String currentFile : currentList){				
				File newFile = new File(finalPath+"/"+n+"/"+currentFile);
				FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(fileToString(path, n, currentFile));				
				bw.close();
				fw.close();
			}			
		}
		System.out.println("Done.");
	}
	
	/*Convert the content of the file into a single string.*/
	public static String fileToString(String p, String name, String cf) throws IOException{
		FileReader fr = new FileReader(p+"\\"+name+"\\"+cf);		
		BufferedReader br = new BufferedReader(fr);
		//Removing header and all non characters.
		String contentAllChar = searchFirstEmptyNewline(br).replaceAll("[^A-Za-z ]", " ").toLowerCase();
		//Removing words shorter that 3 characters.
		String contentLongerOne = contentAllChar.replaceAll("\\b\\w{1,3}\\b", "");
		//Removing unuseful words.
		String contentUnusefulWords = removeUnusefulWords(contentLongerOne);
		//Shrinking spaces.
		String contentOneSpace = contentUnusefulWords.replaceAll("( )+", " ");
		fr.close();
		br.close();
//		System.out.println(contentOneSpace);
		return contentOneSpace;
	}
	
	/*Delete the final directory if it is already present in the workspace.*/
	public static boolean deleteDirectory(File path) {
	    if( path.exists() ) {
	      File[] files = path.listFiles();
	      for(int i=0; i<files.length; i++) {
	         if(files[i].isDirectory()) {
	           deleteDirectory(files[i]);
	         }
	         else {
	           files[i].delete();
	         }
	      }
	    }
	    return( path.delete() );
	  }
	
	/*Reads the file until the first empty line that denotes the end of the header's information.*/
	public static String searchFirstEmptyNewline(BufferedReader br) throws IOException{
		String current = br.readLine();
		while (!current.isEmpty()){
			current = br.readLine();
		}
		String content = "";		
		current = br.readLine();
		while (current!=null){
			content = content+" "+current;
			current = br.readLine();
		}
		return content;
	}
	
	/*Reads the file and removes all the unuseful words.*/
	public static String removeUnusefulWords(String content){
		String returnContent = content;
		for(int i=0; i<UNUSEFULWORDS.length; i++){
			String tmp = null;
//			System.out.println(UNUSEFULWORDS[i]);
//			System.out.println(returnContent);
			tmp = returnContent.replaceAll("\\b"+UNUSEFULWORDS[i]+"\\b", "");
			returnContent = tmp;
//			System.out.println(returnContent);
		}
		return returnContent;
	}
	
}
