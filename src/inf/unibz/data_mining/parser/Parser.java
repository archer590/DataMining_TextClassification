package inf.unibz.data_mining.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Random;

public class Parser {
	
	private final static String[] UNUSEFULWORDS = {"the", "a", "an", "and", "or", "is", "are", "not", "in", "from", 
								"by", "to", "version", "name", "archive", "resources", "last", "modified",
								"at", "on", "over", "about", "between", "among", "inside", "you", "he", "she", "we", "it", "they",
								"my", "mine", "your", "yours", "him", "his", "her", "its", "our", "ours", "them", "ther", "theirs",
								"wpr", "com", "us", "uk", "of", "when", "where", "what", "which", "with", "that", "why", "who", "has", "have", "been",
								"dmn", "unh", "edu", "things", "article"};
	
	public static void main(String[] args) throws IOException {
		System.out.println("------------------ FILE'S PARSER FOR TEXT CLASSIFICATION -------------------");
		System.out.println("All the file will be cleaned from punctuation, unuseful spaces and newlines.");
		String finalPath = "C:\\Users\\Simone\\20news_parsed";
		File workDir = new File(finalPath);
		int numberFiles = 0;
		if (workDir.isDirectory()){
			System.out.println("Folder ./20news_parsed already presents, it will be deleted with all its content.");
			deleteDirectory(workDir);
		}
		System.out.println("Folder and its content deleted correctly.");
		System.out.println();
		boolean checkDir = new File(finalPath).mkdir();
		if(!checkDir){
			System.out.println("Creation folder failed.\nThe system will exit.");
			System.exit(0);
		}			
		System.out.println(finalPath+" directory created.");
		System.out.println();
		String path = "C:\\Users\\Simone\\Dropbox\\Master\\Data Mining\\Project\\news20\\20_newsgroup";
		File folders = new File(path);
		ArrayList<String> folderList = new ArrayList<String>(Arrays.asList(folders.list()));
		System.out.println("----- Start parsing each file -----");
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
				String contentCurrentFile = fileToString(path, n, currentFile);
				if(!contentCurrentFile.isEmpty()){
					File newFile = new File(finalPath+"/"+n+"/"+currentFile);
					FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(contentCurrentFile);	
					bw.close();
					fw.close();
					numberFiles++;
				}				
			}			
		}
		System.out.println();
		System.out.println("Parsing file phase completed.\nNumber of files created: "+numberFiles);
		System.out.println();
		System.out.println();
		System.out.println("----- Starting to write the files .arff -----");
		creationARFF(numberFiles);
		System.out.println();
		System.out.println();
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
	
	/*Reading again all the file created before and collecting them in an Hashtable to write the .arff file.*/
	public static void creationARFF(int numberFiles) throws IOException{
		Hashtable<Integer, String[]> contentTrainingData = new Hashtable<Integer, String[]>();
		Hashtable<Integer, String[]> contentTestData = new Hashtable<Integer, String[]>();
		Random r = new Random();
		int numberAttributes=0, counter=0, percentageTest =0, counterTest=0;
		String path = "C:\\Users\\Simone\\20news_parsed";
		File folders = new File(path);
		ArrayList<String> folderList = new ArrayList<String>(Arrays.asList(folders.list()));
		System.out.println("----- Start reading each file for creating the hashtable -----");
		for(String n : folderList){
			File currentFolder = new File(path+"/"+n);
			ArrayList<String> currentList = new ArrayList<String>(Arrays.asList(currentFolder.list()));
//			System.out.println("Current folder: "+currentFolder.getName()/*+"\n#files: "+currentList.size()*/);
			for(String file : currentList){				
				FileReader fr = new FileReader(path+"/"+n+"/"+file);
//				System.out.println(path+"/"+n+"/"+file);
				BufferedReader br = new BufferedReader(fr);
				String[] content = br.readLine().split(" ");				
				if (counter == 0)
					numberAttributes = content.length;
				if (content.length > numberAttributes)
					numberAttributes = content.length;
				if(r.nextBoolean() && !contentTestData.containsKey(r.nextInt(numberFiles)) && percentageTest<200){
					contentTestData.put(counterTest, content);
					percentageTest++;
					counterTest++;
				} else {
					contentTrainingData.put(counter, content);
					counter++;
				}
//				System.out.println("Counter: "+counter+"\nContent Lenght: "+content.length+"\nAttrs: "+numberAttributes);
				fr.close();
				br.close();				
			}
		}
		System.out.println();
		System.out.println("Files for training data: "+contentTrainingData.size()+"\nFiles for testing data: "+contentTestData.size());
		System.out.println();
		writeTrainingFile(contentTrainingData, numberAttributes);
		writeTestingFile(contentTestData, numberAttributes);
	}
	
	/*Writing (physically) the 20_newsgroups_training.arff file.*/
	@SuppressWarnings("resource")
	public static void writeTrainingFile(Hashtable<Integer, String[]> contentAllFiles, int numberAttributes) throws IOException {
		System.out.println("Start to write 20_newsgroups_training.arff file...");
//		System.out.println("Final number files scanned: "+contentAllFiles.size()+"\nFinal number of attributes: "+numberAttributes);
		File trainingData = new File("./20_newsgroups_training.arff");
		FileWriter fw = new FileWriter(trainingData);
		BufferedWriter bw = new BufferedWriter(fw);
		
		String newline = "";
		newline = "% 1. Title: 20 news groups\n"
				+ "%\n"
				+ "% Created by: Luca Bellettati and Simone Tritini (Free University of Bozen-Bolzano) @ 2015\n\n"
				+ "@RELATION\20_newsgroups\n\n";
		bw.write(newline);
		for(int i=0; i<numberAttributes; i++){
			newline = "@ATTRIBUTE\ta"+i+"\tNOMINAL\n";
			bw.write(newline);
		}
		newline = "\n@DATA\n";
		bw.write(newline);

		for(Integer key : contentAllFiles.keySet()){
			
			String[] currentContent = contentAllFiles.get(key);
			newline = "";
			for(int i=0; i<currentContent.length; i++){
				newline = newline + currentContent[i];
				if(i!=0 && i<currentContent.length-1)
					newline = newline+",";
			}
			bw.write(newline+"\n");
		}
		
//		fw.close();
//		bw.close();
	}
	
	/*Writing (physically) the 20_newsgroups_test.arff file.*/
	@SuppressWarnings("resource")
	public static void writeTestingFile(Hashtable<Integer, String[]> contentAllFiles, int numberAttributes) throws IOException {
		System.out.println("Start to write 20_newsgroups_test.arff file...");
//		System.out.println("Final number files scanned: "+contentAllFiles.size()+"\nFinal number of attributes: "+numberAttributes);
		File arff = new File("./20_newsgroups_test.arff");
		FileWriter fw = new FileWriter(arff);
		BufferedWriter bw = new BufferedWriter(fw);
		String newline = "";
		newline = "% 1. Title: 20 news groups\n"
				+ "%\n"
				+ "% Created by: Luca Bellettati and Simone Tritini (Free University of Bozen-Bolzano) @ 2015\n\n"
				+ "@RELATION\20_newsgroups\n\n";
		bw.write(newline);
		for(int i=0; i<numberAttributes; i++){
			newline = "@ATTRIBUTE\ta"+i+"\tNOMINAL\n";
			bw.write(newline);
		}
		newline = "\n@DATA\n";
		bw.write(newline);

		for(Integer key : contentAllFiles.keySet()){
			String[] currentContent = contentAllFiles.get(key);
			newline = "";
			for(int i=0; i<currentContent.length; i++){
				newline = newline + currentContent[i];
				if(i!=0 && i<currentContent.length-1)
					newline = newline+",";
			}
			bw.write(newline+"\n");
		}
		
//		fw.close();
//		bw.close();
	}
	
}
