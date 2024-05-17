package main;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import mdp.distributor.rmi.DistributorResponse;
import mdp.distributor.rmi.IDistributorService;
import model.Ingredient;
import model.dao.IngredientDAO;

public class OrderViewController implements Initializable{
	private Stage orderStage;
	private IngredientDAO ingredientDAO;
	private IDistributorService distirbutorService;
	private ArrayList<Ingredient> orderList;
	private Logger logger;
	@FXML
	private TableView<Ingredient> ingridientTV;
	@FXML
	private TableColumn<Ingredient, String> ingredientName;
	@FXML
	private TableColumn<Ingredient, Double> ingredientPrice;
	@FXML
	private TableColumn<Ingredient, Double> ingredientQuantity;
	
	@FXML
	private TableView<Ingredient> orderIngridientTV;
	@FXML
	private TableColumn<Ingredient, String> orderIngridentName;
	@FXML
	private TableColumn<Ingredient, Double> orderIngridentquantity;
	
	@FXML
	private Label infoLabel;
	@FXML
	private TextField quantityField;
	public OrderViewController(Stage orderStage, IngredientDAO ingredientDAO, IDistributorService distirbutorService) {
		this.orderStage = orderStage;
		this.ingredientDAO = ingredientDAO;
		this.distirbutorService = distirbutorService;
		this.orderList =new ArrayList<Ingredient>();
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
		try {
			ingridientTV.getItems().setAll(distirbutorService.getIngridents());
		} catch (RemoteException e) {
			this.infoLabel.setText("An error occurd while fetching the data.");
			logger.log(Level.WARNING, " RemoteException Failed fetching the data.",e);
		}
		
		orderIngridentName.setCellValueFactory(new PropertyValueFactory<Ingredient, String>("Name"));

		orderIngridentquantity.setCellValueFactory(new PropertyValueFactory<Ingredient, Double>("QuantityAvailable"));
		this.orderIngridientTV.setRowFactory(tv -> {
            TableRow<Ingredient> row = new TableRow<>();
            
            return row;
        });
		this.orderIngridientTV.getItems().addAll(orderList);
	}

	
	public void add() 
	{
		Ingredient item = ingridientTV.getSelectionModel().getSelectedItem();
		if(item==null)
		{
			infoLabel.setText("No ingridient selected!");
			return;
		}
		String quantstr = this.quantityField.getText();
		if(quantstr==null||quantstr.isBlank()||quantstr.isEmpty())
		{
			infoLabel.setText("No quantity provided!");
			return;
		}
		double quantity = 0;
		try {
			quantity =Double.valueOf(quantstr);
		}catch (Exception e) {
			infoLabel.setText("Quantity must be a number!");
			logger.log(Level.INFO, "Parsing value exception.",e);
			return;
		}
		if(quantity<=0)
		{
			this.infoLabel.setText("Quantity must be greater than zero!");
			return ;
		}
		this.orderList.remove(item);
		Ingredient temp = new Ingredient();
		temp.setName(item.getName());
		temp.setQuantityAvailable(quantity);
		this.orderList.add(temp);
		this.orderIngridientTV.getItems().clear();
		this.orderIngridientTV.getItems().addAll(orderList);
	}
	public void send() 
	{
		try {
			DistributorResponse response = this.distirbutorService.RequestOrder(orderList);
			if(response.isSuccess())
			{
				synchronized (this.ingredientDAO) {
					ArrayList<Ingredient> current = this.ingredientDAO.getAll();
					for(Ingredient itemOrdered: orderList)
						if(current.contains(itemOrdered))
							this.ingredientDAO.updateQuantity(itemOrdered.getName(), 
									itemOrdered.getQuantityAvailable()+current.get(current.indexOf(itemOrdered)).getQuantityAvailable());
						else this.ingredientDAO.create(itemOrdered);
					
					
				}
				this.cancel();
			}
			this.infoLabel.setText(response.getMessage());
		} catch (RemoteException e) {
			logger.log(Level.WARNING, " RemoteException Failed to send order.",e);
		}
	}
	public void cancel ()
	{
		this.orderStage.close();
	}
	public void remove ()
	{
		Ingredient item = orderIngridientTV.getSelectionModel().getSelectedItem();
		if(item==null)
		{
			infoLabel.setText("No ingridient selected to remove!");
			return;
		}
		this.orderList.remove(item);
		this.orderIngridientTV.getItems().clear();
		this.orderIngridientTV.getItems().addAll(orderList);
	}



}
