package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.google.gson.Gson;

import model.CandyOrder;
import model.FactoryUser;

public class SecureComunicationService {
	private static final String RESOURCE_PATH = "resources";
	private SSLSocket socket;
	private BufferedReader br;
	private PrintWriter printWriter;
	private Gson gson;
	private Logger logger;
	public SecureComunicationService() {
		try {
			
			logger = Logger.getLogger("OrderApp Logger");
			try {
				logger.addHandler(new FileHandler("Main.log"));
			} catch (SecurityException | IOException einner) {
				logger.log(Level.WARNING, "Failed to add handler.",einner);
				
			} 
			Properties properties = new Properties();
			properties.load(new FileInputStream(RESOURCE_PATH + File.separator + "properties.config"));
			
			 KeyStore truststore = KeyStore.getInstance("JKS");
		     truststore.load(new FileInputStream(properties.getProperty("TRUST_STORE_PATH")), properties.getProperty("TRUST_STORE_PASSWORD").toCharArray());
		     
		     TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		      trustManagerFactory.init(truststore);
		     SSLContext sslContext = SSLContext.getInstance("TLS");

		        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

		        // Create SSLSocketFactory from SSLContext
		        SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

		     
			this.socket = (SSLSocket) sslSocketFactory.createSocket(properties.getProperty("SECURE_HOST"),
					Integer.valueOf(properties.getProperty("SECURE_PORT")));
			this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.printWriter = new PrintWriter(socket.getOutputStream(), true);
			this.gson = new Gson(); 
		} catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException | KeyManagementException e) {
			logger.log(Level.WARNING, "Failed to intilize the Secure communication Service.",e);
		}
	}

	public boolean login(FactoryUser user) {
		try {
			printWriter.println("LOGIN");
			if ("OK".equals(br.readLine())) {
				printWriter.println(gson.toJson(user, FactoryUser.class));
				return "OK".equals(br.readLine());
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to login through the Secure communication Service.",e);
		}
		return false;
	}

	public boolean sendOrder(CandyOrder order, boolean accapted) {
		try {
			if (accapted)
				printWriter.println("CANDYORDER#ACCAPTE");
			else	
				printWriter.println("CANDYORDER#REJECT");
			if ("OK".equals(br.readLine())) {
				printWriter.println(gson.toJson(order, CandyOrder.class));
				return "OK".equals(br.readLine());
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to send order through the Secure communication Service.",e); 
		}
		return false;
	}

	public void close() {
		try {
			br.close();
			printWriter.close();
			socket.close();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to close the Secure communication Service.",e);
		}

	}

}
