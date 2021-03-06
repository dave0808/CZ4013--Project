package data;

import java.util.Observable;

public class Flight extends Observable {

	private int id;
	private String source;
	private String destination;
	private char[] departureTime;
	private float airFair;
	private int availability;
	private int booked;
	
	public Flight(int id, String src, String dst, char[] time, float fair, int nSeats){
		this.id = id;
		this.source = src;
		this.destination = dst;
		this.airFair = fair;
		this.availability = nSeats;
		this.booked = 0;

		if(time.length == 12){
			this.departureTime = time;
		}
		else{
			this.departureTime = new char[12];
		}
		
	}

	public int getId() {
		return id;
	}

	public String getSource() {
		return source;
	}

	public String getDestination() {
		return destination;
	}

	public char[] getDepartureTime() {
		return departureTime;
	}

	public float getAirFair() {
		return airFair;
	}

	public int getAvailability() {
		return availability;
	}
	
	public boolean bookSeats(int no){
		
		if( no > this.availability && (no > 0)){
			return false;
		}
		else{
			this.availability -= no;
			this.booked += no;
			this.setChanged();
			this.notifyObservers(this.availability);
			this.clearChanged();
			return true;
		}
	}

	public boolean cancelSeats(int no){
		
		if( no > this.booked && (no > 0)){
			return false;
		}
		else{
			this.booked -= no;
			this.availability += no;
			this.setChanged();
			this.notifyObservers(this.availability);
			this.clearChanged();
			return true;
		}
	}
	
	@Override
	public boolean equals(Object obj){
		
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof Flight))
			return false;

		Flight other = (Flight) obj;
		if (other.getId() == this.id) 
			return true;
		else 
			return false;
	}
	
	@Override
	public int hashCode(){
		return this.id;
	}
}
