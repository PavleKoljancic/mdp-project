package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import model.Candy;

public class CandyService {
	private static final  String RESOURCE_PATH="resources";

	private Gson gson;
	private URL url;
	private Logger logger;
	public CandyService(String key, String username) {
		logger = Logger.getLogger("ClientApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException e) {
			logger.log(Level.WARNING, "Failed to add handler.",e);
			 
		}
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(RESOURCE_PATH + File.separator + "properties.config"));
			url  = new URL(properties.getProperty("BASE_URL") + "candy/"+key+"/"+username);
			this.gson = new Gson();
		} catch (IOException e) {

			logger.log(Level.WARNING, "Failed to intilize candy service.",e);
		}
	}
	
	public ArrayList<Candy> getAll() 
	{
		ArrayList<Candy> result = new ArrayList<Candy>();
		try {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("Accept", "application/json");
		if(con.getResponseCode()==HttpURLConnection.HTTP_OK)
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
			result.addAll(Arrays.asList(gson.fromJson(br.readLine(), Candy[].class)) );
		}
		con.disconnect();}
		catch (IOException e) {
			logger.log(Level.WARNING, "Candy service get all IOExcpetion.",e);
			
		}
		return result;
		
	}
	
}
