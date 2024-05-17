package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import model.dao.CandyDAO;

@Path("candy/")
public class CandyService {
	private static final  String RESOURCE_PATH="resources";

	private CandyDAO candyDAO;
	private UserSessionManager sessionManager;
	public CandyService() {
		Properties properties = new Properties();
		this.sessionManager = UserSessionManager.getInstance();
		try {
			properties.load(new FileInputStream(RESOURCE_PATH+File.separator+"properties.config"));
			this.candyDAO = new CandyDAO(properties.getProperty("REDIS_HOST"), Integer.valueOf(properties.getProperty("REDIS_PORT")));
		} catch (IOException e) {
			Logger logger = Logger.getLogger("Factory app Logger");
	    	try {
	    		logger.addHandler(new FileHandler("Main.log"));
	    		logger.log(Level.WARNING, "Failed to intilize the Candy Data accsess object.",e);
	    	} catch (SecurityException | IOException einner) {
	    		logger.log(Level.WARNING, "Failed to add handler.",einner);
	    		
	    	}
			
		} 
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{key}/{username}")
	public Response getProducts(@PathParam("key") String key,@PathParam("username") String username) 
	{
		if(this.sessionManager.checkUserSession(key, username))
			return Response.ok().entity(this.candyDAO.getAll()).build();
		return Response.status(Status.FORBIDDEN).build();
		
	}
}