package service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocket;



import com.google.gson.Gson;

import model.Candy;
import model.CandyOrder;
import model.FactoryUser;
import model.dao.FactoryUserDAO;

public class SecureComunicationServerThread extends Thread {
	private SSLSocket sslSocket;
	private BufferedReader br;
	private PrintWriter printWriter;
	private Gson gson;
	private FactoryUserDAO dao;
	private Logger logger;
	public SecureComunicationServerThread(SSLSocket sslSocket) throws IOException {
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
		this.sslSocket = sslSocket;
		this.setDaemon(true);
		this.br = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
		this.printWriter = new PrintWriter(sslSocket.getOutputStream(),true);
		this.gson = new Gson();
		this.dao = new FactoryUserDAO();
	}
	
	@Override
	public void run() {
	
		while(sslSocket.isConnected()&&!sslSocket.isClosed()) 
		{
		
			try {
				String line = br.readLine();
				if(line!=null) {
					String [] cmds = line.split("#");
					if("LOGIN".equals(cmds[0]) ) 
					{
						printWriter.println("OK");
						FactoryUser user = gson.fromJson(br.readLine(), FactoryUser.class);
						dao.reloadFromFile();
						if(dao.getAll().contains(user))
						{
							int index =dao.getAll().indexOf(user);
							FactoryUser real = dao.getAll().get(index);
							if(real.getPassword().equals(user.getPassword()))
								printWriter.println("OK");
							else printWriter.println("NOT OK");
						}
						else printWriter.println("NOT OK");
					}
					
					else if("CANDYORDER".equals(cmds[0])) 
					{ 	boolean accapted="ACCAPTE".equals(cmds[1]);
						printWriter.println("OK");
						CandyOrder order = gson.fromJson(br.readLine(), CandyOrder.class);
						this.writeOrderToFile(order, accapted);
						printWriter.println("OK");
						
					}
					else printWriter.println("NOT OK");
				}
				
			} catch (IOException e) {
				logger.log(Level.WARNING,"IO exception in the secure communications thread",e);
			}
			
		}
		try {
			br.close();
			printWriter.close();
			sslSocket.close();
		} catch (IOException e) {
			logger.log(Level.WARNING,"IO exception in the secure communications thread. Failed to close the opend streams and socket.",e);
		}
	
	}
	
	private void writeOrderToFile(CandyOrder order,boolean accapted) 
	{
		try {
			PrintWriter filewriter = new PrintWriter(new FileOutputStream("./data/orders/"+order.getTimestamp()+".txt"),true);
			filewriter.println("Ordered by:"+order.getEmail());
			filewriter.println("Accapted:"+accapted);
			filewriter.println("Timestamp:"+order.getTimestamp());
			filewriter.println("Products:");
			for(Candy item : order.getOrderItems())
				filewriter.println(item);
			filewriter.close();
			
		} catch (FileNotFoundException e) {
			logger.log(Level.WARNING,"Failed to create the file to write the order in.",e);
		}
	}

}
