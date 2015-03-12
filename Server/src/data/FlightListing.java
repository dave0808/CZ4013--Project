package data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class FlightListing {

	Set<Flight> Flights;
	
	public FlightListing(String filename){
		Flights = Collections.newSetFromMap(new ConcurrentHashMap<Flight, Boolean>());
		
		this.getFlightData(filename);
	}
	
	/*
	 * Get the flight represented by id,
	 * returns null if flight does not exist
	 */
	public Flight getFlight(int id){

		Flight requested = null;
		
		for(Flight f : Flights){
			if (f.getId() == id){
				requested = f;
				break;
			}
		}
		return requested;
	}
	
	public Set<String> getDest(String src){
		Set<String> destinations = new HashSet<String>();
		
		for(Flight f: this.Flights){
			if(f.getSource().equals(src)){
				destinations.add(f.getDestination());
			}
		}
		return destinations;
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
	/*
	 * Attempts to book number n tickets for flight id
	 * True if possible and false if not. 
	 * This should always be carried out after checking if flight exists
	 */
	public boolean buyTickets(int id, int n){
		
		boolean booked = false;
		
		for(Flight f : Flights){
			if(f.getId() == id){
				booked = f.bookSeats(n);
				break;
			}
		}
		return booked;
	}
	
	/*
	 * Attempts to book number n tickets for flight id
	 * True if possible and false if not. 
	 * This should always be carried out after checking if flight exists
	 */
	public boolean cancelTickets(int id, int n){
		
		boolean canceled = false;
		
		for(Flight f : Flights){
			if(f.getId() == id){
				canceled = f.cancelSeats(n);
				break;
			}
		}
		return canceled;
	}
	private void getFlightData(String fileName){
		// TODO parse file and fill datastructure
	}
}
