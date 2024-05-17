package main;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mdp.distributor.rmi.DistributorRegisterService;
import mdp.distributor.rmi.IDistributorRegister;
import model.dao.CandyDAO;
import model.dao.IngredientDAO;
import service.SecureComunicationServer;

public class FactoryApp extends Application {
	private static final  String RESOURCE_PATH="resources";
	private static Scene scene;
	private static DistributorRegisterService  distributorRegisterService;
	private static IngredientDAO ingredientDAO;
	private static CandyDAO candyDAO;
	private static Registry registry;
	private static  String BINDING_NAME;
	private static Logger logger;
	public static void main(String [] args)
	{	
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
    	try {
		System.setProperty("java.security.policy", RESOURCE_PATH+File.separator+"server_policy.txt" );
		if(System.getSecurityManager()==null)
			System.setSecurityManager(new SecurityManager());
		 Properties properties = new Properties();
		 properties.load(new FileInputStream(RESOURCE_PATH+File.separator+"properties.config"));
		 distributorRegisterService = DistributorRegisterService.getInstance();
		 ingredientDAO = new IngredientDAO(properties.getProperty("REDIS_HOST"), Integer.valueOf(properties.getProperty("REDIS_PORT")));
		 candyDAO  = new CandyDAO(properties.getProperty("REDIS_HOST"), Integer.valueOf(properties.getProperty("REDIS_PORT")));
		 IDistributorRegister service = (IDistributorRegister) UnicastRemoteObject.exportObject(distributorRegisterService,0);
		 registry = LocateRegistry.createRegistry(Integer.valueOf(properties.getProperty("RMI_PORT")));
		 BINDING_NAME = properties.getProperty("BINDING_NAME");
		 registry.rebind(BINDING_NAME, service);
    	}
    	catch (IOException e) {
    		logger.log(Level.WARNING, "Failed to intilize the main factory app.",e);
		}
		 launch(args);
	}
	  
	    @Override
	    public void start(Stage stage) {
	    	
	        try {
				scene = new Scene(this.loadFXML(stage));
				stage.setScene(scene);
		        stage.setTitle("Main Factory App");
		        stage.show();
		        new SecureComunicationServer().start();
			} catch (IOException e) {
				logger.log(Level.WARNING, "Failed to load the main factory app GUI.",e);
			}
	        
	      
	    }

	    private  Parent loadFXML(Stage stage) throws IOException {
	    	
	    	
	        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
	        fxmlLoader.setController(new MainAppViewController(stage,distributorRegisterService,ingredientDAO,candyDAO,registry,BINDING_NAME));
	        return fxmlLoader.load();

	      
	        
	    }

}
