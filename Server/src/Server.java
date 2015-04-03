import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import data.FlightListing;
import data.Message;


public class Server {
	
	// UDP socket for requests
	private DatagramSocket incoming = null;
	// Buffer for requests coming in
	private byte[] buffer = new byte[1000];
	// Thread pool to manage reply worker threads
	private ExecutorService pool;
	// Class containing flight data
	private FlightListing flightData;
	// Storage for request history
	private Set<Message> requestHistory;
	// Name of the file containing the flight data
	private static final String FILENAME = "flightDeets.csv";
	
	public Server(){
		
		System.out.println("Initialising Server.");
		// Using CachedThreadpool to allow dynamic response
		this.pool = Executors.newCachedThreadPool();
		
		System.out.println("Initialising storage.");
		// Initialise Flight data store
		try {
			this.flightData = new FlightListing(FILENAME);
			System.out.println("Flight list initialised.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
		
		// Initialise structure for storing flight history
		this.requestHistory =  Collections.newSetFromMap(new ConcurrentHashMap<Message,Boolean>());
		
		try {
			// Open UDP Socket on port 8888
			System.out.println("Opening datagram socket.");
			this.incoming = new DatagramSocket(8888);
			
			while(true){
				
				// New DatagramPacket to receive requests
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				
				// Blocking, so will wait for new request to be received
				System.out.println("Listening for incoming packet.");
				this.incoming.receive(request);
				
				// Pass onto worker thread to process and create reply
				System.out.println("Passing to worker thread.");
				this.pool.execute(new Worker(this, request));
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the flightData
	 */
	public FlightListing getFlightData() {
		return flightData;
	}

	/**
	 * @return the requestHistory
	 */
	public Set<Message> getRequestHistory() {
		return requestHistory;
	}
	
}
