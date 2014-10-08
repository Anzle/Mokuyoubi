import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;


public class PeerHost extends Thread{
	
	private ServerSocket socket;
	private int port;
	private String peer_id;
	
	public PeerHost(){
		
		for(int i = 1; i < 10; i++){
			port = 6880 + i;
			try {
				socket = new ServerSocket(port);
				System.out.println("Listen port is: " + port);
			} catch (NumberFormatException e) {
				/* DO NOTHING */
			} catch (IOException e) {
				System.out.println("port " + port + " is in use");
			}
		}
		

		char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
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
}
