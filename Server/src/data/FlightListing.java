package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FlightListing {

	Set<Flight> Flights;
	
	public FlightListing(String filename){
		Flights = Collections.newSetFromMap(new ConcurrentHashMap<Flight, Boolean>());
		
		this.getFlightData(filename);
	}
	
	public Flight getFlight(int id) throws InvalidIDException{

		for(Flight f : Flights){
			if (f.getId() == id){
				return f;
			}
		}
		 throw new InvalidIDException("Invalid ID");
	}
	
	public boolean hasAirport(String port){
		
		for(Flight f : Flights){
			if(f.getSource().equals(port)){
				return true;
			}
			else if(f.getDestination().equals(port)){
				return true;
			}
		}
		return false;
	}
	public List<Flight> getFlight(String src, String dest){
		
		List<Flight> temp = new ArrayList<Flight>();
		
		for(Flight f : Flights){
			if(f.getSource().equals(src) && f.getDestination().equals(dest)){
				temp.add(f);
				break;
			}
		}
		return temp;
	}

	public boolean buyTickets(int id, int no) throws InvalidIDException{
		
		for(Flight f : Flights){
			if(f.getId() == id){
				return f.bookSeats(no);
			}
		}
		 throw new InvalidIDException("Invalid ID");	
	}
	
	private void getFlightData(String fileName){
		// TODO parse file and fill datastructure
	}
}
