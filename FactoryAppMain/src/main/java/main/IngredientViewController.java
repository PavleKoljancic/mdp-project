package main;

import java.io.IOException;
import java.net.URL;

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

import model.datachange.IDateChangedSubscriberIngridients;
import model.Ingredient;
import model.dao.IngredientDAO;

public class IngredientViewController implements Initializable, IDateChangedSubscriberIngridients{
	public IngredientViewController(IngredientDAO ingredientDAO) {
		this.ingredientDAO = ingredientDAO;
		this.ingredientDAO.subscribe(this);
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
	}
	private IngredientDAO ingredientDAO;
	@FXML
	private TableView<Ingredient> ingridientTV;
	@FXML
	private TableColumn<Ingredient, String> ingredientName;
	@FXML
	private TableColumn<Ingredient, Double> ingredientQuantity;
	@FXML
	private Label infoLabel;
	@FXML
	private TextField nameField;
	@FXML
	private TextField  quantityField;
	private Logger logger;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		ingredientName.setCellValueFactory(new PropertyValueFactory<Ingredient, String>("Name"));
		
		ingredientQuantity.setCellValueFactory(new PropertyValueFactory<Ingredient, Double>("QuantityAvailable"));
		ingridientTV.setRowFactory(tv -> {
            TableRow<Ingredient> row = new TableRow<>();
            
            return row;
        });
		this.ingridientTV.getItems().setAll(this.ingredientDAO.getAll());
		
	}
	@Override
	public void onDataChanged(IngredientDAO source) {
		this.ingridientTV.getItems().clear();
		this.ingridientTV.getItems().setAll(this.ingredientDAO.getAll());
		
	}
	
	public void remove() {
		
		Ingredient item = this.ingridientTV.getSelectionModel().getSelectedItem();
		if(item==null) 
		{
			this.infoLabel.setText("No item selected!");
			return;
		}
			
		this.ingredientDAO.delete(item.getName());
	}
	
	public void add() {
		String name = this.nameField.getText();
		if(name==null||name.isEmpty()||name.isBlank())
			this.infoLabel.setText("Name CANNOT BE EMPTY!");
		
		String quantityStr = this.quantityField.getText();
		double quantity =0;
		try {
			quantity = Double.valueOf(quantityStr);
		}
		catch(Exception e) 
		{
			this.infoLabel.setText("Quantity must be a number!");
			logger.log(Level.INFO, "Format exception while parsing double.",e);
			return ;
		}
		if(quantity<0)
		{
			this.infoLabel.setText("Quantity must be nonnegative!");
			return ;
		}
		Ingredient temp = new Ingredient();
		temp.setName(name);
		temp.setQuantityAvailable(quantity);
		if(ingredientDAO.contains(temp.getName()))
		{
			ingredientDAO.delete(temp.getName());
			ingredientDAO.create(temp);
			this.infoLabel.setText("Updated!");
			
		}
		else 
		{

				ingredientDAO.create(temp);
				this.infoLabel.setText("Added!");

		}
		
}
	
}
