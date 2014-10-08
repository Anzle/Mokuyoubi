import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import GivenTools.Bencoder2;
import GivenTools.BencodingException;
import GivenTools.TorrentInfo;
import GivenTools.ToolKit;


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
	InputStream fstream;

	try
	{
		fstream = new FileInputStream(file);
		fsize = file.length();
		
		// Initialize the byte array for the file's data
		tbytes = new byte[(int) fsize];

		int point = 0;
		int done = 0;

		// Read from the file
		while (point < tbytes.length
				&& (done = fstream.read(tbytes, point,
						tbytes.length - point)) >= 0)
		{
			point += done;
		}

		fstream.close();

	}
	catch (FileNotFoundException e)
	{
		return;
	}
	catch (IOException e)
	{
		return;
	}

	//tbytes is the byte array with all metainfo
	
	URL getlist=null;
	
	try {
		TorrentInfo alltinfo=new TorrentInfo(tbytes);
		
		System.out.println("file name: "+alltinfo.file_name);
		
		getlist=alltinfo.announce_url;
		
		ToolKit tkit= new ToolKit();
		
		tkit.print(alltinfo.torrent_file_map);// this is only used to debug and print the map
		
	
	} catch (BencodingException e) {
		
		e.printStackTrace();
	}
	
	
	// PART 3 STARTS HERE-
	
	//Variable getlist is the URL to connect to
	
	System.out.println("THE URL IS: "+getlist);
	
	String inline=""; 
	URL urlobj;
	
	try {
		
		String toscrape=getlist.toString();
		//_________________________________
		
		String finalurl="";
		for(int i=0;i<toscrape.length();i++){
			
			String check=toscrape.substring(i,i+8);
			
			if(check.equals("announce")){
				finalurl=toscrape.substring(0, i)+"scrape";
				break;
			}
		}
		//__________________________________
		
		urlobj = new URL(finalurl);
	
     HttpURLConnection uconnect = (HttpURLConnection) urlobj.openConnection();
     uconnect.setRequestMethod("GET");
     //int responseCode = uconnect.getResponseCode();

     BufferedReader in = new BufferedReader(
             new InputStreamReader(uconnect.getInputStream()));
     
     StringBuffer response = new StringBuffer();

     while ((inline = in.readLine()) != null) {
         System.out.println(inline);//prints stuff
    	 response.append(inline);
         
     }
     in.close(); 
     
	} catch (MalformedURLException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
	
	
	
	}

	

}
