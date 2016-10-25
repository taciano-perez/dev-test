package goeuro.bus.direct.core;

/**
 * Exception thrown when parsing an invalid route entry. 
 * @author Taciano Perez
 *
 */
public class InvalidRouteException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidRouteException(String msg) {
		super(msg);
	}

}
