import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * data wrapper for the storing piece download information
 * @author Rich
 * @author Rob(kinda)
 *
 */
public class Piece {

	private Block[] blocks;
	private final int pieceSize;
	
	public Piece(int numblocks, int pieceSize){
		this.blocks = new Block[numblocks];
		this.pieceSize = pieceSize;
	}
	
	public void addBlock(Block b, int i){
		this.blocks[i] = b;
	}
	
	public byte[] getData(){
		if(blocks == null)
			return null;
		ByteBuffer ret = ByteBuffer.allocate(pieceSize);
		for(Block b : blocks){
			if(b == null){
				return null;
			}
			ret.put(b.getData());
		}
		return null;
	}
	
	/**
	 * Validate takes the piece from a block(?) and validates it aginst the SHA-1 hash of the piece
	 * 	from the torrent file. It uses the MessageDigest create_hash to create the hash of the piece
	 * @param piece_hashs
	 * 		from the TorrentInfo.piece_hashes.array()
	 * @param piece
	 * 		This is the piece that was downloaded from the peer
	 * @param pieve_index
	 * 		This is the position of the piece's hash within the pieces_hashes(2D array)
	 * */
	public boolean validate(byte[] piece_hashes, byte[] piece, int piece_index){
		//Make sure we don't get an index out of bounds error
		if(piece_index != piece_hashes.length)
			return false;
		
		byte[] hashed_piece;
		MessageDigest create_hash = null;
		//The MessageDigest will use the SHA algorithm to create the hash of our pieces
		try {
			create_hash = MessageDigest.getInstance("SHA");
		} catch (NoSuchAlgorithmException e) {
			System.err.println("The Digester has a tummy ache.");
			e.printStackTrace();
		}
		hashed_piece = create_hash.digest(piece);
		
		//Still checking for index out of bounds errors
		if(piece_hashes.length != hashed_piece.length)
			return false;
		
		for(int i = 0; i<piece_hashes.length;i++)
			if(piece_hashes[i] != hashed_piece[i])
				return false;
		
		return true;
	}

	public int getNextBlock() {
		for(int i = 0; i < this.blocks.length; i++){
			if(blocks[i] == null)
				return i;
		}
		return -1;
		
	}

	public int getLength() {
		return pieceSize;
	}

	public int numBlocks() {
		return this.blocks.length;
	}
}
