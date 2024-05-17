package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticastPromotionService {
	private static final  String RESOURCE_PATH="resources";
	private MulticastSocket socket;
	private int recivePort;
	InetAddress address;
	private Logger logger;
	public MulticastPromotionService()  
	{	
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
		try {
		Properties properties = new Properties();
		properties.load(new FileInputStream(RESOURCE_PATH+File.separator+"properties.config"));
		this.socket = new MulticastSocket();
		this.recivePort = Integer.valueOf(properties.getProperty("MULTICAST_PORT"));
		this.address = InetAddress.getByName(properties.getProperty("MULTICAST_ADDRESS"));
		socket.joinGroup(address);
		}
		catch (IOException e) {
			logger.log(Level.WARNING, "Failed to intilize the multicast promotion service....",e);
		}
	}
	
	public void sendPromtion(String promoText) 
	{	
		byte [] data = promoText.getBytes(StandardCharsets.UTF_8 );
		try {
			socket.send(new DatagramPacket(data, data.length, address, recivePort));
		} catch (IOException e) {
			
			logger.log(Level.WARNING, "Failed to send promotion.",e);
		}
		
	}
}
