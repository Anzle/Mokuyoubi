/**
 * data wrapper for the storing piece download information
 * @author Rich
 *
 */
public class Piece {
	
	private int length = 0;
	private byte[] data = null;
	
	/**
	 * set piece length
	 * 
	 * @param i length in bytes
	 */
	public void setLength(int i){
		this.length = i;
	}
	
	/**
	 * sets data if piece
	 * 
	 * @param data byte[] of data
	 */
	public void setData(byte[] data){
		this.data = data;
	}

	/**
	 * Retrieves byte[] data
	 * @return piece byte data
	 */
	public byte[] getData() {
		return data;
	}

}
