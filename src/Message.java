	
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
public class Message {

	/**Keeps the peer from disconnecting */
	public static byte[] keep_alive = {0};
	/** */
	public static byte[] choke = {1,0};
	/** */
	public static byte[] unchoke = {1,1};
	/** */
	public static byte[] intrested = {1,2};
	/** */
	public static byte[] uninterested = {1,3};
	
	
	/** Perform the handshake between our client and the peer to begin connections
	 * This method should be called from the Peer.java class
	 * @param info_hash -> 20-byte SHA1 hash, peer_id->20-byte string
	 * @return  the handshake message of the form: <pstrlen><pstr><reserved><info_hash><peer_id>
	 * */
	public static byte[] handshake(byte[] info_hash, byte[] peer_id){
		byte[] hand_shake = new byte[68];
		//<19><BitTorrent protocol><00000000>
		hand_shake[0] = 19; 
		hand_shake[1] = 'B';
		hand_shake[2] = 'i';
		hand_shake[3] = 't';
		hand_shake[4] = 'T';
		hand_shake[5] = 'o';
		hand_shake[6] = 'r';
		hand_shake[7] = 'r';
		hand_shake[8] = 'e';
		hand_shake[9] = 'n';
		hand_shake[10] = 't';
		hand_shake[11] = ' ';
		hand_shake[12] = 'p';
		hand_shake[13] = 'r';
		hand_shake[14] = 'o';
		hand_shake[15] = 't';
		hand_shake[16] = 'o';
		hand_shake[17] = 'c';
		hand_shake[18] = 'o';
		hand_shake[19] = 'l';
		
		//fill 20 -> 27 with '0's 
		for(int i=0;i<8;i++)
			hand_shake[20+i] = 0;
		
		//fill 28 - 47 with the SHA1 hash from info_hash
		for(int i=0; i<20;i++)
			hand_shake[28+i] = info_hash[i];
		
		//fill 48 - 67 with the peer_id
		for(int i=0; i<20;i++)
			hand_shake[48+i] = peer_id[i];
		
		//out handshake is completed, return it to the peer
		return hand_shake;
	}
	
}//End of Message Class
