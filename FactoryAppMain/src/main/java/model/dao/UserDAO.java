package model.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import model.FactoryUser;
import model.User;


public class UserDAO {
	private static final String RESOURCE_PATH = "resources";
	Gson gson;
	File dataFile;
	ArrayList<User> data;
	private Logger logger;
	
	public UserDAO() throws FileNotFoundException, IOException {
		this.gson = new Gson();
		Properties properties = new Properties();
		properties.load(new FileInputStream(RESOURCE_PATH+File.separator+"properties.config"));
		this.dataFile = new File(properties.getProperty("DATA_SOURCE") +File.separator+ properties.getProperty("USERS"));
		data = new ArrayList<User>();
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
		if (!dataFile.exists())

			this.saveToFile();

		else
			this.loadFromFile();
	}
	
	private void loadFromFile() throws  IOException {

		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
		this.data.addAll(Arrays.asList(gson.fromJson(bf.readLine(), User[].class)));
		bf.close();
	}

	private void saveToFile() throws IOException {

		this.dataFile.delete();
		this.dataFile.createNewFile();
		PrintWriter writer = new PrintWriter(dataFile);
		writer.println(this.gson.toJson(this.data.toArray(), User[].class));
		writer.close();

	}
	
	public boolean create(User user) {
		synchronized (data) {
			if(this.data.contains(user))
				return false;
			
			if (this.data.add(user)) {
				try {
					this.saveToFile();
					return true;
				} catch (IOException e) {

					logger.log(Level.WARNING, "Failed to save user data to file.",e);
				}

			}
			return false;

		}
		
	}

	public boolean remove(User user) {
		synchronized (data) {
			if (this.data.remove(user)) {
				try {
					this.saveToFile();
					return true;
				} catch (IOException e) {

					logger.log(Level.WARNING, "Failed to save user data to file.",e);
				}

			}
			return false;
		}

	}

	public boolean update(User user) {
		synchronized (data) {
			if (this.data.remove(user) && this.data.add(user)) {
				try {
					this.saveToFile();
					return true;
				} catch (IOException e) {

					logger.log(Level.WARNING, "Failed to save user data to file.",e);
				}

			}
			return false;

		}

	}


	public boolean contains(User user) {
		return this.data.contains(user);
	}
	public Optional<User> getByUsername(String username) {
		return this.data.stream().filter((u)->{return u.getUsername().equals(username);}).findFirst();
	}

	public ArrayList<User> getAll() {
		
		return this.data;
	}
	
	public synchronized void reloadFromFile() throws IOException 
	{
		this.data.clear();
		this.loadFromFile();
	}
}
