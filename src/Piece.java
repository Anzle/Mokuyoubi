import java.nio.ByteBuffer;

/**
 * data wrapper for the storing piece download information
 * @author Rich
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

}
