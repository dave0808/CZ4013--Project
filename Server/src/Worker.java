import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import data.Flight;
import data.InvalidMessageException;
import data.Message;


public class Worker implements Runnable, Observer {

	// Request packet, should be passed in from Server thread.
	private DatagramPacket request = null;
	// Socket for sending response
	private DatagramSocket outgoing = null;
	// Class storing flight data
	private Server masterServer;
	// Length of time it should monitor a flight, 
	//should only be set to a number if  service is requested
	private int monitorLen = 0;
	
	private int id = -1;
	private byte mType = 0;
	
	public Worker(Server master, DatagramPacket req, DatagramSocket soc){
		
		this.masterServer = master;
		this.request = req;
		this.outgoing = soc;
	}
	
	@Override
	public void run() {
		DatagramPacket reply = null;
		
		// Wrap data inside bytebuffer for easy unmarshalling
		ByteBuffer bb = ByteBuffer.wrap(this.request.getData());
		
		try {
			this.id = bb.getInt();
			this.mType = bb.get();
			
			System.out.println("id---------" + this.id + "---------");
			System.out.println("type---------" + this.mType + "---------");		
			
			// Create dummy flight to use as comparison 
			Message dummy = new Message(id, new DatagramPacket(new byte[1], 0, this.request.getSocketAddress()));

		// Check if History already contains this request
		if(this.masterServer.getRequestHistory().contains(dummy)){
			// If so, get reply and resend	
			for (Message m : this.masterServer.getRequestHistory()){
				if(m.equals(dummy)){
					reply = m.getReply();
				}
			}
		}
		else{

			// if not then continue on and create reply
			ByteBuffer tempReply = this.process(bb, this.mType);
			
			// Wrap reply byte array inside bytebuffer
			ByteBuffer byteReply = ByteBuffer.wrap(new byte[1000]);

			// Ass necessary data into reply
			byteReply.putInt(this.id);
			byteReply.put(this.mType);
			
			byteReply.put(tempReply.array(), 0, tempReply.position());
			
			int pos = byteReply.position();
			
			byte[] toSend = new byte[pos];
			
			System.arraycopy(byteReply.array(), 0, toSend, 0, pos);
			
			reply = new DatagramPacket(toSend, pos, this.request.getSocketAddress());
			reply.setPort(2222);
		}
		
		// Open up socket for sending reply
		this.outgoing.send(reply);
		
		// We only want to add to the history is it was successfully sent
		this.masterServer.getRequestHistory().add(new Message(id, reply));
		// Close socket
		
		// Check if we need to perform the monitoring function
		if(this.monitorLen > 0){
			double time = 0;
			while(true){
				
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Add elapsed time onto counter
				time += 0.25;
				
				// Check if required time has elapsed. 
				if(time >= this.monitorLen){
					// If so, break out of while loop
					break;
				}
			}
		}
		
		} catch (InvalidMessageException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ByteBuffer process(ByteBuffer buff, byte type) throws InvalidMessageException{
		ByteBuffer reply = null;
		
		try{
			
			/* Get the next byte in the buffer, 
			 * This byte corresponds to the service requested
			 * Switch and carry out appropriate service,
			 * if request is incomplete or doesn't represent a service then throw InvalidMessageException
			 */
			
			
			switch(type){
				case 1 : reply = this.messageType1(buff);
				break;
				case 2 : reply = this.messageType2(buff);
				break;
				case 3 : reply = this.messageType3(buff);
				break;
				case 4 : reply = this.messageType4(buff);
				break;
				case 5 : reply = this.messageType5(buff);
				break;
				case 6 : reply = this.messageType6(buff);
				break;
				default : throw new InvalidMessageException("Unkown Service type");
			}
			return reply;
		}
		catch(BufferUnderflowException e){
			throw new InvalidMessageException("The data is not in correct format");
		}
	}
	/*	“A service that allows a user to query the flight identifier(s) by specifying the source and 
	 * destination places. If multiple flights match the source and destination places, all of them 
	 * should be returned to the user. If no flight matches the source and destination places, an error
	 *  message should be returned.”	
	*/	
	private ByteBuffer messageType1(ByteBuffer buff) throws InvalidMessageException{
		
		int origLen = 0;
		String orig = "";
		int destLen = 0;
		String dest = "";
		
		// Initialise reply data buffer
		ByteBuffer reply = ByteBuffer.wrap(new byte[1000]);
		
		// Initialise default reply data
		List<Flight> flights;
		
		try{
			// Get length of origin string
			origLen = buff.getInt();
			// Build origin string
			for(int i = 0; i < origLen;i++){
				orig += (char)buff.get();
			}
			// Get length of destination string
			destLen = buff.getInt();
			// Build destination string
			for(int i = 0; i < destLen;i++){
				dest += (char)buff.get();
			}
			
			// Check database for flights
			flights = this.masterServer.getFlightData().getFlight(orig, dest);
			// Build response
			int len = flights.size();
			
			/*
			 * If len is 0 here it means that one or both airports don't exist, or there is no route
			 * 
			 * For reply values for len mean:
			 * 
			 * len = 0 : no route
			 * len = -1 : only origin not present
			 * len = -2 : only destination not present
			 * len = -3 : both origin and destination not present
			 */
			if(len == 0){	
				
				if(this.masterServer.getFlightData().hasAirport(orig)){
					len =-1;
				}
				if(this.masterServer.getFlightData().hasAirport(dest)){
					len =-2;
				}
				reply.putInt(len);
			}
			else{
				// Routes were found, put in reply
				reply.putInt(len);
				for(int i = 0; i < len; i++){
					reply.putInt(flights.get(i).getId());
				}
			}
		}
		catch(BufferUnderflowException e){
			throw new InvalidMessageException("The data is not in correct format");
		}
		
		return reply;		
	}
	
	/*	“A service that allows a user to query the departure time, airfare and seat availability
	 *  by specifying the flight identifier. If the flight with the requested identifier does not 
	 *  exist, an error message should be returned.”
	*/	
	private ByteBuffer messageType2(ByteBuffer buff) throws InvalidMessageException{
		
		// Initialise reply data buffer
		ByteBuffer reply = ByteBuffer.wrap(new byte[1000]);
		
		// Initialise default reply data
		int avail = -1;
		char[] flightTime = new char[12];
		float cost = (float) 0.0;
		Flight requested = null;
		
		try{
			// Get flight ID from request data
			int id = buff.getInt();
			
			// Get flight represented by id
			requested = this.masterServer.getFlightData().getFlight(id);
			
			// Check to see if flight exists
			if(requested != null){
				// If so then get data required for reply
				avail = requested.getAvailability();
				flightTime = requested.getDepartureTime();
				cost = requested.getAirFair();
			}
			
			// Add reply data to reply buffer
			reply.putInt(avail);
			for(int i = 0; i < 12; i++){
				reply.put((byte)flightTime[i]);
			}
			reply.putFloat(cost);
		}
		catch(BufferUnderflowException e){
			throw new InvalidMessageException("The data is not in correct format");
		}
		
		return reply;		
	}
	
	/*	“A service that allows a user to make seat reservation on a flight by specifying the flight 
	 * identifier and the number of seats to reserve. On successful reservation, an acknowledgement 
	 * is returned to the client and the seat availability of the flight should be updated at the server.
	 * In case of incorrect user input (e.g., not-existing flight identifier or insufficient number of 
	 * available seats), a proper error message should be returned.”
	*/	
	private ByteBuffer messageType3(ByteBuffer buff) throws InvalidMessageException{
		
		// Initialise reply data buffer
		ByteBuffer reply = ByteBuffer.wrap(new byte[1000]);
		
		// Initialise default reply data
		int reserved = -1;
		Flight requested = null;
		
		try{
			// Get required information from request data
			int id = buff.getInt();
			int n = buff.getInt();
			
			// Attempt to retrieve requested flight
			requested = this.masterServer.getFlightData().getFlight(id);
			
			// Check is requested flight exists
			if(requested != null){
				// If so, attempt to book seats
				if(this.masterServer.getFlightData().getFlight(id).bookSeats(n)){
					reserved = n;
				}
				else reserved = 0;
			}
			// Add reply data to reply buffer
			reply.putInt(reserved);
		}
		catch(BufferUnderflowException e){
			throw new InvalidMessageException("The data is not in correct format");
		}
		
		return reply;	
	}

	/*	“A service that allows a user to monitor updates made to the seat availability 
	 * information of a flight at the server for a time period called monitor interval. 
	 * To register, the client provides the flight identifier and the length of monitor 
	 * interval to the server.”
	*/	
	private ByteBuffer messageType4(ByteBuffer buff) throws InvalidMessageException{
		
		// Initialise reply data buffer
		ByteBuffer reply = ByteBuffer.wrap(new byte[1000]);
		
		// Initialise default reply data
		int avail = -1;
		Flight requested = null;
		
		try{
			// Get required information from request data
			int id = buff.getInt();
			this.monitorLen = buff.getInt();
			
			// Attempt to retrieve requested flight
			requested = this.masterServer.getFlightData().getFlight(id);
			
			// Check is requested flight exists
			if(requested != null){
				// If so then get availability and set this as observer
				avail = this.masterServer.getFlightData().getFlight(id).getAvailability();
				this.masterServer.getFlightData().getFlight(id).addObserver(this);
			}
			
			// Add reply data to reply buffer
			reply.putInt(avail);
		}
		catch(BufferUnderflowException e){
			throw new InvalidMessageException("The data is not in correct format");
		}
		
		return reply;	
	}

	/*	“A service that allows a user to cancel a seat reservation on a flight by specifying the 
	 * flight identifier and the number of seats to cancel. On successful cancellation, an 
	 * acknowledgement is returned to the client and the seat availability of the flight should be 
	 * updated at the server. In case of incorrect user input (e.g., not-existing flight identifier or 
	 * insufficient number of booked seats), a proper error message should be returned.”
	*/	
	private ByteBuffer messageType5(ByteBuffer buff) throws InvalidMessageException{
		
		// Initialise reply data buffer
				ByteBuffer reply = ByteBuffer.wrap(new byte[1000]);
				
				// Initialise default reply data
				int canceled = -1;
				Flight requested = null;
				
				try{
					// Get required information from request data
					int id = buff.getInt();
					int n = buff.getInt();
					
					// Attempt to retrieve requested flight
					requested = this.masterServer.getFlightData().getFlight(id);
					
					// Check is requested flight exists
					if(requested != null){
						// If so, attempt to cancel seats
						if(this.masterServer.getFlightData().getFlight(id).cancelSeats(n)){
							canceled = n;
						}
						else canceled = 0;
					}
					// Add reply data to reply buffer
					reply.putInt(canceled);
				}
				catch(BufferUnderflowException e){
					throw new InvalidMessageException("The data is not in correct format");
				}
				
				return reply;	
	}

	/*	“A service that allows a user to query which destinations are available for any given source. 
	 * The user will specify the source as a string and will receive as a reply a list of possible 
	 * destinations as strings.”
	*/	
	private ByteBuffer messageType6(ByteBuffer buff) throws InvalidMessageException{
		
		// Initialise reply data buffer
		ByteBuffer reply = ByteBuffer.wrap(new byte[1000]);
		
		// Initialise default reply data
		int n = 0;
		
		Set<String> destinations;
		
		try{
			int len = buff.getInt();
			String src = "";
			
			// The length of the source airport cannot be less than 1
			if(len < 1) throw new InvalidMessageException("The data is not in correct format");
			
			// Get the source string from request
			for(int i = 0; i < len; i++){
				src += (char)buff.get();
			}
			// Get destinations and reply data
			destinations = this.masterServer.getFlightData().getDest(src);
			
			n = destinations.size();
			
			// Put number of destinations into reply
			reply.putInt(n);
			
			for(String s : destinations){
				char[] temp = s.toCharArray();
				
				// Add length of following character sequence
				reply.putInt(temp.length);
				// Enter the character sequence
				for(int j = 0; j < temp.length; j++){
					reply.put((byte)temp[j]);
				}
			}
		}
		catch(BufferUnderflowException e){
			throw new InvalidMessageException("The data is not in correct format");
		}
		
		return reply;	
	}

	@Override
	public void update(Observable arg0, Object arg1) {

		int avail = (int) arg1;
		// Warp reply byte array inside bytebuffer
		ByteBuffer byteReply = ByteBuffer.wrap(new byte[1000]);

		// Insert data into reply
		byteReply.putInt(this.id);
		byteReply.put(this.mType);
		byteReply.putInt(avail);
		
		// Package reply
		DatagramPacket reply = new DatagramPacket(byteReply.array(), byteReply.array().length, this.request.getSocketAddress());
	
	
	try {
		// open up socket for sending reply
		this.outgoing.send(reply);
		
		System.out.println("Reply sent");
		System.out.println(reply.getData());
		
		// We only want to add to the history is it was successfully sent
		this.masterServer.getRequestHistory().add(new Message(id, reply));
		
	} catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}
}