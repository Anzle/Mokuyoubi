
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;


/*
 *  Open a TCP socket on the local machine and contact the peer using the BT peer protocol and request a piece of the file.
    Download the piece of the file and verify its SHA-1 hash against the hash stored in the metadata file. The first time you begin the download, you need to contact the tracker and let it know you are starting to download.
    After a piece is downloaded and verified, the peer is notified that you have completed the piece.
 */
public class Peer{

	private DataOutputStream to_peer;
	private DataInputStream from_peer;
	
	private Socket peer_socket;
	private String peer_ip;
	private int port_number;
	private byte[] peer_id;
	
	//Who is accepting/looking for anything?
	private boolean am_choking;
	private boolean am_interested;
	private boolean peer_choking;
	private boolean peer_interested;
	
	//Peer responses
	private byte[] recieved_message;
	private boolean alive;
	private boolean busy;
	private boolean[] bitfield;
	
	
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
		from_peer = new DataInputStream(peer_socket.getInputStream());
		
		//Initial states of our peers
		am_choking = true; // I am choking
		am_interested = false; // I am not interested
		peer_choking = true;
		peer_interested = false;
		
		//Commence the handshaking
		to_peer.write(Message.handshake(info_hash, peer_id));
		to_peer.flush();
		from_peer.readFully(recieved_message);
		Message.validateHandshake(recieved_message, info_hash);
		
		//At this point, the client should be ready to receive messages from the user. 
		
		
		alive = true;
		busy = false;
	}

	/** Sends a magical Columbidae to deliver our message to the connected Peer
	 * If the bird is shot out of flight, print to the error stream
	 * 
	 * @param message
	 * 		The message to be sent via pigeon
	 */
	public void sendMessage(byte[] message){
		try {
			to_peer.write(message);
			to_peer.flush();
		} catch (IOException e) {
			System.err.println("Error sending message: " + message.toString() +
					"/nto peer located at: " + peer_ip);
		}
	}
	
	/**
	 * gets full buffer from peer's data stream
	 * @return
	 * 	byte[] buffer of data
	 */
	private byte[] getResponse() {
		byte[] ret = null;
		try {
			from_peer.readFully(ret);
			return ret;
		} catch (IOException e) {
			System.err.println("Error reading from peer located at " + this.peer_ip + ":" + this.port_number + "(peerID: " + this.peer_id.toString() + ")");
		}
		return null;
	}

	/**
	 * Reads a block of file data
	 * @return
	 * 		piece data wrapper for data
	 */
	private Block readPeice() {
		int length = 0;
		try {
			length = from_peer.readInt();
			byte messageID = from_peer.readByte();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] data = getResponse();
		return new Block(data, length - 1);
	}
	
	/**
	 * test if peer is connected
	 * @return
	 */
	public boolean isAlive() {
		return alive;
	}
	
	/**
	 * test if client is waiting for data from the peer
	 * @return
	 */
	public boolean isBusy() {
		return busy;
	}
	
	/**
	 * get data bitfield of pieces that the peer has
	 * @return boolean[] bitfield
	 */
	public boolean[] getBitfield(){
		return bitfield;
	}
	
	/**
	 * sends request to peers for a piece of the file
	 * @param pieceIndex piece index
	 * @param pieceOffset position in peice to start downloading
	 * @param length length in bytes to request from peer
	 * @return
	 * 		returns null if no data was downloaded. Piece object containing data and length info 
	 */
	public Block requestBlock(int pieceIndex, int pieceOffset, int length) {
		if(this.peer_choking){
			return null;
		}
		//busy = true;
		
		byte[] message = Message.buildRequest(pieceIndex, pieceOffset, length);
		sendMessage(message);
		return readPeice();
		//TODO
	}

	/** Send this message to keep the Peer connection active; sent every two minutes if not other message sent*/
	public void keepAlive(){ sendMessage(Message.keep_alive);}
	
	/**Inform the Peer that you are not accepting at this time*/
	public void choke(){ 
		sendMessage(Message.choke);
		this.am_choking = true;}
	
	/**Inform the Peer that we are once again accepting messages*/
	public void unchoke(){ 
		sendMessage(Message.unchoke);
		this.am_choking = false;}
	
	/** Inform the Peer that we are interested in them.
	 * Like... Hey Sexy; how about we go somewhere private and transfer some files*/
	public void interested(){ 
		sendMessage(Message.intrested);
		this.am_interested = true;
	}
	
	/** Inform the Peer that we aren't interested in what they have
	 * LIke... You're a nice Peer and all, but I'm... not looking for a relationship 
	 * */
	public void uninterested(){
		sendMessage(Message.uninterested);
		this.am_interested = false;
	}
	
	/**Inform the Peer of our last downloaded piece
	 * @param pieceIndex
	 * 		the 0 based index of the last Piece we downloaded
	 */
	public void have(int pieceIndex){
		byte[] have = Message.have;
		have[2] = (byte)pieceIndex;
		sendMessage(have);
	}
}
