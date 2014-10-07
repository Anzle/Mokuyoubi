import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import GivenTools.BencodingException;


public class main{

	public static void main(String[] args) {
	
	if(args.length!=2){
		System.out.println("THERE WAS AN ERROR WITH THE INPUTS");
		return;
	}
	
	String tfile=""; // .torrent file to be loaded 
	String sfile=""; // name of the file to save the data to
		
	for(int i=0;i<args.length;i++){
		if(i==0){
			tfile=args[i];
		}else if(i==1){
			sfile=args[i];
		}
	}

//the following is a check to make sure the command line arguments were stored correctly
	System.out.println("tfile: "+tfile);  
	System.out.println("sfile: "+sfile);
	
	
	
	
	File file = new File(tfile);
	long fsize = -1;
	byte[] tbytes = null;
	InputStream file_stream;

	try
	{
		file_stream = new FileInputStream(file);
		fsize = file.length();
		
		// Initialize the byte array for the file's data
		tbytes = new byte[(int) fsize];

		int offset = 0;
		int aread = 0;

		// Read from the file
		while (offset < tbytes.length
				&& (aread = file_stream.read(tbytes, offset,
						tbytes.length - offset)) >= 0)
		{
			offset += aread;
		}

		file_stream.close();

	}
	catch (FileNotFoundException e)
	{
		System.err
				.println("Error: [TorrentFileHandler.java] The file \""
						+ tfile
						+ "\" does not exist. Please make sure you have the correct path to the file.");
		return;
	}
	catch (IOException e)
	{
		System.err
				.println("Error: [TorrentFileHandler.java] There was a general, unrecoverable I/O error while reading from \""
						+ tfile + "\".");
		System.err.println(e.getMessage());
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	}

}
