
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
public class Peer implements Comparable<Peer>{

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

	private Piece readPeice() {
		Piece p = new Piece();
		try {
			p.setLength(from_peer.readInt());
			byte messageID = from_peer.readByte();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] data = getResponse();
		p.setData(data);
		return p;
	}

	public boolean isAlive() {
		return alive;
	}

	public boolean isBusy() {
		return busy;
	}
	
	public boolean[] getBitfield(){
		return bitfield;
	}

	public Piece requestPiece(int pieceIndex, int pieceOffset, int length) {
		if(this.peer_choking){
			return null;
		}
		//busy = true;
		
		byte[] message = Message.buildRequest(pieceIndex, pieceOffset, length);
		sendMessage(message);
		return readPeice();
		//TODO
	}

	public boolean equals(Object o){
		if (o == null || !(o instanceof Peer)) {
			return false;
		}

		Peer peer = (Peer) o;
		byte[] peerID = peer.peer_id;

		for (int i = 0; i < this.peer_id.length; i++) {
			if (this.peer_id[i] != peerID[i]) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int compareTo(Peer p) {
		// TODO Auto-generated method stub
		return 0;
	}

}
