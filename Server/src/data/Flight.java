package data;
public class Flight {

	private int id;
	private String source;
	private String destination;
	private double departureTime;
	private float airFair;
	private int availability;
	
	public Flight(int id, String src, String dst, double time, float fair, int avail){
		this.id = id;
		this.source = src;
		this.destination = dst;
		this.departureTime = time;
		this.airFair = fair;
		this.availability = avail;
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

	public double getDepartureTime() {
		return departureTime;
	}

	public float getAirFair() {
		return airFair;
	}

	public int getAvailability() {
		return availability;
	}
	
	public boolean bookSeats(int no){
		
		if( no > this.availability){
			return false;
		}
		else{
			this.availability =- no;
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
