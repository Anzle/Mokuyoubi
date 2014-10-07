import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileBuilder {
	
	private RandomAccessFile file;

	public FileBuilder(String outputfile, int fileBytes) {
		try {
			this.file = new RandomAccessFile(outputfile, "rw");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
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
	
	public void close(){
		try {
			file.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
