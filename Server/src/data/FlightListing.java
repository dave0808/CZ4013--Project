package data;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlightListing {

	// Collection of Flights
	Set<Flight> Flights;
	
	BufferedReader reader;
	
	// Variables used for pattern matching of file line
	Pattern lineSyntax;
	String correctLine = "(\\d+),(.+),(.+),(\\d{12}),(\\d+[.]\\d{2}),(\\d+)";
	
	public FlightListing(String filename) throws IOException{
		Flights = Collections.newSetFromMap(new ConcurrentHashMap<Flight, Boolean>());
		
		this.lineSyntax = Pattern.compile(this.correctLine);
		
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
	
	/*
	 * Returns a set containing all of the possible destinations leaving from 
	 * airport designated by argument src
	 */
	public Set<String> getDest(String src){
		Set<String> destinations = new HashSet<String>();
		
		for(Flight f: this.Flights){
			if(f.getSource().equalsIgnoreCase(src)){
				destinations.add(f.getDestination());
			}
		}
		return destinations;
	}
	
	/*
	 * Attempts to find if an airport is present within the flight info storage
	 * True if airport is present as either source or destination
	 */
	public boolean hasAirport(String port){
		
		for(Flight f : Flights){
			if(f.getSource().equalsIgnoreCase(port)){
				return true;
			}
			else if(f.getDestination().equalsIgnoreCase(port)){
				return true;
			}
		}
		return false;
	}
	/*
	 * Iterates through list of flights attempting to find routes between 
	 * source and destination.
	 * If no route is found then list is empty. 
	 */
	public List<Flight> getFlight(String src, String dest){
		
		List<Flight> temp = new ArrayList<Flight>();
		
		for(Flight f : Flights){
			if(f.getSource().equalsIgnoreCase(src) && f.getDestination().equalsIgnoreCase(dest)){
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
		// Attempt to find flight
		for(Flight f : Flights){
			if(f.getId() == id){
				// If flight is found then attempt to book seats
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
		
		// Find flight
		for(Flight f : Flights){
			if(f.getId() == id){
				// If flight is found, cancel.
				canceled = f.cancelSeats(n);
				break;
			}
		}
		return canceled;
	}
	
	/*
	 * Attempts to open file refered to by arg fileName.
	 * If file is in correct format then entries are 
	 * added to flight storage structure.
	 */
	private void getFlightData(String fileName) throws IOException{
		
		// Initialise file reader wrapped in a buffered Reader
		reader = new BufferedReader(new FileReader(fileName));
		String currentLine = "";
		
		while((currentLine = reader.readLine()) != null) {
			// Create matcher for pattern
			Matcher match = this.lineSyntax.matcher(currentLine);

			if(match.matches()){
				// Match each variable to specific part of Pattern
				int id = Integer.parseInt(match.group(1));
				String src = match.group(2);
				String dst = match.group(3);
				String date = match.group(4);
				float cost = Float.parseFloat(match.group(5));
				int seats = Integer.parseInt(match.group(6));
				
				// Add new flight into info storage
				this.Flights.add(new Flight(id,src,dst,date.toCharArray(),cost,seats));
			}
			else{
				throw new IOException("File incorrect format");
			}
		}
	}
	
}
