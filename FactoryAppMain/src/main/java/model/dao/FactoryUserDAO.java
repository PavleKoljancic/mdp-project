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
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import model.FactoryUser;

public class FactoryUserDAO {
	private static final String RESOURCE_PATH = "resources";
	Gson gson;
	File dataFile;
	ArrayList<FactoryUser> data;
	private Logger logger;
 
	public FactoryUserDAO() throws FileNotFoundException, IOException {
		this.gson = new Gson();
		Properties properties = new Properties();
		properties.load(new FileInputStream(RESOURCE_PATH+File.separator+"properties.config"));
		this.dataFile = new File(properties.getProperty("DATA_SOURCE") +File.separator+ properties.getProperty("FACTORY_USERS"));
		data = new ArrayList<FactoryUser>();
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
		this.data.addAll(Arrays.asList(gson.fromJson(bf.readLine(), FactoryUser[].class)));
		bf.close();
	}

	private void saveToFile() throws IOException {

		this.dataFile.delete();
		this.dataFile.createNewFile();
		PrintWriter writer = new PrintWriter(dataFile);
		writer.println(this.gson.toJson(this.data.toArray(), FactoryUser[].class));
		writer.close();

	}

	public ArrayList<FactoryUser> getAll() {
		return new ArrayList<FactoryUser>(data);
	}

	public boolean create(FactoryUser FactoryUser) {
		synchronized (data) {
			if(this.data.contains(FactoryUser))
				return false;
			
			if (this.data.add(FactoryUser)) {
				try {
					this.saveToFile();
					return true;
				} catch (IOException e) {

					logger.log(Level.WARNING, "Failed to save factory user data to file.",e);
				}

			}
			return false;

		}
		
	}

	public boolean remove(FactoryUser FactoryUser) {
		synchronized (data) {
			if (this.data.remove(FactoryUser)) {
				try {
					this.saveToFile();
					return true;
				} catch (IOException e) {

					logger.log(Level.WARNING, "Failed to save factory user data to file.",e);
				}

			}
			return false;
		}

	}

	public boolean update(FactoryUser factoryUser) {
		synchronized (data) {
			if (this.data.remove(factoryUser) && this.data.add(factoryUser)) {
				try {
					this.saveToFile();
					return true;
				} catch (IOException e) {

					logger.log(Level.WARNING, "Failed to save factory user data to file.",e);
				}

			}
			return false;

		}

	}


	public boolean contains(FactoryUser temp) {
		return this.data.contains(temp);
	}
	public synchronized void reloadFromFile() throws IOException 
	{
		this.data.clear();
		this.loadFromFile();
	}
}
