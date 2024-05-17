package main;

import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mdp.distributor.rmi.DistributorService;
import mdp.distributor.rmi.IDistributorRegister;
import model.IDataChangedSubscriber;
import model.Ingredient;
import model.IngredientDAO;
import mylogger.LoggerSingleton;

public class MainViewController  implements Initializable,IDataChangedSubscriber {
	
	IngredientDAO dao;
	Registry registry;
	Stage parentStage;
	IDistributorRegister distributorRegister;
	String IdName;
	DistributorService service;
	public MainViewController(IngredientDAO dao, Registry registry, Stage parentStage,
			IDistributorRegister distributorRegister, String idName,DistributorService service) {
		super();
		this.dao = dao;
		dao.subscribe(this);
		this.registry = registry;
		this.parentStage = parentStage;
		this.distributorRegister = distributorRegister;
		IdName = idName;
		this.service = service;
		final MainViewController temp = this;
		this.parentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		
			@Override
			public void handle(WindowEvent arg0) {
				try {
					dao.unsubscribe(temp);
					distributorRegister.disconnect(idName);
					registry.unbind(IdName);
					UnicastRemoteObject.unexportObject(service, true);
				} catch (RemoteException | NotBoundException e) {
					
					LoggerSingleton.getInstance().log(Level.WARNING,"Error while disconecting distributor RMI." ,e);
				}

		        System.exit(0);
		        
				
			}
		});
	}

	@FXML
	TableView<Ingredient> tableView;
	@FXML
    private TableColumn<Ingredient, String> nameColumn;
    @FXML
    private TableColumn<Ingredient, Double> priceColumn;
    @FXML
    private TableColumn<Ingredient, Double> quantityColumn;
	
    @FXML 
    private TextField nameField;

    @FXML 
    private TextField quantityField;

    @FXML 
    private TextField priceField;
    
    @FXML
    private Label infoLabel;
    
    @Override
	public void initialize(URL arg0, ResourceBundle arg1) {

        nameColumn.setCellValueFactory(new PropertyValueFactory<Ingredient, String>("Name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<Ingredient, Double>("PricePerQuantity"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<Ingredient, Double>("QuantityAvailable"));
        this.tableView.getItems().setAll(this.dao.getAll());
        tableView.setRowFactory(tv -> {
            TableRow<Ingredient> row = new TableRow<>();
            
            return row;
        });
		
	}

	public void remove() {
		
		Ingredient item = this.tableView.getSelectionModel().getSelectedItem();
		if(item==null) 
		{
			this.infoLabel.setText("No item selected!");
			return;
		}
			
		this.dao.remove(item);
	}
	
	public void add() {
		String name = this.nameField.getText();
		if(name==null||name.isEmpty()||name.isBlank())
			this.infoLabel.setText("Name CANNOT BE EMPTY!");
		String priceStr = this.priceField.getText();
		double price =0;
		try {
			price = Double.valueOf(priceStr);
		}
		catch(Exception e) 
		{
			this.infoLabel.setText("Price must be a number!");
			LoggerSingleton.getInstance().log(Level.INFO,"Wrong format of field." ,e);
			return ;
		}
		if(price<=0)
		{
			this.infoLabel.setText("Price must be greater than zero!");
			return ;
		}
		String quantityStr = this.quantityField.getText();
		double quantity =0;
		try {
			quantity = Double.valueOf(quantityStr);
		}
		catch(Exception e) 
		{
			this.infoLabel.setText("Quantity must be a number!");
			LoggerSingleton.getInstance().log(Level.INFO,"Wrong format of field." ,e);
			return ;
		}
		if(quantity<=0)
		{
			this.infoLabel.setText("Quantity must be greater than zero!");
			return ;
		}
		Ingredient temp = new Ingredient(name, quantity, price);
		if(dao.contains(temp))
		{
			if(dao.update(temp))
				this.infoLabel.setText("Updated!");
			else this.infoLabel.setText("Ingredient couldn't be updated.");
		}
		else 
		{
			if(dao.create(temp))
				this.infoLabel.setText("Added!");
			else this.infoLabel.setText("Ingredient couldn't be added.");
		}
		
	}

	@Override
	public void onDataChanged(IngredientDAO source) {
		
		Platform.runLater(()->{
			tableView.getItems().clear();
			tableView.getItems().addAll(dao.getAll());
		});
		
	}

}
