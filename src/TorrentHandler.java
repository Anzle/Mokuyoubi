import java.util.ArrayList;
import java.util.Vector;

import GivenTools.TorrentInfo;


public class TorrentHandler {
	
	private static int MAX_PIECE_LENGTH = 16384;
	
	private TorrentInfo torInfo = null;
	private Tracker tracker = null;
	private Vector<Peer> currentPeers = null;
	
	private boolean[] bitfield;
	private boolean error_death;
	private boolean running;
	private int pieceIndex;
	
	private FileBuilder file;
	
	/**
	 * Constructor. sets up instance of class
	 * 
	 * @param torInfo TorrentInfo object from the .torrent file
	 * @param tracker reference to the tracker to pull new peers from
	 * @param outputfile output file location where the finished file will be saved
	 */
	public TorrentHandler(TorrentInfo torInfo, Tracker tracker, String outputfile){
		this.torInfo = torInfo;
		this.tracker = tracker;
		this.bitfield = new boolean[this.torInfo.piece_hashes.length];
		this.running = true;
		this.error_death = false;
		this.file = new FileBuilder(outputfile, torInfo.file_length);
	}
	
	/**
	 * Manages downloading the files pieces from the peers
	 * 
	 * @return
	 * 		true - operation completes successfully
	 * 		false - an error was caught during execution causing the download to fail
	 */
	public boolean download(){
		while(running){

			ArrayList<Peer> peers = tracker.requestPeers();

			if(true)
				return true;
			for(Peer p : peers){
				if(!currentPeers.contains(p))
					currentPeers.add(p);
				if(p.isAlive() && !p.isBusy()){
					pieceIndex = getNextPiece(p);
					if(pieceIndex >= 0){
						int offset = 0;
						int pLen = pieceLength(pieceIndex);
						int left = pLen;
						int length = Math.min(MAX_PIECE_LENGTH, left);
						int numBlocks = pLen / length;
						if(pLen % length > 0)
							numBlocks++;
						Piece piece = new Piece(numBlocks, pLen);
						int bindex = 0;
						while(left > 0){
							Block b = p.requestBlock(pieceIndex, offset, length);
							piece.addBlock(b, bindex);
							offset += length;
							left -= length;
							length = Math.min(MAX_PIECE_LENGTH, left);
							bindex++;
						}
						byte[] data = piece.getData();
						if(data != null)
							file.write(data, torInfo.piece_length * pieceIndex + offset);
					}
				}
			}
		}
		
		
		if(error_death)
			return false;
		
		return true;
	}
	
	/**
	 * Calculated the size of a piece to download
	 * 
	 * @param pieceIndex indexof the peice
	 * @return number of bytes in length the piece is as an integer 
	 */
	private int pieceLength(int pieceIndex) {
		int length = 0;
		if(pieceIndex == this.bitfield.length - 1){
			length = this.torInfo.file_length % this.torInfo.piece_length;
		}
		if(length == 0)
			length = this.torInfo.piece_length;
		return length;
	}
	
	/**
	 * Finds the next piece the the client needs and the peer has
	 * 
	 * @param p peer the user is connecting with
	 * @return int index of the next piece to download
	 */
	private int getNextPiece(Peer p) {
		boolean[] peerBitfield = p.getBitfield();
		for(int i = 0; i < bitfield.length; i++){
			if(!bitfield[i] && peerBitfield[i]){
				return i;
			}
			
		}
		return -1;
	}
	
	/**
	 * Saves a finished data byte array for a piece to the file.
	 * 
	 * @param piece index of the piece
	 * @param data byte[] data of the file to be written
	 * @return
	 * 		return true if data is written and false if an error is caught
	 */
	public boolean savePiece(int piece, byte[] data){
		return file.write(data, piece * torInfo.piece_length);
	}
	
	/** Return's the current Piece Index for the have message
	 * @return
	 * 		the current pieteIndex as an integer
	 * */
	public int getPieceIndex(){
		return pieceIndex;
	}

}
