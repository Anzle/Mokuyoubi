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
	
	private FileBuilder file;
	
	public TorrentHandler(TorrentInfo torInfo, Tracker tracker, String outputfile){
		this.torInfo = torInfo;
		this.bitfield = new boolean[this.torInfo.piece_hashes.length];
		this.running = true;
		this.error_death = false;
		this.file = new FileBuilder(outputfile, torInfo.file_length);
	}
	
	public boolean download(){
		
		while(running){

			Vector<Peer> peers = tracker.requestPeers();
			
			for(Peer p : peers){
				if(!currentPeers.contains(p))
					currentPeers.add(p);
				if(p.isAlive() && !p.isBusy()){
					int pieceIndex = getNextPiece(p);
					if(pieceIndex >= 0){
						int offset = 0;
						int pLen = pieceLength(pieceIndex);
						int left = pLen;
						int length = Math.min(MAX_PIECE_LENGTH, left);
						while(left > 0){
							Piece piece = p.requestPiece(pieceIndex, offset, length);
							file.write(piece.getData(), pLen * pieceIndex + offset);
							offset += length;
							left -= length;
							length = Math.min(MAX_PIECE_LENGTH, left);
						}
					}
				}
				
			}
		}
		
		
		if(error_death)
			return false;
		
		return true;
	}

	private int pieceLength(int pieceIndex) {
		int length = 0;
		if(pieceIndex == this.bitfield.length - 1){
			length = this.torInfo.file_length % this.torInfo.piece_length;
		}
		if(length == 0)
			length = this.torInfo.piece_length;
		return length;
	}

	private int getNextPiece(Peer p) {
		boolean[] peerBitfield = p.getBitfield();
		for(int i = 0; i < bitfield.length; i++){
			if(!bitfield[i] && peerBitfield[i]){
				return i;
			}
			
		}
		return -1;
	}
	
	public boolean savePiece(int piece, byte[] data){
		return file.write(data, piece * torInfo.piece_length);
	}

}
