package main;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Candy;
import model.dao.CandyDAO;
import model.datachange.IDateChangedSubscriberCandy;

public class CandyViewController implements Initializable, IDateChangedSubscriberCandy {
	private CandyDAO dao;
	@FXML
	private TableView<Candy> candyTV;
	@FXML
	private TableColumn<Candy, String> candyName;
	@FXML
	private TableColumn<Candy, Double> candyQuantity;
	@FXML
	private TableColumn<Candy, Double> candyPrice;
	@FXML
	private Label infoLabel;
	@FXML
	private TextField nameField;
	@FXML
	private TextField quantityField;
	@FXML
	private TextField priceField;
	
	private Logger logger;

	public CandyViewController() {
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
	}

	public void remove() {

		Candy item = this.candyTV.getSelectionModel().getSelectedItem();
		if (item == null) {
			this.infoLabel.setText("No item selected!");
			return;
		}

		this.dao.delete(item);
	}

	public void add() {
		String name = this.nameField.getText();
		if (name == null || name.isEmpty() || name.isBlank())
			this.infoLabel.setText("Name CANNOT BE EMPTY!");
		String priceStr = this.priceField.getText();
		double price = 0;
		try {
			price = Double.valueOf(priceStr);
		} catch (Exception e) {
			this.infoLabel.setText("Price must be a number!");
			logger.log(Level.INFO,"Parsing text values exception",e);
			return;
		}
		String quantityStr = this.quantityField.getText();
		double quantity = 0;
		try {
			quantity = Double.valueOf(quantityStr);
		} catch (Exception e) {
			this.infoLabel.setText("Quantity must be a number!");
			logger.log(Level.INFO,"Parsing text values exception",e);
			return;
		}
		if(quantity<=0||price<=0)
		{
			this.infoLabel.setText("Quantity and Price must be greater than zero!");
			return ;
		}
		Candy temp = new Candy(name, price, quantity);
		dao.set(temp);
		if (dao.contains(temp))
			this.infoLabel.setText("Updated!");
		else

			this.infoLabel.setText("Added!");

	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		candyName.setCellValueFactory(new PropertyValueFactory<Candy, String>("Name"));
		candyPrice.setCellValueFactory(new PropertyValueFactory<Candy, Double>("Price"));
		candyQuantity.setCellValueFactory(new PropertyValueFactory<Candy, Double>("Quantity"));
        this.candyTV.getItems().setAll(this.dao.getAll());
        candyTV.setRowFactory(tv -> {
            TableRow<Candy> row = new TableRow<>();
            
            return row;
        });

	}

	public CandyViewController(CandyDAO dao) {
		this.dao = dao;
		this.dao.subscribe(this);
	}

	@Override
	public void onDataChanged(CandyDAO source) {
		Platform.runLater(()->{
			candyTV.getItems().clear();
			candyTV.getItems().addAll(dao.getAll());
		});

	}

}
