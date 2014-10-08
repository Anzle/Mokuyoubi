
public class Block {
	
	private final byte[] data;
	private final int piece;
	private final int offset;
	
	public Block(byte[] data, int piece, int offset){
		this.data = data;
		this.piece = piece;
		this.offset = offset;
	}
	
	public byte[] getData(){
		return data;
	}
	
	public int getLength(){
		return data.length;
	}
	
	public int getPiece(){
		return piece;
	}
	
	public int getOffset(){
		return offset;
	}

}
