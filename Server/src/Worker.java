import java.net.DatagramPacket;
import java.net.DatagramSocket;

import data.FlightListing;


public class Worker implements Runnable {

	// Request packet, should be passed in from Server thread.
	private DatagramPacket request = null;
	// Reply Packet, should be generated and sent back out.
	private DatagramPacket reply = null;
	// Socket for sending response
	private DatagramSocket outgoing = null;
	// Class storing flight data
	private FlightListing flightData;
	
	public Worker(FlightListing flights, DatagramPacket req){
		
		this.flightData = flights;
		this.request = req;
	}
	
	@Override
	public void run() {
		
		
	}
	
	private void unMarshall(){
		
	}

}
