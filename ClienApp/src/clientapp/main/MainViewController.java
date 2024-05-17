package clientapp.main;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
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
import model.CandyOrder;
import service.CandyService;
import service.MQSenderService;
import service.MulticastPromotonListner;
import service.listner.IMulticastPromotonSubscriber;

public class MainViewController implements Initializable, IMulticastPromotonSubscriber {

	private CandyService candyService;
	private HashSet<Candy> orderList;
	private MQSenderService senderService;
	@FXML
	private TableView<Candy> candyTableView;

	@FXML
	private TableColumn<Candy, String> candyNameColumn;
	@FXML
	private TableColumn<Candy, Double> candyQuantityColumn;
	@FXML
	private TableColumn<Candy, Double> candyPriceColumn;

	@FXML
	private TableView<Candy> orderTableView;
	@FXML
	private TableColumn<Candy, String> orderNameColumn;
	@FXML
	private TableColumn<Candy, Double> oderQuantityColumn;
	@FXML
	private TableColumn<Candy, Double> orderPriceColumn;
	@FXML
	private Label infoLabel;
	@FXML
	private TextField quantityTextField;
	@FXML
	private TextField emailTextField;
	@FXML
	private Label promoLabel;
	private Logger logger;

	public MainViewController(CandyService candyService) {
		super();
		logger = Logger.getLogger("ClientApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException einner) {
			logger.log(Level.WARNING, "Failed to add handler.",einner);
			
		}  
		this.candyService = candyService;
		this.orderList = new HashSet<Candy>();
		this.senderService = new MQSenderService();
		MulticastPromotonListner multicastPromotonListner = new MulticastPromotonListner();
		multicastPromotonListner.subsribe(this);
		multicastPromotonListner.start();
	} 

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		candyNameColumn.setCellValueFactory(new PropertyValueFactory<Candy, String>("Name"));
		candyQuantityColumn.setCellValueFactory(new PropertyValueFactory<Candy, Double>("Quantity"));
		candyPriceColumn.setCellValueFactory(new PropertyValueFactory<Candy, Double>("Price"));
		this.candyTableView.getItems().setAll(this.candyService.getAll());
		candyTableView.setRowFactory(tv -> {
			TableRow<Candy> row = new TableRow<>();

			return row;
		});

		orderNameColumn.setCellValueFactory(new PropertyValueFactory<Candy, String>("Name"));
		oderQuantityColumn.setCellValueFactory(new PropertyValueFactory<Candy, Double>("Quantity"));
		orderPriceColumn.setCellValueFactory(new PropertyValueFactory<Candy, Double>("Price"));
		this.orderTableView.getItems().setAll(this.orderList);
		candyTableView.setRowFactory(tv -> {
			TableRow<Candy> row = new TableRow<>();

			return row;
		});

	}

	public void refresh() {
		this.candyTableView.getItems().clear();
		this.candyTableView.getItems().addAll(this.candyService.getAll());
	}

	public void add() {
		Candy item = this.candyTableView.getSelectionModel().getSelectedItem();
		if (item == null) {
			this.infoLabel.setText("No item to add selected.");
			return;
		}
		String quantityString = this.quantityTextField.getText();
		if (quantityString == null || quantityString.isBlank() || quantityString.isEmpty()) {
			this.infoLabel.setText("No quantity set.");
			return;
		}
		double quantity = 0;
		try {
			quantity = Double.valueOf(quantityString);
		} catch (Exception e) {
			this.infoLabel.setText("Quantity must be a number.");
			logger.log(Level.INFO, "Parsing valdition exception quantity text couldn't be convreted to a number.",e);
			return;
		}
		if(quantity<=0) 
		{
			this.infoLabel.setText("Quantity must be a greater than zero.");
			return;
		}
		this.orderList.add(new Candy(item.getName(), item.getPrice(), quantity));
		this.orderTableView.getItems().clear();
		this.orderTableView.getItems().addAll(this.orderList);
	}

	public void remove() {
		Candy item = this.orderTableView.getSelectionModel().getSelectedItem();
		if (item == null) {
			this.infoLabel.setText("No item to remove selected.");
			return;
		}
		this.orderList.remove(item);
		this.orderTableView.getItems().clear();
		this.orderTableView.getItems().addAll(this.orderList);
	}

	public void send() {
		if (this.orderList.isEmpty()) {
			this.infoLabel.setText("Order cannot be empty!");
			return;
		}
		String email = this.emailTextField.getText();
		if (email == null || email.isBlank() || email.isEmpty()) {
			this.infoLabel.setText("No emial set!");
			return;
		}

		if (this.senderService.publishToMessageQueue(new CandyOrder(new ArrayList<Candy>(this.orderList), email))) {
			this.emailTextField.setText("");
			this.orderList.clear();
			this.orderTableView.getItems().clear();
			this.infoLabel.setText("Order sent!");
		} else
			this.infoLabel.setText("Something went wrong while sending the order.");

	}

	@Override
	public void onPromotion(String promotionText) {
		Platform.runLater(() -> {

			this.promoLabel.setText(promotionText);

		});

	}
}
