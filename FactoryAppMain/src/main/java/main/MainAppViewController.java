package main;

import java.io.IOException;
import java.net.URL;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mdp.distributor.rmi.DistributorRegisterService;
import model.dao.CandyDAO;
import model.dao.IngredientDAO;

public class MainAppViewController implements Initializable{

	private DistributorRegisterService distributorRegisterService;
	private IngredientDAO ingredientDAO;
	private CandyDAO candyDAO;
	private Registry registry;
	private String BINDING_NAME;

	@FXML
	Tab distributorsTab;
	@FXML
	Tab ingredientsTab;
	@FXML
	Tab candyTab;
	@FXML
	Tab usersTab;
	@FXML
	Tab factroyUsersTab;
	private Logger logger;
	

	public MainAppViewController(Stage parentStage, DistributorRegisterService distributorRegisterService,
			IngredientDAO ingredientDAO,CandyDAO candyDAO, Registry registry, String bINDING_NAME) {

		this.distributorRegisterService = distributorRegisterService;
		this.ingredientDAO = ingredientDAO;
		this.registry = registry;
		this.BINDING_NAME = bINDING_NAME;
		this.candyDAO = candyDAO;
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
		parentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {

				try {
					registry.unbind(BINDING_NAME);
					UnicastRemoteObject.unexportObject(distributorRegisterService, true);
				} catch (RemoteException | NotBoundException e) {
					logger.log(Level.WARNING, "Failed to close RMI service.",e);
				}
				System.exit(0);

			}
		});
	
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource( "DistributorsView.fxml"));
            fxmlLoader.setController(new DistributorsViewController(this.distributorRegisterService,ingredientDAO,registry));
            distributorsTab.setContent(fxmlLoader.load());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load DistributorsView.fxml.",e);
		}
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource( "IngredientView.fxml"));
            fxmlLoader.setController(new IngredientViewController(this.ingredientDAO));
            ingredientsTab.setContent(fxmlLoader.load());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load IngredientView.fxml.",e);
		}
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource( "CandyView.fxml"));
            fxmlLoader.setController(new CandyViewController(this.candyDAO));
            candyTab.setContent(fxmlLoader.load());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load CandyView.fxml.",e);
		}
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource( "FactoryUsersView.fxml"));
            factroyUsersTab.setContent(fxmlLoader.load());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load FactoryUsersView.fxml.",e);
		}
		try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource( "UserView.fxml"));
           usersTab.setContent(fxmlLoader.load());
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load UserView.fxml.",e);
		}
	}

}
