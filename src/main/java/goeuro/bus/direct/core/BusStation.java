package goeuro.bus.direct.core;

import java.util.HashSet;
import java.util.Set;

/**
 * A bus station. It has an ID and a set of bus routes that connect to it.
 * @author Taciano Perez
 *
 */
public class BusStation {
	
	/**
	 * Station ID.
	 */
	private int stationID;

	/**
	 * Set of routes connecting to this station.
	 */
	private Set<Integer> routeIDs = new HashSet<Integer>();

	/**
	 * Constructs a new BusStation.
	 * @param id the station id
	 */
	public BusStation(Integer id) {
		this.stationID = id;
	}

	public int getStationID() {
		return stationID;
	}

	public void setStationID(int stationID) {
		this.stationID = stationID;
	}

	public Set<Integer> getRouteIDs() {
		return routeIDs;
	}

	public void addRouteID(Integer routeID) {
		this.routeIDs.add(routeID);
	}

}
