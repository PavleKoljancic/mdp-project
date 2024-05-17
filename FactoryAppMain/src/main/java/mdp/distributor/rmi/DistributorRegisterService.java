package mdp.distributor.rmi;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Distributor;

public class DistributorRegisterService implements IDistributorRegister{
	
	private static final String RESOURCES_PATH="resources";

	private static DistributorRegisterService instnace=null;
	
	public static DistributorRegisterService getInstance() throws FileNotFoundException, IOException 
	{
		if(instnace==null)
			instnace = new DistributorRegisterService();
		return instnace;
	}
	
	private DistributorRegisterService() throws FileNotFoundException, IOException {
		Properties properties = new Properties();
		properties.load(new FileInputStream(RESOURCES_PATH+File.separator+"properties.config"));
		this.dataFile = new File(properties.getProperty("DATA_SOURCE")+File.separator+properties.getProperty("DISTRIBUTORS_FILE"));
		if(!dataFile.exists())
			this.dataFile.createNewFile();
		this.distributors = new ArrayList<Distributor>();
		Files.readAllLines(Paths.get(properties.getProperty("DATA_SOURCE")+File.separator+properties.getProperty("DISTRIBUTORS_FILE"))).stream().forEach((s)->this.distributors.add(new Distributor(s,false)));

		this.subscribers = new HashSet<IConectionSubscriberRMI>();
	}

	ArrayList<Distributor> distributors;

	HashSet<IConectionSubscriberRMI> subscribers;
	File dataFile;
	
	@Override
	public synchronized DistributorResponse register(String identificationName) throws RemoteException {
		Distributor temp = new Distributor(identificationName,false);
		if(this.distributors.contains(temp))
			return new DistributorResponse(false, "Identification name taken.");
		if(!this.saveToFile(identificationName))
			return new DistributorResponse(false, "An uknown error occurd.");
		this.distributors.add(temp);
		informSubscribersRegistered();
		return new DistributorResponse(true, "You have sucsesfuly registered.");
	}

	private boolean saveToFile(String identificationName)  {
		

		try (PrintWriter writer = new PrintWriter(new FileOutputStream(dataFile,true))){
			
			writer.println(identificationName);
			writer.close();
		} catch (FileNotFoundException e) {
			
			Logger logger = Logger.getLogger("Factory app Logger");
	    	try {
	    		logger.addHandler(new FileHandler("Main.log"));
	    	} catch (SecurityException | IOException einner) {
	    		logger.log(Level.WARNING, "Failed to add handler.",einner);
	    		
	    	}
			logger.log(Level.WARNING, "Failed to add handler.",e);
			return false;
		}
	


		return true;
	}

	@Override
	public  DistributorResponse connect(String identificationName) throws RemoteException {
		synchronized (this.distributors) {
			Distributor temp = new Distributor(identificationName,false);
			if(!this.distributors.contains(temp))
				return new DistributorResponse(false, "You are not registred.");
			this.distributors.get(distributors.indexOf(temp)).setConnected(true);;
			this.informSubscribersConnect();
			return new DistributorResponse(true, "You have been connected.");
			
		}
	}

	public ArrayList<Distributor> getDistributors() {
		return distributors;
	}

	@Override
	public DistributorResponse disconnect(String identificationName) throws RemoteException {
		synchronized (this.distributors) {
			Distributor temp = new Distributor(identificationName,false);
			if(!this.distributors.contains(temp))
				return new DistributorResponse(false, "You are not registred.");
			this.distributors.get(distributors.indexOf(temp)).setConnected(false);;
			this.informSubscribersDisconnect();
			return new DistributorResponse(true, "You have been disconnected.");
			
		}
	}
	
	public boolean  subscribe(IConectionSubscriberRMI subscriberRMI) 
	{
		synchronized (this.subscribers) {
			return this.subscribers.add(subscriberRMI);
		}
			
		
	}
	public boolean  unsubscribe(IConectionSubscriberRMI subscriberRMI) 
	{
		synchronized (this.subscribers) {
			return this.subscribers.remove(subscriberRMI);
		}
			
		
	}
	
	private void informSubscribersConnect() 
	{
		synchronized (this.subscribers) {

			for(IConectionSubscriberRMI subscriber : this.subscribers)
				subscriber.connected(this);
			
			
		}
	}
	private void informSubscribersDisconnect() 
	{
		synchronized (this.subscribers) {

			for(IConectionSubscriberRMI subscriber : this.subscribers)
				subscriber.disconnected(this);
			
			
		}
	}
	private void informSubscribersRegistered() 
	{
		synchronized (this.subscribers) {

			for(IConectionSubscriberRMI subscriber : this.subscribers)
				subscriber.register(this);
			
			
		}
	}
	

	
}
