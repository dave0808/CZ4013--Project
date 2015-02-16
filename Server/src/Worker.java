import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class Worker implements Runnable {

	private DatagramPacket request = null;
	private DatagramPacket reply = null;
	
	private DatagramSocket outgoing = null;
	
	public Worker(DatagramPacket req){
		this.request = req;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	private byte[] marshall(){
		
		return null;
	}
	
	private void unMarshall(){
		
	}
}
