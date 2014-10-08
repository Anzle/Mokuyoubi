import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import GivenTools.Bencoder2;
import GivenTools.BencodingException;
import GivenTools.ToolKit;
import GivenTools.TorrentInfo;

public class Tracker {

	private final char[] HEXCHARS = "0123456789ABCDEF".toCharArray();
	private TorrentInfo torinfo;
	private PeerHost host;
	private URL url;
	private byte[] info_hash;
	
	ArrayList<Peer> peers;

	public Tracker(TorrentInfo torinfo, PeerHost host) {
		this.torinfo = torinfo;
		this.host = host;
		this.url = torinfo.announce_url;
		info_hash = torinfo.info_hash.array();
		System.out.println("file name: " + torinfo.file_name);
		
	}
	
	private void getPeers() throws IOException{
		String ih_str = "";

		for (int i = 0; i < info_hash.length; i++) {
			if ((info_hash[i] & 0x80) == 0x80) { // if the byte data has the most
												// significant byte set (e.g. it
												// is negative)
				ih_str += "%" + this.HEXCHARS[(info_hash[i] & 0xF0) >>> 4] + this.HEXCHARS[info_hash[i] & 0x0F];
			} else {
				try { // If the byte is a valid ascii character, use URLEncoder
					ih_str += URLEncoder.encode(new String(new byte[] { info_hash[i] }), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					System.out.println("URL formation error:" + e.getMessage());
				}
			}
		}

		String query = "announce?info_hash=" + ih_str + "&peer_id=" + host.getPeerID() + "&port=" + host.getPort() + "&left=" + torinfo.file_length + "&uploaded=0&downloaded=0";


		// ToolKit.print(alltinfo.torrent_file_map);// this is only used to
		// debug
		// and print the map

		// PART 3 STARTS HERE-

		// Variable getlist is the URL to connect to


		URL urlobj;

		byte[] tracker_response = null;

		urlobj = new URL(url, query);		

		HttpURLConnection uconnect = (HttpURLConnection) urlobj.openConnection();
		uconnect.setRequestMethod("GET");

		BufferedReader in = new BufferedReader(
				new InputStreamReader(uconnect.getInputStream()));

		StringBuffer response = new StringBuffer();

		String inline = "";
		while ((inline = in.readLine()) != null) {

			tracker_response = inline.getBytes();

			//System.out.println(inline);// prints stuff
			response.append(inline);

		}
		in.close();
		peers = new ArrayList<Peer>();
		try {
			HashMap<ByteBuffer, Object> h = (HashMap) Bencoder2.decode(response.toString().getBytes());
			//ToolKit.print(h);
			ByteBuffer b = ByteBuffer.wrap("peers".getBytes());

			ByteBuffer ip_key = ByteBuffer.wrap("ip".getBytes());
			ByteBuffer pid_key = ByteBuffer.wrap("peer id".getBytes());
			ByteBuffer port_key = ByteBuffer.wrap("port".getBytes());
			ArrayList<HashMap<ByteBuffer, Object>> list = (ArrayList<HashMap<ByteBuffer, Object>>) h.get(b);
			for(HashMap<ByteBuffer, Object> p_info : list){
				String ip = new String(((ByteBuffer) p_info.get(ip_key)).array());
				byte[] pid = ((ByteBuffer) p_info.get(pid_key)).array();
				int port = (int) p_info.get(port_key);
				
				Peer newPeer = new Peer(ip, port, host.getPeerID().getBytes(), torinfo.info_hash.array(), pid_key.array());
				
				if(newPeer != null){
					peers.add(newPeer);
				}
			}
		} catch (BencodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * retrieves a list of peers from the server
	 * 
	 * @return list of currently connected peers
	 */
	public ArrayList<Peer> requestPeers() {
		// TODO
		try {
			getPeers();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return peers;
	}

}
