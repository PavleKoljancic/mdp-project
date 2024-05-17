package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import service.listner.IMulticastPromotonSubscriber;

public class MulticastPromotonListner extends Thread {
	private MulticastSocket socket;

	private InetAddress address;
	private static final  String RESOURCE_PATH="resources";
	private HashSet<IMulticastPromotonSubscriber> subscribers;
	private Logger logger;
	public MulticastPromotonListner()  
	{	
		this.setDaemon(true);
		logger = Logger.getLogger("ClientApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException e) {
			logger.log(Level.WARNING, "Failed to add handler.",e);
			
		}
		try {
			this.subscribers =new HashSet<IMulticastPromotonSubscriber>();
		Properties properties = new Properties();
		properties.load(new FileInputStream(RESOURCE_PATH+File.separator+"properties.config"));
		this.socket = new MulticastSocket(Integer.valueOf(properties.getProperty("MULTICAST_PORT")));
		this.address = InetAddress.getByName(properties.getProperty("MULTICAST_ADDRESS"));
		socket.joinGroup(address);
		}
		catch (IOException e) {
			logger.log(Level.WARNING, "Failed to intilize MulticastPromotonListner.",e);
		}
	}
	@Override
	public void run() {
		byte [] buffer = new byte[1024];
		DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
		while(true) 
		{	
			try {
				socket.receive(packet);
				byte[] msg = Arrays.copyOfRange(packet.getData(), 0, packet.getLength());
				String result = new String(msg, StandardCharsets.UTF_8);
				this.informSubscribers(result);
			} catch (IOException e) {
				
				logger.log(Level.WARNING, "Failed to read MulticastPromoton message.",e);
			}
		}
	}
	private void informSubscribers(String result) {
		
		synchronized (this.subscribers) {
			for(IMulticastPromotonSubscriber subscriber : subscribers)
				subscriber.onPromotion(result);
		}
		
	}
	public boolean subsribe(IMulticastPromotonSubscriber subscriber) 
	{
		synchronized (this.subscribers) {
			return this.subscribers.add(subscriber);
		}
	}
	public boolean unsubsribe(IMulticastPromotonSubscriber subscriber) 
	{
		synchronized (this.subscribers) {
			return this.subscribers.remove(subscriber);
		}
	}
}
