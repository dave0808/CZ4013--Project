import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import data.FlightListing;
import data.InvalidMessageException;
import data.Message;


public class Worker implements Runnable {

	// Request packet, should be passed in from Server thread.
	private DatagramPacket request = null;
	// Socket for sending response
	private DatagramSocket outgoing = null;
	// Class storing flight data
	private Server masterServer;
	
	public Worker(Server master, DatagramPacket req){
		
		this.masterServer = master;
		this.request = req;
		
	}
	
	@Override
	public void run() {
		DatagramPacket reply = null;
		
		int id = -1;
		
		ByteBuffer bb = ByteBuffer.wrap(this.request.getData());
		
		try {
			
			id = this.getID(bb);
			
			Message dummy = new Message(id, new DatagramPacket(null, 0, this.request.getSocketAddress()));

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
			byte[] byteReply = this.process(bb);
			reply = new DatagramPacket(byteReply, byteReply.length, this.request.getSocketAddress());
		}
		
		this.outgoing = new DatagramSocket(8888);
		this.outgoing.send(reply);
		
		// We only want to add to the history is it was sucssfully sent
		this.masterServer.getRequestHistory().add(new Message(id, reply));
		this.outgoing.close();
		
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
	
	private int getID(ByteBuffer buff) throws InvalidMessageException{
		int id = -1;
		try{
		id = buff.getInt();
		return id;
		}
		catch(BufferUnderflowException e){
			throw new InvalidMessageException("The data is not in correct format");
		}
	}
	
	private byte[] process(ByteBuffer buff) throws InvalidMessageException{
		byte[] reply;
		
		try{
			
			/* Get the next byte in the buffer, 
			 * This byte corresponds to the service requested
			 * Switch and carry out appropriate service,
			 * if request is incomplete or doesn't represent a service then throw InvalidMessageException
			 */
			switch(buff.get()){
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
	private byte[] messageType1(ByteBuffer buff) throws InvalidMessageException{
		return null;
		
	}
	
	/*	“A service that allows a user to query the departure time, airfare and seat availability
	 *  by specifying the flight identifier. If the flight with the requested identifier does not 
	 *  exist, an error message should be returned.”
	*/	
	private byte[] messageType2(ByteBuffer buff) throws InvalidMessageException{
		return null;
		
	}
	
	/*	“A service that allows a user to make seat reservation on a flight by specifying the flight 
	 * identifier and the number of seats to reserve. On successful reservation, an acknowledgement 
	 * is returned to the client and the seat availability of the flight should be updated at the server.
	 * In case of incorrect user input (e.g., not-existing flight identifier or insufficient number of 
	 * available seats), a proper error message should be returned.”
	*/	
	private byte[] messageType3(ByteBuffer buff) throws InvalidMessageException{
		return null;
		
	}

	/*	“A service that allows a user to monitor updates made to the seat availability 
	 * information of a flight at the server for a time period called monitor interval. 
	 * To register, the client provides the flight identifier and the length of monitor 
	 * interval to the server.”
	*/	
	private byte[] messageType4(ByteBuffer buff) throws InvalidMessageException{
		return null;
		
	}

	/*	“A service that allows a user to cancel a seat reservation on a flight by specifying the 
	 * flight identifier and the number of seats to cancel. On successful cancellation, an 
	 * acknowledgement is returned to the client and the seat availability of the flight should be 
	 * updated at the server. In case of incorrect user input (e.g., not-existing flight identifier or 
	 * insufficient number of booked seats), a proper error message should be returned.”
	*/	
	private byte[] messageType5(ByteBuffer buff) throws InvalidMessageException{
		return null;
	}

	/*	“A service that allows a user to query which destinations are available for any given source. 
	 * The user will specify the source as a string and will receive as a reply a list of possible 
	 * destinations as strings.”
	*/	
	private byte[] messageType6(ByteBuffer buff) throws InvalidMessageException{
		return null;
	}

}
