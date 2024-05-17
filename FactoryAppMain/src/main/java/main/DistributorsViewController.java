package main;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mdp.distributor.rmi.DistributorRegisterService;
import mdp.distributor.rmi.IConectionSubscriberRMI;
import mdp.distributor.rmi.IDistributorService;
import model.Distributor;
import model.Ingredient;
import model.dao.IngredientDAO;

public class DistributorsViewController implements Initializable,IConectionSubscriberRMI {
	
	private DistributorRegisterService distributorRegisterService;
	private IngredientDAO ingredientDAO;
	private Registry registry;
	
	@FXML
	private Label infoLabel;
	@FXML
	private Label distributorLabel;
	@FXML
	private TableView<Distributor> distributorTV;
	@FXML
	private TableView<Ingredient> ingridientTV;
	@FXML
	private TableColumn<Distributor, String> idNameColumn;
	@FXML
	private TableColumn<Distributor, Boolean> connectedColumnt;
	@FXML
	private TableColumn<Ingredient, String> ingredientName;
	@FXML
	private TableColumn<Ingredient, Double> ingredientPrice;
	@FXML
	private TableColumn<Ingredient, Double> ingredientQuantity;
	private Logger logger;
	
	public DistributorsViewController(DistributorRegisterService distributorRegisterService,
			IngredientDAO ingredientDAO, Registry registry) {
		this.distributorRegisterService = distributorRegisterService;
		this.ingredientDAO = ingredientDAO;
		this.registry = registry;
		this.distributorRegisterService.subscribe(this);
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		ingredientName.setCellValueFactory(new PropertyValueFactory<Ingredient, String>("Name"));
		ingredientPrice.setCellValueFactory(new PropertyValueFactory<Ingredient, Double>("PricePerQuantity"));
		ingredientQuantity.setCellValueFactory(new PropertyValueFactory<Ingredient, Double>("QuantityAvailable"));
		ingridientTV.setRowFactory(tv -> {
            TableRow<Ingredient> row = new TableRow<>();
            
            return row;
        });
		idNameColumn.setCellValueFactory(new PropertyValueFactory<Distributor, String>("IdentificationName"));
		connectedColumnt.setCellValueFactory(new PropertyValueFactory<Distributor, Boolean>("Connected"));
		distributorTV.getItems().setAll(distributorRegisterService.getDistributors());
		distributorTV.setRowFactory( tv-> 
		{
			TableRow<Distributor> row = new TableRow<Distributor>();
			return row;
		});
		
		distributorLabel.setText("No Distributor selected.");
		
		
	}
	@Override
	public void disconnected(DistributorRegisterService s) {
		distributorTV.getItems().clear();
		distributorTV.getItems().addAll(distributorRegisterService.getDistributors());
		
	}
	@Override
	public void connected(DistributorRegisterService s) {
		distributorTV.getItems().clear();
		distributorTV.getItems().addAll(distributorRegisterService.getDistributors());
		
	}
	@Override
	public void register(DistributorRegisterService s) {
		distributorTV.getItems().clear();
		distributorTV.getItems().addAll(distributorRegisterService.getDistributors());
		
	}
	
	public void select() 
	{
		Distributor item = this.distributorTV.getSelectionModel().getSelectedItem();
		this.ingridientTV.getItems().clear();
		if(item==null) 
		{
			this.infoLabel.setText("No distributor selected!");
			return;
		}
		distributorLabel.setText(item.getIdentificationName());
		if(!item.isConnected())
		{
			this.infoLabel.setText("The distributor isn't connected right now.");
			return;
		}
			
		try {
			IDistributorService distirbutorService =  (IDistributorService) registry.lookup(item.getIdentificationName());
			
			this.ingridientTV.getItems().addAll(distirbutorService.getIngridents());
			
		} catch (RemoteException | NotBoundException e) {
			
			this.logger.log(Level.WARNING,"Failed to connect to the distributor Remote exception",e);
		}
	}
	
	public void startOrder() 
	{
		Distributor item = this.distributorTV.getSelectionModel().getSelectedItem();
		if(item==null) 
		{
			this.infoLabel.setText("No distributor selected!");
			return;
		}
		if(!item.isConnected())
		{
			this.infoLabel.setText("The distributor isn't connected right now.");
			return;
		}
		try {
			IDistributorService distirbutorService =  (IDistributorService) registry.lookup(item.getIdentificationName());
			Stage orderStage = new Stage();
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OrderView.fxml"));
	        fxmlLoader.setController(new OrderViewController(orderStage,ingredientDAO,distirbutorService));
			Scene scene = new Scene(fxmlLoader.load());
			orderStage.setScene(scene);
			orderStage.show();
			
			
		} catch (IOException | NotBoundException e) {
			
			this.logger.log(Level.WARNING,"Failed to connect to the distributor Remote exception",e);
		}
	}

}
