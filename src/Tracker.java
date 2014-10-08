import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

import GivenTools.Bencoder2;
import GivenTools.BencodingException;
import GivenTools.ToolKit;
import GivenTools.TorrentInfo;


public class Tracker {

    private final String HEXCHARS = "0123456789ABCDEF";
	
	public Tracker(TorrentInfo alltinfo, PeerHost host) throws IOException{
		
		URL url = alltinfo.announce_url;
		
		byte[] info_hash = alltinfo.info_hash.array();
		String ih_str = "";
				
		StringBuilder sb = new StringBuilder(info_hash.length * 2);
		for (byte b : info_hash)
	        sb.append(HEXCHARS.charAt((b & 0xF0) >> 4))
	            .append(HEXCHARS.charAt((b & 0x0F)));
		ih_str = sb.toString();

		String query = "announce?info_hash=" + ih_str + "&peer_id=" + host.getPeerID() + "&port=" + host.getPort() + "&left=" + alltinfo.file_length + "&uploaded=0&downloaded=0";
		
		System.out.println("file name: " + alltinfo.file_name);

		//ToolKit.print(alltinfo.torrent_file_map);// this is only used to debug
												// and print the map

		// PART 3 STARTS HERE-

		// Variable getlist is the URL to connect to

		System.out.println("THE URL IS: " + url);

		String inline = "";
		URL urlobj;

		byte[] tracker_response = null;
		
		urlobj = new URL(url, query);
		
		System.out.println(urlobj.toString());

		HttpURLConnection uconnect = (HttpURLConnection) urlobj.openConnection();
		uconnect.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(
				new InputStreamReader(uconnect.getInputStream()));

		StringBuffer response = new StringBuffer();

		while ((inline = in.readLine()) != null) {

			tracker_response = inline.getBytes();

			System.out.println(inline);// prints stuff
			response.append(inline);

		}
		in.close();
		try {
			HashMap h = (HashMap) Bencoder2.decode(response.toString().getBytes());
			ToolKit.print(h);
		} catch (BencodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("response from tracker in the form of byte[]: " + tracker_response);
	}
	
	/**
	 * retrieves a list of peers from the server
	 * @return
	 * 		list of currently connected peers
	 */
	public Vector<Peer> requestPeers(){
		//TODO 
		return new Vector<Peer>();
	}

}
