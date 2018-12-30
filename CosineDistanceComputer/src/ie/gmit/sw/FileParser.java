package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Pattern;

public class FileParser implements Runnable{
	
	private BlockingQueue<Word> queue;
	private String dir;
	private String file;
	
	//Constructors
	public FileParser(BlockingQueue<Word> queue, String dir, String file){
		this.queue = queue;
		this.dir = dir;
		this.file = file;
	}

	public void run() {
		BufferedReader br = null;
		String line = null;
		String savedString = "";
		String stripptedString = null;
		Pattern pattern = Pattern.compile(" ");
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(dir+"/"+file)));
			
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		try {
			while((line = br.readLine()) != null) {
				String[] words = pattern.split(line.toUpperCase().replaceAll("[^A-Za-z0-9 ]", ""));
				
				int arrayLength = words.length;
				
				if (arrayLength % 3 == 1) {
					savedString = words[arrayLength - 1];
					arrayLength -= 1;
				}
				else if (arrayLength % 3 == 2) {
					savedString = words[arrayLength - 2] + " " + words[arrayLength - 1];
					arrayLength -= 2; 
				}
				
				//FOR
				for (int i = 0; i < arrayLength; i+=3) { 
					stripptedString = words[i];
					stripptedString += " " + words[i+1];
					stripptedString += " " + words[i+2];
					
					queue.put(new Word (file.hashCode(), stripptedString.hashCode()));
				}
				
				if (savedString != "") {
					queue.put(new Word (file.hashCode(), savedString.hashCode()));
					savedString = "";
				}
			}
			queue.put(new Poison(file.hashCode(), stripptedString.hashCode())); //finishes
			System.out.println("Finished");
			br.close();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
