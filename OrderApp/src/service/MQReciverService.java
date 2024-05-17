package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.ConnectionFactory;

public class MQReciverService {
	private static final  String RESOURCE_PATH="resources";
	private Channel channel;
	private Connection connection;
	private Logger logger;
	public MQReciverService(DeliverCallback deliverCallback) {
		Properties properties = new Properties();
		logger = Logger.getLogger("OrderApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException einner) {
			logger.log(Level.WARNING, "Failed to add handler.",einner);
			
		} 
		
		try {
			properties.load(new FileInputStream(RESOURCE_PATH+File.separator+"properties.config"));
			ConnectionFactory factory = new ConnectionFactory();
			factory.setHost(properties.getProperty("MQ_HOST"));
			factory.setPort(Integer.valueOf(properties.getProperty("MQ_PORT")));
			factory.setUsername(properties.getProperty("MQ_USERNAME"));
			factory.setPassword(properties.getProperty("MQ_PASSWORD"));
			String QUEUE_NAME = properties.getProperty("QUEUE_NAME");
			 connection = factory.newConnection();
			this.channel = connection.createChannel();
			boolean durable = true;
	        int prefetchCount = 1;
	        //Ovo sluzi da bi uzimao samo jednu po jednu poruku
			channel.basicQos(prefetchCount);
	        channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
	        //AutoAcknowlegdment false sluzi da MQ server nebi obrisao poruku cim je posalje 
	        //Nego da mora da dobije odgovor od primalca pa tek onda da je obrise
	        boolean AutoAcknowlgdment =false;
	        channel.basicConsume(QUEUE_NAME, AutoAcknowlgdment, deliverCallback, consumerTag -> { });  	
		} catch (IOException | TimeoutException e) {
			
			logger.log(Level.WARNING, "Failed to intilize the MQ REciver Service.",e);
		}
	}
	
	public void sendBasicAcknowledgement(Delivery delivery) 
	{
		try {
			channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to send basick acknowlegdment to the MQ.",e);
		}
	} 
	public void close() 
	{
		try {
			
			this.channel.close();
			this.connection.close();
		} catch (IOException | TimeoutException e) {
			logger.log(Level.WARNING, "Failed to close the MQ reciver service.",e);
		}
	} 
}
