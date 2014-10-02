import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 *  Open a TCP socket on the local machine and contact the peer using the BT peer protocol and request a piece of the file.
    Download the piece of the file and verify its SHA-1 hash against the hash stored in the metadata file. The first time you begin the download, you need to contact the tracker and let it know you are starting to download.
    After a piece is downloaded and verified, the peer is notified that you have completed the piece.
 */
public class Peer {

	private DataOutputStream to_peer;
	private BufferedReader from_peer;
	private Socket peer_socket;
	private String peer_ip;
	private int port_number;
	
	//Who is accepting/looking for anything?
	private boolean am_choking;
	private boolean am_interested;
	private boolean peer_choking;
	private boolean peer_interested;
	
	
	/** Peer creates a connects to a peer that we desire to download the file from.
	 * @throws an exception when attempting to connect. If it fails, the main file should try
	 * to contact a separate peer or something*/
	public Peer(String ipaddress, int port, byte[] peer_id, byte[] info_hash) throws Exception{
		//peer connection information
		peer_ip = ipaddress;
		port_number = port;
		
		//Establish connection with a peer
		peer_socket = new Socket(peer_ip, port_number);
		to_peer = new DataOutputStream(peer_socket.getOutputStream());
		from_peer = new BufferedReader( new InputStreamReader(peer_socket.getInputStream()));
		
		//Initial states of our peers
		am_choking = true; // I am choking
		am_interested = false; // I am not interested
		peer_choking = true;
		peer_interested = false;
		
		//Commence the handshake
		to_peer.write(Message.handshake(info_hash, peer_id));
		
		//At this point, the client should be ready to recieve messages from the user. 
		
	}
	
	
	//These will be static functions that take a peer it wants to
	//communicate with as input, then sends out the data to that peer
	
	//<length prefix><message ID><payload>
	// keep-alive <0><><> prevents the peer from closing the connection
	// choke <1><0><>
	// unchoke <1><1><>
	// interested <1><2><>
	//uninterested <1><3><>
	// have <5><4><Zero-Based Index/count from 0/ of the piece that has just been downloaded and verified>
	// request <13><6>< <index><begin><length> >
	   /*<index> is an integer specifying the zero-based piece index
		* <begin> is an integer specifying the zero-based byte offset within the piece
		* is typically 2^14 (16384) bytes. A smaller piece should only be used if the piece length is not divisible by 16384.
		*/
	// piece <9+X><7>< <index><begin><block>  >
		/*<index> is an integer specifying the zero-based piece index
		 * <begin> is an integer specifying the zero-based byte offset within the piece, and 
		 * <block> which is a block of data, and is a subset of the piece specified by <index>
		 */
}
