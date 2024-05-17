package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Properties;
import java.util.concurrent.TimeoutException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import model.CandyOrder;

public class MQSenderService {
	

	private static final  String RESOURCE_PATH="resources";
	private ConnectionFactory factory;
	private String QUEUE_NAME;
	private Logger logger;
	public MQSenderService() 
	{	
		logger = Logger.getLogger("ClientApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException e) {
			logger.log(Level.WARNING, "Failed to add handler.",e);
			
		}
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(RESOURCE_PATH+File.separator+"properties.config"));
			this.factory = new ConnectionFactory();
			factory.setHost(properties.getProperty("MQ_HOST"));
			factory.setPort(Integer.valueOf(properties.getProperty("MQ_PORT")));
			factory.setUsername(properties.getProperty("MQ_USERNAME"));
			factory.setPassword(properties.getProperty("MQ_PASSWORD"));
			this.QUEUE_NAME = properties.getProperty("QUEUE_NAME");
		} catch (IOException e) {
			
			logger.log(Level.WARNING,"Failed to intilize MQ Service", e);
		}

		 
	}
	
	public boolean publishToMessageQueue(CandyOrder order) {

		String xmlEncoded =null;
		try {
			JAXBContext context = JAXBContext.newInstance(CandyOrder.class);
			Marshaller marshaller = context.createMarshaller();

			StringWriter writer = new StringWriter();
			marshaller.marshal(order, writer);
			xmlEncoded = writer.toString();
		} catch (JAXBException e) {
			logger.log(Level.WARNING,"Failed to serialize to XML", e);
		}

		
		if(xmlEncoded!=null)
		try (Connection connection = factory.newConnection(); Channel channel = connection.createChannel()) {
			boolean durable = true; // Ovo sluzi da ako bi se MQ serveru gasio da ostanu idalje poruke
			channel.queueDeclare(QUEUE_NAME, durable, false, false, null);
			channel.basicPublish("", QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, xmlEncoded.getBytes("UTF-8"));
			return true;

		} catch (IOException | TimeoutException e) {
			logger.log(Level.WARNING,"Failed to send message to the MQ", e);
		}

		return false;
	}
	
}
