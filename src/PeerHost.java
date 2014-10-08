import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class PeerHost extends Thread{
	
	private ServerSocket socket;
	private int port;
	private String peer_id;
	private boolean[] bitfield;
	
	public PeerHost(){
		this.bitfield = new boolean[0];

		for(int i = 1; i < 10; i++){
			port = 6880 + i;
			try {
				socket = new ServerSocket(port);
				System.out.println("Listen port is: " + port);
				break;
			} catch (NumberFormatException e) {
				/* Nope */
			} catch (IOException e) {
				System.out.println("port " + port + " is in use");
			}
		}
		

		char[] chars = "0123456789ABCDEF".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
		    char c = chars[random.nextInt(chars.length)];
		    sb.append(c);
		}
		peer_id = sb.toString();
		
		start();
	}

	public void run(){
		if(true)
			return;
		Socket c;
		try {
			while((c = socket.accept()) != null){
				c.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getPeerID(){
		return peer_id;
	}
	
	public int getPort(){
		return port;
	}

	public boolean[] getBitfield() {
		return bitfield;
	}
	
	public void setHave(int index){
		if(index < this.bitfield.length)
			this.bitfield[index] = true;
	}

	public void makeBitField(int length) {
		this.bitfield = new boolean[length];
	}
}
