package goeuro.bus.direct.service;

import goeuro.bus.direct.core.BusRouteData;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * <p>Bus Direct Route REST Service</p> 
 * <p>This service indicates whether two stations are served by a direct bus line or not.</p>
 * <p>Usage: http://[hostname]:[port]/api/direct?dep_sid={}&amp;arr_sid={}</p>
 * <p>The parameters dep_sid (departure) and arr_sid (arrival) are two Station IDs (SID) represented by integers.</p>
 * <p>The response is a single JSON Object:</p>
 * <code>
 * {
 *   "$schema": "http://json-schema.org/draft-04/schema#",
 *   "type": "object",
 *   "properties": {
 *     "dep_sid": {
 *       "type": "integer"
 *     },
 *     "arr_sid": {
 *       "type": "integer"
 *     },
 *     "direct_bus_route": {
 *       "type": "boolean"
 *     }
 *   },
 *   "required": [
 *     "dep_sid",
 *     "arr_sid",
 *     "direct_bus_route"
 *   ]
 * }
 * </code>
 * @author Taciano Perez
 */
@Path(value="direct")
public class DirectRESTService {
	
    /**
     * Singleton of BusRouteData who does the actual search for bus routes.
     */
    private static BusRouteData data;

    /**
     * Set the path of the file containing the bus route/station data, 
     * and parse the file.
     */
    public static void setDataFile(String filePath) { 
        System.out.println("Parsing data file");
        data = new BusRouteData();
        // parse file
        data.parseBusRouteData(filePath);
    }
	
    /**
     * Default constructor. 
     */
    public DirectRESTService() {
    }

    /**
     * HTTP GET method receiving departure and arrival SIDs and returning the JSON response object
     * @param dep_sid Departure SID
     * @param arr_sid Arrival SID
     * @return JSON Object echoing dep_sid, arr_sid and returning the boolean direct_bus_route
     */
    @GET
    @Consumes( {MediaType.TEXT_PLAIN, MediaType.TEXT_PLAIN})
    @Produces("application/json")
    public DirectRESTServiceResponse getJson( @QueryParam("dep_sid") String dep_sid, @QueryParam("arr_sid") String arr_sid) {
    	// get answer from data
    	Integer depSidInt = null, arrSidInt = null;
    	boolean hasDirectConnection = false;
    	try {
	    	depSidInt = Integer.valueOf(dep_sid);
	    	arrSidInt = Integer.valueOf(arr_sid);
	    	hasDirectConnection = data.hasConnection(depSidInt, arrSidInt);
    	} catch (NumberFormatException nfe) {
    		System.err.println("Error parsing input arguments on DirectRESTService.");
    	}
    	
    	DirectRESTServiceResponse response = new DirectRESTServiceResponse();
    	response.setDep_sid(depSidInt);
    	response.setArr_sid(arrSidInt);
    	response.setDirect_bus_route(hasDirectConnection);
    	return response;
    }

}
