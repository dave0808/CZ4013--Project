package data;

import java.net.DatagramPacket;
import java.net.SocketAddress;

public class Message {

	private int requestID;
	
	private SocketAddress clientIP;
		
	private DatagramPacket reply;
	
	public Message(int id, DatagramPacket reply){
		this.requestID = id;
		this.reply = reply;
		this.clientIP = this.reply.getSocketAddress();
		
	}
	
	/**
	 * @return the reply
	 */
	public DatagramPacket getReply() {
		return reply;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientIP == null) ? 0 : clientIP.hashCode());
		result = prime * result + requestID;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Message)) {
			return false;
		}
		Message other = (Message) obj;
		if (clientIP == null) {
			if (other.clientIP != null) {
				return false;
			}
		} else if (!clientIP.equals(other.clientIP)) {
			return false;
		}
		if (requestID != other.requestID) {
			return false;
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	
}
