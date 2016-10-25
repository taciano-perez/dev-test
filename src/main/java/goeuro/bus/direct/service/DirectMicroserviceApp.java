package goeuro.bus.direct.service;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Application running DirectRESTService as a microservice,
 * using embedded Jetty.
 * Takes the path/name of the data file as a mandatory argument.
 * @author Taciano Perez
 */
public class DirectMicroserviceApp {

	/**
	 * Main method
	 * @param args path/name of the data file (mandatory argument)
	 */
	public static void main(String[] args) {
		
		// check for argument (data file path)
		if (args.length < 1) {
			System.err.println("Fatal error: data file path must be provided as first application argument.");
			System.exit(-1);
		}
		// parse data file
		DirectRESTService.setDataFile(args[0]);

		// set context path to '/api'
		ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
		context.setContextPath("/api");

		// create Jetty server
		Server jettyServer = new Server(8088);
		jettyServer.setHandler(context);
		ServletHolder jerseyServlet = context.addServlet(
		     org.glassfish.jersey.servlet.ServletContainer.class, "/*");
		jerseyServlet.setInitOrder(0);
		// register Jackson
		jerseyServlet.setInitParameter(org.glassfish.jersey.server.ServerProperties.PROVIDER_PACKAGES,
			"com.fasterxml.jackson.jaxrs.json;"
			+ "jersey.jetty.embedded");

		// load our REST service
		jerseyServlet.setInitParameter(
		   "jersey.config.server.provider.classnames",
		   DirectRESTService.class.getCanonicalName());

		// start Jetty server
		try {
		    jettyServer.start();
		    jettyServer.join();
		} catch (Exception e) {
			System.err.println("Error starting embedded Jetty server: " + e);
		} finally {
		    jettyServer.destroy();
		}
	}

}
