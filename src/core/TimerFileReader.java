package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.TimerTask;

public class TimerFileReader extends TimerTask {
	private File file;
	public TimerFileReader(String filename) throws FileNotFoundException{
		File file = new File(filename);
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
	}
	
	public void run() {  
        
    } 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
