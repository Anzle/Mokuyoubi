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
public class Peer implements Runnable {

	private DataOutputStream to_peer;
	private DataInputStream from_peer;

	private Socket peer_socket;
	private String peer_ip;
	private int port_number;
	private byte[] peer_id;
	private byte[] info_hash;
	private TorrentHandler handler;

	private PeerHost host;

	/* Our peer ID, foe handshakes */
	private byte[] my_id;

	// Who is accepting/looking for anything?
	private boolean am_choking;
	private boolean am_interested;
	private boolean peer_choking;
	private boolean peer_interested;

	// Peer responses
	private byte[] recieved_message;
	private boolean alive;
	private boolean busy;
	private boolean[] bitfield;

	/**
	 * Peer creates a connects to a peer that we desire to download the file
	 * from.
	 * 
	 * @throws an
	 *             exception when attempting to connect. If it fails, the main
	 *             file should try to contact a separate peer or something
	 */
	public Peer(String ipaddress, int port, byte[] my_id, byte[] info_hash, byte[] peer_id, PeerHost host, TorrentHandler handler) {
		// peer connection information
		peer_ip = ipaddress;
		port_number = port;
		this.info_hash = info_hash;
		this.peer_id = peer_id;
		this.my_id = my_id;
		this.host = host;
		this.handler = handler;
		// Establish connection with a peer
		// System.out.println("Attempting to connect to:" + ipaddress);

		// Initial states of our peers
		am_choking = true; // I am choking
		am_interested = false; // I am not interested
		peer_choking = true;
		peer_interested = false;

		// At this point, the client should be ready to receive messages from
		// the user.

		alive = false;
		busy = false;
	}

	public void run() {
		try {
			connect();
			alive = true;
		} catch (IOException e) {
			System.err.println("peer(" + peer_ip + ")" + e.getMessage());
			return;
		}
		sendHandshake();
		System.out.println("peer(" + peer_ip + ") connected");
		this.unchoke();
		this.interested();
		System.out.println("peer(" + peer_ip + ") interested");
		alive = true;

		while (alive) {

			try {
				if(this.peer_socket.isClosed()){
					alive = false;
					System.out.println("Dead");
					break;
				}
				int length = from_peer.readInt();
				if (length == 0)
					continue;

				int messageID = ((Byte) from_peer.readByte()).intValue();
				System.out.println("l - " + length + " m - " + messageID);
				byte[] data;
				switch (messageID) {
				case 0:
					this.peer_choking = true;
					break;
				case 1:
					this.peer_choking = false;
					break;
				case 2:
					this.peer_interested = true;
					break;
				case 3:
					this.peer_interested = false;
					break;
				case 4:
					int i = from_peer.readInt();
					this.bitfield[i] = true;
					break;
				case 5:
					data = new byte[length - 1];
					from_peer.readFully(data);
					boolean[] bits = new boolean[data.length * 8];
					int boolIndex = 0;
					for (int byteIndex = 0; byteIndex < data.length; ++byteIndex) {
						for (int bitIndex = 7; bitIndex >= 0; --bitIndex) {
							if (boolIndex >= data.length * 8) {
								break;
							}

							bits[boolIndex++] = (data[byteIndex] >> bitIndex & 0x01) == 1 ? true : false;
						}
					}
					this.bitfield = bits;
					break;
				case 6:
					data = new byte[length - 1];
					from_peer.readFully(data);
				case 7:
					int piece = from_peer.readInt();
					int offset = from_peer.readInt();
					data = new byte[length - 9];
					from_peer.readFully(data);
					Block block = new Block(data, piece, offset);
					this.handler.save(block);
					break;
				default:
					System.err.println("invalid messageID form peer " + this.peer_ip);
					break;

				}

			} catch (IOException e) {
				System.err.println("peer is closed " + this.peer_ip);
				break;
			}
		}
	}

