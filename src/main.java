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
import java.util.HashMap;
import java.util.Random;

import GivenTools.Bencoder2;
import GivenTools.BencodingException;
import GivenTools.ToolKit;
import GivenTools.TorrentInfo;

public class main {

	public static void main(String[] args) {

		if (args.length != 2) {
			System.out.println("THERE WAS AN ERROR WITH THE INPUTS");
			return;
		}

		String tfile = ""; // .torrent file to be loaded
		String sfile = ""; // name of the file to save the data to

		for (int i = 0; i < args.length; i++) {
			if (i == 0) {
				tfile = args[i];
			} else if (i == 1) {
				sfile = args[i];
			}
		}

		// the following is a check to make sure the command line arguments were
		// stored correctly
		System.out.println("tfile: " + tfile);
		System.out.println("sfile: " + sfile);

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

		} catch (FileNotFoundException e)
		{
			return;
		} catch (IOException e)
		{
			return;
		}

		// tbytes is the byte array with all metainfo

		try {
			TorrentInfo alltinfo = new TorrentInfo(tbytes);
			PeerHost host = new PeerHost();
			Tracker tracker = new Tracker(alltinfo, host);
			TorrentHandler handler = new TorrentHandler(alltinfo, tracker, sfile);
			handler.download();
			

		} catch (BencodingException e) {

			e.printStackTrace();
		}

	}

}
