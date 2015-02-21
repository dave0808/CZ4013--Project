import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.FlightListing;


public class Server {
	
	// UDP socket for requests
	private DatagramSocket incoming = null;
	// Buffer for requests coming in
	private byte[] buffer = new byte[1000];
	// Threadpool to manage reply worker threads
	private ExecutorService pool;
	// Class containing flight data
	private FlightListing flightData;
	// Name of the file containing the flight data
	private static final String FILENAME = "flightdata.txt";
	
	public Server(){
		
		// Using CachedThreadpool to allow dynamic response
		this.pool = Executors.newCachedThreadPool();
		
		// Initialise Flight data store
		flightData = new FlightListing(FILENAME);
		
		try {
			// Open UDP Socket on port 8888
			this.incoming = new DatagramSocket(8888);
			
			while(true){
				
				// New DatagramPacket to receive requests
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				
				// Blocking, so will wait for new request to be received
				this.incoming.receive(request);
				
				// Pass onto worker thread to process and create reply
				this.pool.execute(new Worker(this.flightData, request));
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
