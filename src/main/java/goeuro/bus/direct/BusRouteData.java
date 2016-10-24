package goeuro.bus.direct;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class containing information about bus routes and its stations.
 * It is intended to be used as a singleton. Before performing queries, data must be loaded using the parseBusRouteData method.
 * Method hasConnection then can be used to check if two stations have a direct connection by any bus route.
 * <p>In order to make queries faster, we store sets of bus routes indexed by station (an inverted index of the data file),
 * and intersect these sets to check if there is a set containing both dep_sid and arr_sid. If there is at least one intersecting set,
 * there is a direct bus route between dep_sid and arr_sid. 
 * @author Taciano Perez
 */
public class BusRouteData {
	
	/**
	 * Map where the key is stationID and value is the corresponding BusStation.
	 */
	private Map<Integer,BusStation> stations = new HashMap<Integer,BusStation>();

	/**
	 * Returns true if there is a route connecting these two stations, false otherwise. 
	 * @param dep_sid departure station
	 * @param arr_sid arrival station
	 * @return true if the stations are connected, false otherwise
	 */
	public boolean hasConnection(int dep_sid, int arr_sid) {
		// get departure station
		BusStation depStation = stations.get(dep_sid);
		if (depStation == null) return false;
		// get arrival station
		BusStation arrStation = stations.get(arr_sid);
		if (arrStation == null) return false;
		// find routes connecting both the departing and arrival stations
		// (i.e., intersect the route sets of both departing and arrival stations)
		Set<Integer> intersect = depStation.getRouteIDs().parallelStream()
                .filter(arrStation.getRouteIDs()::contains)
                .collect(Collectors.toSet());
		return intersect.size() > 0;	// if there are common routes, return true
	}
	
	/**
	 * Parse a bus route data file.
	 * @param fileName name of the data file
	 */
	public void parseBusRouteData(String fileName) {
		int lineNum = 1;		// current line number
		int expectedRoutes = 0;	// expected number of routes

		// parse the file
		try (Scanner scanner = new Scanner(new File(fileName))) {
			// scan first line --- number of routes
			if (scanner.hasNextLine()) expectedRoutes = Integer.valueOf(scanner.nextLine());
			// scan each route line
			while (scanner.hasNextLine()){
				lineNum++;
				try {
					this.parseBusRouteLine(scanner.nextLine());	// parse each line
				} catch (InvalidRouteException ire) {
					// if we catch an invalid line, log it and continue processing, in hope that other lines will be fine
					System.err.println("Error parsing data file " + fileName + " at line "+ lineNum + ". Description: " + ire.getLocalizedMessage());
					ire.printStackTrace();
				}
			}
		} catch (Exception e) {
			System.err.println("Fatal error parsing data file " + fileName + " at line "+ lineNum + ".");
			e.printStackTrace();
		}
		// check if we found the number of routes expected by the file header
		int numRoutes = lineNum-1; // subtract the first line with the number of routes	
		if (numRoutes != expectedRoutes) {
			System.err.println("Error parsing data file. Expected " + expectedRoutes + " but found " + numRoutes + " entries.");
		}
	}
	
	/**
	 * Internal method to parse a single route entry from the data file. Populate the stations collection.
	 * @param line a line being parsed
	 * @throws InvalidRouteException if there are invalid values in the data file
	 */
	private void parseBusRouteLine(String line) throws InvalidRouteException {
		String[] tokens = line.split(" ");
		if (tokens.length < 3) {
			// we need at least a route and two station IDs!
			throw new InvalidRouteException("Route has less than two stations.");
		}
		try {
			// parse route ID
			int routeID = Integer.valueOf(tokens[0]);
			for (int i=1; i<tokens.length; i++) {	// iterate through stations on this route
				Integer stationID = Integer.valueOf(tokens[i]);
				BusStation station = stations.get(stationID);	// get the station entry
				if (station == null) {	// create and add to collection, if necessary
					station = new BusStation(stationID);
					stations.put(stationID, station);
				}
				station.addRouteID(routeID);	// add the route to the station entry
			}
		} catch (NumberFormatException nfe) {
			// if we are here, Integer.valueOf parsing failed
			throw new InvalidRouteException("Error parsing route or station IDs.");
		}
	}

}
