package service;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import model.RegistrationRequest;
import model.User;
import model.dao.CandyDAO;
import model.dao.UserDAO;

@Path("users/")
public class UserService {
	UserDAO userDAO;
	private UserSessionManager sessionManager;

	public UserService() {
		sessionManager = UserSessionManager.getInstance();
		Logger logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
		try {
			
			this.userDAO = new UserDAO();
		} catch (IOException e) {
			logger.log(Level.WARNING,"Failed to intilize the User DAO object",e);
		}
	}
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("register")
	public Response register(RegistrationRequest request) 
	{
		if(this.userDAO.getByUsername(request.getUsername()).isPresent())
			return  Response.status(Status.CONFLICT).entity("Username taken.").build();
		if(!request.checkPasswordMatch())
			return  Response.status(Status.CONFLICT).entity("Passwords don't match.").build();
		if (userDAO.create(request.buildUser()))
			return  Response.status(Status.CREATED).entity("Account created").build();
		return  Response.status(Status.INTERNAL_SERVER_ERROR).entity("Unknown error occurd").build();

	}
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("login")
	public Response login(User user) 
	{	
		Optional<User> optinal = userDAO.getByUsername(user.getUsername());
		if(optinal.isEmpty()||(!optinal.get().getPassword().equals(user.getPassword())))
			return  Response.status(Status.UNAUTHORIZED).entity("Wrong password or username").build();
		if(optinal.get().isBlocked())
			return  Response.status(Status.UNAUTHORIZED).entity("Account has been blocked").build();
		if(!optinal.get().isActivated())
			return  Response.status(Status.UNAUTHORIZED).entity("Account not acctivated").build();
		
		String key = this.sessionManager.startUserSession(user);
		if(key!=null)
			return Response.status(Status.OK).entity(key).build();
		return  Response.status(Status.FORBIDDEN).entity("Please logout from your other session").build();
	}
	@PUT
	@Produces(MediaType.TEXT_PLAIN)
	@Path("logout/{key}/{username}")
	public Response logout(@PathParam("key") String key,@PathParam("username") String username) 
	{
		if(this.sessionManager.endUserSession(key, username))
			
			return Response.ok().entity("You have logged out.").build();
			
			
		return Response.status(Status.FORBIDDEN).build();
		
	}

}
