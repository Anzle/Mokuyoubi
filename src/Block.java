
public class Block {
	
	private final byte[] data;
	private final int length;
	
	public Block(byte[] data, int length){
		this.data = data;
		this.length = length;
	}
	
	public byte[] getData(){
		return data;
	}
	
	public int getLength(){
		return length;
	}

}
