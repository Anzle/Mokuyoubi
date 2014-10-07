import java.nio.ByteBuffer;

	
	//These will be static functions that will produce the needed messages
	
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


/**Message should do message validation as well, in my opinion*/
public class Message {

	/**Keeps the peer from disconnecting */
	public static byte[] keep_alive = {0};
	
	/**Resist Data Sharing*/
	public static byte[] choke = {1,0};
	/**Enable data sharing */
	public static byte[] unchoke = {1,1};
	/**show interest in a file */
	public static byte[] intrested = {1,2};
	/**show lack of interest in what the peer has to offer*/
	public static byte[] uninterested = {1,3};
	
	
	/*The beginning of the handshake: this is always the same*/
	private static byte[] handshake_header= {19,'B','i','t','T','o','r','r','e','n','t',
											' ','p','r','o','t','o','c','o','l',
											0,0,0,0,0,0,0,0};
	
	/** Perform the handshake between our client and the peer to begin connections
	 * This method should be called from the Peer.java class
	 * @param info_hash -> 20-byte SHA1 hash, peer_id->20-byte string
	 * @return  the handshake message of the form: <pstrlen><pstr><reserved><info_hash><peer_id>
	 * */
	public static byte[] handshake(byte[] info_hash, byte[] peer_id){
		byte[] hand_shake = new byte[68];
		//<19><BitTorrent protocol><00000000>
		//copy the handshake_header into the handshake message
		for(int i = 0; i<handshake_header.length; i++)
			hand_shake[i] = handshake_header[i];

		//fill 28 - 47 with the SHA1 hash from info_hash
		for(int i=0; i<20;i++)
			hand_shake[28+i] = info_hash[i];
		
		//fill 48 - 67 with the peer_id
		for(int i=0; i<20;i++)
			hand_shake[48+i] = peer_id[i];
		
		//out handshake is completed, return it to the peer
		return hand_shake;
	}
	
	/** Check if the handshake message from a peer was a valid message. 
	 * @param 
	 * 		The handshake received from a peer
	 * @return
	 * 		True if it is valid, false otherwise
	 * */
	public static boolean validateHandshake(byte[] recieved_handshake,byte[] info_hash){
		
		//Check if the message is the correct size
		if(recieved_handshake.length != 68 )
			return false;
		//Check if it has the proper heading
		for(int i=0;i<handshake_header.length;i++)
			if(recieved_handshake[i] != handshake_header[i])
				return false;
		//Check if it has the correct SHA1 hash
		for(int i=0; i<info_hash.length;i++)
			if(recieved_handshake[28+i] != info_hash[i])
				return false;
		//If those check out... we will just assume the peerID is correct, for right now
		return true;
	}
	
	/**
	 * Generate the block request message to request part of a block from the peer
	 * @param pieceIndex piece of the file to download part of
	 * @param pieceOffset distance into the piece to start downloading
	 * @param length length in bytes of data to download starting at the offset
	 * @return byte array of the finished message
	 */
	public static byte[] buildRequest(int pieceIndex, int pieceOffset, int length) {
		//message is 14 long including length byte
		ByteBuffer responseBuff = ByteBuffer.allocate(14);
		responseBuff.put((byte)13); //set length
		responseBuff.put((byte)6); //set messageID
		responseBuff.putInt(pieceIndex); //<index>
		responseBuff.putInt(pieceOffset); //<offset>
		responseBuff.putInt(length); //<block>
		return responseBuff.array();
	}
	
}//End of Message Class
