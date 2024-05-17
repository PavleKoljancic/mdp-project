package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class SecureComunicationServer extends Thread {
	private static final String RESOURCE_PATH = "resources";
	private ServerSocket serverSocket;
	private Logger logger;

	public SecureComunicationServer() {
		Properties properties = new Properties();
		this.setDaemon(true);
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
		
		try {
			properties.load(new FileInputStream(RESOURCE_PATH + File.separator + "properties.config"));
			System.setProperty("javax.net.ssl.keyStore", properties.getProperty("KEY_STORE_PATH"));
			System.setProperty("javax.net.ssl.keyStorePassword", properties.getProperty("KEY_STORE_PASSWORD"));
			SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			this.serverSocket = ssf.createServerSocket(Integer.valueOf(properties.getProperty("SECURE_PORT")));
		} catch (IOException e) {

			logger.log(Level.WARNING, "Failed to intilize the SecureComunicationServer.",e);
		}

	}
	@Override
	public void run() {
		while(true) 
		{
			try {
				SSLSocket sslSocket = (SSLSocket) this.serverSocket.accept();
				SecureComunicationServerThread thread = new SecureComunicationServerThread(sslSocket);
				thread.start();
			
			} catch (IOException e) {
				
				logger.log(Level.WARNING, "Failed to accaepct a secure connection in the main server thread.",e);
			}
		}
	}
}
