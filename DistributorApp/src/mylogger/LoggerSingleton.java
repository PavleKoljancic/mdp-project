package mylogger;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LoggerSingleton {
	private static Logger instance=null;
	public static Logger getInstance() 
	{
		if(instance==null) 
		{
			instance = Logger.getLogger("DistributorRMI Logger");
		        try {
					FileHandler handler = new FileHandler("DistributorRMI.log");
					instance.addHandler(handler);
				} catch (SecurityException | IOException e) {
					instance.log(Level.WARNING,"Exception while making file handler" ,e);					     
				}
		        
		}
		return instance;
	}
}
