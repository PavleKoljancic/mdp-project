package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import mylogger.LoggerSingleton;

public class IngredientDAO {

	String identificationName;
	Gson gson;
	File dataFile;
	ArrayList<Ingredient> data;
	HashSet<IDataChangedSubscriber> subscribers;

	public IngredientDAO(String identificationName, File DATA_DIR) throws FileNotFoundException, IOException {
		this.identificationName = identificationName;
		this.gson = new Gson();
		this.subscribers = new HashSet<IDataChangedSubscriber>();
		this.dataFile = new File(DATA_DIR + this.identificationName + ".json");
		data = new ArrayList<Ingredient>();
		if (!dataFile.exists())

			this.saveToFile();

		else
			this.loadFromFile();
	}

	private void loadFromFile() throws JsonSyntaxException, IOException {

		BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(dataFile)));
		this.data.addAll(Arrays.asList(gson.fromJson(bf.readLine(), Ingredient[].class)));
		bf.close();

	}

	private void saveToFile() throws IOException {

		this.dataFile.delete();
		this.dataFile.createNewFile();
		PrintWriter writer = new PrintWriter(dataFile);
		writer.println(this.gson.toJson(this.data.toArray(), Ingredient[].class));
		writer.close();

	}

	public ArrayList<Ingredient> getAll() {
		return new ArrayList<Ingredient>(data);
	}

	public boolean create(Ingredient ingredient) {
		synchronized (data) {
			if(this.data.contains(ingredient))
				return false;
			
			if (this.data.add(ingredient)) {
				try {
					this.saveToFile();
					informSubscribers();
					return true;
				} catch (IOException e) {

					LoggerSingleton.getInstance().log(Level.WARNING,"Exception while saving igridients to file." ,e);	
				}

			}
			return false;

		}
		
	}

	public boolean remove(Ingredient ingredient) {
		synchronized (data) {
			if (this.data.remove(ingredient)) {
				try {
					this.saveToFile();
					informSubscribers();
					return true;
				} catch (IOException e) {

					LoggerSingleton.getInstance().log(Level.WARNING,"Exception while saving igridients to file." ,e);	
				}

			}
			return false;
		}

	}

	public boolean update(Ingredient ingredient) {
		synchronized (data) {
			if (this.data.remove(ingredient) & this.data.add(ingredient)) {
				try {
					this.saveToFile();
					informSubscribers();
					return true;
				} catch (IOException e) {
					
					LoggerSingleton.getInstance().log(Level.WARNING,"Exception while saving igridients to file." ,e);	
				}

			}
			return false;

		}

	}

	public boolean subscribe(IDataChangedSubscriber subscriber) {
		synchronized (this.subscribers) {
			return this.subscribers.add(subscriber);
		}
	}

	public boolean unsubscribe(IDataChangedSubscriber subscriber) {
		synchronized (this.subscribers) {
			return this.subscribers.remove(subscriber);
		}
	}

	private void informSubscribers() {
		synchronized (this.subscribers) {
			this.subscribers.stream().forEach((s) -> s.onDataChanged(this));
		}
	}

	public boolean contains(Ingredient temp) {
		return this.data.contains(temp);
	}

}
