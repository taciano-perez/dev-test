package goeuro.bus.direct;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
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
	 * Path of the file containing the bus route/station data.
	 */
	private static final String DATA_FILE = "d://temp//testfile.txt"; 
	
	static {	// load file data into singleton 
		System.out.println("Parsing data file");
		data = new BusRouteData();
		// parse file
		data.parseBusRouteData(DATA_FILE);
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
    public String getJson( @QueryParam("dep_sid") String dep_sid, @QueryParam("arr_sid") String arr_sid) {
    	// get answer from data
    	Integer depSidInt, arrSidInt;
    	boolean hasDirectConnection = false;
    	try {
	    	depSidInt = Integer.valueOf(dep_sid);
	    	arrSidInt = Integer.valueOf(arr_sid);
	    	hasDirectConnection = data.hasConnection(depSidInt, arrSidInt);
    	} catch (NumberFormatException nfe) {
    		System.err.println("Error parsing input arguments on DirectRESTService.");
    	}
    	System.out.println("dep="+dep_sid+" arr="+arr_sid);
    	System.out.println("direct_route=" + hasDirectConnection);
    	
    	// ensure we don't throw an exception if an the input parameter is missing
    	if (dep_sid == null) dep_sid = "null";
    	if (arr_sid == null) arr_sid = "null";
    	
    	// build a JSON response
    	JsonObjectBuilder responseBuilder = Json.createObjectBuilder();
    	responseBuilder.add("dep_sid", dep_sid);
    	responseBuilder.add("arr_sid", arr_sid);
    	responseBuilder.add("direct_bus_route", hasDirectConnection);
    	JsonObject response = responseBuilder.build();
    	
    	return response.toString();
    }

}