	private void connect() throws IOException {
		peer_socket = new Socket(peer_ip, port_number);
		to_peer = new DataOutputStream(peer_socket.getOutputStream());
		from_peer = new DataInputStream(peer_socket.getInputStream());
	}

	public void sendHandshake() {
		// Commence the handshaking
		recieved_message = new byte[68];
		try {
			to_peer.write(Message.handshake(info_hash, my_id));
			to_peer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("peer(" + peer_ip + ")" + e.getMessage());
			e.printStackTrace();
			System.err.println("This error brought to you by: Our Handshake");
		}
		try {
			from_peer.readFully(recieved_message);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.err.println("This error brought to you by: Handshake from Peer:" + peer_ip + " ID" + peer_id.toString());
		}
		if (!Message.validateHandshake(recieved_message, info_hash, peer_id))
			System.err.println("Malformatted handshake from Peer:" + peer_ip + " ID" + peer_id.toString() +
					"\nWe shouldn't talk to them anymore. . .");
	}

	/**
	 * Sends a magical Columbidae to deliver our message to the connected Peer
	 * If the bird is shot out of flight, print to the error stream
	 * 
	 * @param message
	 *            The message to be sent via pigeon
	 */
	public void sendMessage(byte[] message) {
		try {
			to_peer.write(message);
			to_peer.flush();
		} catch (IOException e) {
			System.err.println("Error sending message: " + message.toString() +
					"/nto peer located at: " + peer_ip);
		}
	}

	/**
	 * test if peer is connected
	 * 
	 * @return
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * test if client is waiting for data from the peer
	 * 
	 * @return
	 */
	public boolean isBusy() {
		return busy;
	}

	/**
	 * get data bitfield of pieces that the peer has
	 * 
	 * @return boolean[] bitfield
	 */
	public boolean[] getBitfield() {
		return bitfield;
	}

	/**
	 * sends request to peers for a piece of the file
	 * 
	 * @param pieceIndex
	 *            piece index
	 * @param pieceOffset
	 *            position in peice to start downloading
	 * @param length
	 *            length in bytes to request from peer
	 * @return returns null if no data was downloaded. Piece object containing
	 *         data and length info
	 */
	public void requestBlock(int pieceIndex, int pieceOffset, int length) {
		if (this.peer_choking) {
			return;
		}
		// busy = true;

		byte[] message = Message.buildRequest(pieceIndex, pieceOffset, length);
		sendMessage(message);
		// TODO
	}

	/**
	 * Send this message to keep the Peer connection active; sent every two
	 * minutes if not other message sent
	 */
	public void keepAlive() {
		sendMessage(Message.keep_alive);
	}

	/** Inform the Peer that you are not accepting at this time */
	public void choke() {
		sendMessage(Message.choke);
		this.am_choking = true;
	}

	/** Inform the Peer that we are once again accepting messages */
	public void unchoke() {
		sendMessage(Message.unchoke);
		this.am_choking = false;
	}

	/**
	 * Inform the Peer that we are interested in them. Like... Hey Sexy; how
	 * about we go somewhere private and transfer some files
	 */
	public void interested() {
		sendMessage(Message.intrested);
		this.am_interested = true;
	}

	/**
	 * Inform the Peer that we aren't interested in what they have LIke...
	 * You're a nice Peer and all, but I'm... not looking for a relationship
	 * */
	public void uninterested() {
		sendMessage(Message.uninterested);
		this.am_interested = false;
	}

	/**
	 * Inform the Peer of our last downloaded piece
	 * 
	 * @param pieceIndex
	 *            the 0 based index of the last Piece we downloaded
	 */
	public void have(int pieceIndex) {
		byte[] have = Message.have;
		have[2] = (byte) pieceIndex;
		sendMessage(have);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Peer) {
			Peer p = (Peer) o;

			if (p.peer_ip.equals(this.peer_ip) && p.port_number == this.port_number) {
				return true;
			}

			return false;

		} else if (o == this) {
			return true;
		} else {
			return false;
		}
	}

	public String getIP() {
		return this.peer_ip;
	}
}
