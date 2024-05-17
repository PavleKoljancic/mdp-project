package main;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Properties;
import java.util.logging.Level;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mdp.distributor.rmi.DistributorResponse;
import mdp.distributor.rmi.DistributorService;
import mdp.distributor.rmi.IDistributorRegister;
import mdp.distributor.rmi.IDistributorService;
import model.IngredientDAO;
import mylogger.LoggerSingleton;

public class FirstViewController {

    private static final  String RESOURCE_PATH="resources";
    @FXML
    private Label Infolabel;
    @FXML
    private TextField IdTextField;
    @FXML
    private Button connectButton;
    @FXML
    private Button registerButton;
    
    
    private Registry registry=null;
    private IDistributorRegister distributorRegister=null;
    private String IdName;
    private Stage parentStage;
    private File dataDir;
    public FirstViewController(Stage stage) {
		this.parentStage = stage;
		this.parentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent arg0) {
				System.exit(0);
				
			}
		});
	}

	public void initialize() {
    	
		
		Properties properties = new Properties();
		this.Infolabel.setText("Loading properties...");
		int port =-1;
		String RMIServiceName =null;
		boolean propertiesLoaded = false;
		try {
			properties.load(new FileInputStream(RESOURCE_PATH+File.separator+"properties.config"));
			dataDir = new File(properties.getProperty("DATA_FOLDER"));
			port = Integer.valueOf(properties.getProperty("RMI_PORT"));
			RMIServiceName = properties.getProperty("REGISTER_OBJECT_NAME");
			
			propertiesLoaded=true;
		} catch (IOException e) {
			this.Infolabel.setText("Error loading properties file");
			LoggerSingleton.getInstance().log(Level.WARNING,"Error loading properties file." ,e);	

		}
		if(propertiesLoaded) 
		{	
			this.Infolabel.setText("Connecting to server...");
			
			
			try {
				
				 registry = LocateRegistry.getRegistry(port);

			}  catch ( RemoteException e) {
				this.Infolabel.setText("An error occured while connecting...");
				LoggerSingleton.getInstance().log(Level.WARNING,"An error occured while connecting to the Factory server." ,e);
			}
			
			try {
				 distributorRegister = (IDistributorRegister)registry.lookup(RMIServiceName);
				 enable();
				 
			} catch (RemoteException | NotBoundException e) {
				 
				this.Infolabel.setText("Error locating server");
				LoggerSingleton.getInstance().log(Level.WARNING,"Error locating server." ,e);
			}
			
			
		}
		
		

    }
    
    private void enable() 
    {
    	
   	 this.Infolabel.setText("");
	 this.IdTextField.setDisable(false);
	 this.registerButton.setDisable(false);
	 this.connectButton.setDisable(false);
    }
    
    public void register() 
    {
    	if(getIdNameFromtextField())
    	try {
    		 DistributorResponse response = this.distributorRegister.register(IdName);
			this.Infolabel.setText(response.getMessage());
		} catch (RemoteException e) {
			this.Infolabel.setText("An uknown error occurd.");
			LoggerSingleton.getInstance().log(Level.INFO,"Remote exception." ,e);
		} 
    }
    public void connect() {
    	
    	if(getIdNameFromtextField())
    	try {
    		 DistributorResponse response = this.distributorRegister.connect(IdName);
			this.Infolabel.setText(response.getMessage());
			if(response.isSuccess())
			{
				try {
					IngredientDAO doa = new IngredientDAO(IdName,dataDir );
					DistributorService service = new DistributorService(doa);
					IDistributorService serviceInterface = (IDistributorService) UnicastRemoteObject.exportObject(service,0);
					registry.rebind(IdName, serviceInterface);
					FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
			        fxmlLoader.setController(new MainViewController(doa, registry, parentStage, distributorRegister, IdName, service));
			        Scene scene = new Scene(fxmlLoader.load());
			        this.parentStage.setScene(scene);
			        this.parentStage.show();
				} catch (IOException e) {
					this.Infolabel.setText("An uknown error occurd.");
					LoggerSingleton.getInstance().log(Level.WARNING,"IOEXception." ,e);
				}
			}
		} catch (RemoteException e) {
			this.Infolabel.setText("An uknown error occurd.");
			LoggerSingleton.getInstance().log(Level.WARNING,"Remote exception." ,e);
		} 
    }
    


	private boolean getIdNameFromtextField() {
		this.IdName = this.IdTextField.getText();
    	if(IdName==null|IdName.isEmpty())
    		{this.Infolabel.setText("Identification name cannot be empty.");
    			return false;
    		}
    	return true;
	}
}
