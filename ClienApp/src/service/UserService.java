package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import model.RegistrationRequest;
import model.User;

public class UserService {
	private static final String RESOURCE_PATH = "resources";
	private String BASE_URL;
	private Gson gson;
	private Logger logger;

	public UserService() {
		logger = Logger.getLogger("ClientApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException e) {
			logger.log(Level.WARNING, "Failed to add handler.",e);
			 
		}
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(RESOURCE_PATH + File.separator + "properties.config"));
			this.BASE_URL = properties.getProperty("BASE_URL") + "users/";
			this.gson = new Gson();
		} catch (IOException e) {

			logger.log(Level.WARNING, "Failed to intilize userservice.",e);
		}
	}

	public HttpURLConnection register(RegistrationRequest request) {

		try {
			URL url = new URL(BASE_URL + "register");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			PrintWriter pw = new PrintWriter(con.getOutputStream(), true);
			pw.println(gson.toJson(request, RegistrationRequest.class));
			
			return con; 
			
		} catch (IOException e) {

			logger.log(Level.WARNING, "Failed to register user.",e);
		}

		return null;

	}

	public HttpURLConnection login(User user)  {
		try {
			URL url = new URL(BASE_URL + "login");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setDoOutput(true);
			PrintWriter pw = new PrintWriter(con.getOutputStream(), true);
			pw.println(gson.toJson(user, User.class));
			return con;
		} catch (IOException e) {

			logger.log(Level.WARNING, "Failed to login user.",e);
		}

		return null;
	}

	public int logout(String key, String username)  {
		try {
			URL url = new URL(BASE_URL + "logout/" + key + "/" + username);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("PUT");
			return con.getResponseCode();
			
		} catch (IOException e) {

			logger.log(Level.WARNING, "Failed to logout user.",e);
		}

		return 0;
	}
}
