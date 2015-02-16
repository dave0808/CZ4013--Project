import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {
	
	private DatagramSocket incoming = null;
	private byte[] buffer = new byte[1000];
	private ExecutorService pool;
	
	public Server(){
		
		this.pool = Executors.newCachedThreadPool();
		
		try {
			this.incoming = new DatagramSocket(8888);
			
			while(true){
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				this.incoming.receive(request);
				this.pool.execute(new Worker(request));
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
