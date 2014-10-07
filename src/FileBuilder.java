import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Random access file wrapper to simplify writing to the file
 * 
 * @author Rich
 *
 */
public class FileBuilder {
	
	private RandomAccessFile file;
	
	/**
	 * Constructor
	 * 
	 * @param outputfile url of the output file
	 * @param fileBytes total expected length of the output file
	 */
	public FileBuilder(String outputfile, int fileBytes) {
		try {
			this.file = new RandomAccessFile(outputfile, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * writes the content byte[] to the file starting at position
	 * 
	 * @param content data to be written
	 * @param postition start position of the file pointer
	 * @return
	 * 		true - the file was written correctly
	 * 		false - there was a error while writing to the file
	 */
	public boolean write(byte[] content, int postition){
		try {
			file.seek(postition);
			file.write(content);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * save and close the file pointer after the file is finsihed being written to
	 */
	public void close(){
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
