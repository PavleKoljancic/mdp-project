package main;



import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;

import jakarta.xml.bind.Unmarshaller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.Candy;
import model.CandyOrder;
import service.MQReciverService;
import service.MailService;
import service.SecureComunicationService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

public class OrderMainViewController implements DeliverCallback, Initializable {

	public OrderMainViewController(Stage parentStage,SecureComunicationService secureComunicationService) {
		this.semaphore = new Semaphore(1);
		logger = Logger.getLogger("OrderApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException einner) {
			logger.log(Level.WARNING, "Failed to add handler.",einner);
			
		}
		try {
			this.semaphore.acquire();
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "Interupt exception while synchronizing the orders app with the MQ.",e);
		}
		this.currentCandyOrder = null;
		this.currentDelivery = null;
		this.mqReciverService = new MQReciverService(this);
		parentStage.setOnCloseRequest(new javafx.event.EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
				mqReciverService.close(); 
				secureComunicationService.close();

			}
		});
		try {
			this.mailService = new MailService();
		} catch (IOException e) {
			logger.log(Level.WARNING, "Exception intilaizing the mailService.",e);
		}
		this.secureComunicationService = secureComunicationService;
	}

	private MQReciverService mqReciverService;
	private Delivery currentDelivery;
	private CandyOrder currentCandyOrder;
	private Semaphore semaphore;
	private MailService mailService;
	private SecureComunicationService secureComunicationService;
	private Logger logger;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		candyNameColumn.setCellValueFactory(new PropertyValueFactory<Candy, String>("Name"));
		candyQuantityColumn.setCellValueFactory(new PropertyValueFactory<Candy, Double>("Price"));
		candyPriceColumn.setCellValueFactory(new PropertyValueFactory<Candy, Double>("Quantity"));
		candyTableView.setRowFactory(tv -> {
			TableRow<Candy> row = new TableRow<>();

			return row;
		});
		this.setInvisable();
		this.semaphore.release();
	}

	@FXML
	private TableView<Candy> candyTableView;

	@FXML
	private TableColumn<Candy, String> candyNameColumn;
	@FXML
	private TableColumn<Candy, Double> candyQuantityColumn;
	@FXML
	private TableColumn<Candy, Double> candyPriceColumn;
	@FXML
	private Button rejectButton;
	@FXML
	private Button accepteButton;

	@FXML
	private Label infoLabel;
	@FXML
	private Label dateLabel;
	@FXML
	private Label emailLabel;

	@Override
	public void handle(String consumerTag, Delivery delivery) throws IOException {
		this.currentDelivery = delivery;
		String xml = new String(currentDelivery.getBody(), StandardCharsets.UTF_8);
		try {
			currentCandyOrder = fromXML(xml);
		} catch (JAXBException e) {
			currentCandyOrder = null;
			currentDelivery = null;
			mqReciverService.sendBasicAcknowledgement(delivery);
			logger.log(Level.INFO, "Exception unmarshalling the XML order.",e);

		}
		if (!isOrderValid(currentCandyOrder)) {
			currentCandyOrder = null;
			currentDelivery = null;
			mqReciverService.sendBasicAcknowledgement(delivery);
		}
		try {
			this.semaphore.acquire();
			this.semaphore.release();
		} catch (InterruptedException e) {
			logger.log(Level.WARNING, "Exception synchronizing the gui with the MQ.",e);
		}
		Platform.runLater(() -> {
			updateDisplay();
		});

	}

	private static boolean isOrderValid(CandyOrder order) {
		if (order == null)
			return false;
		if (order.getEmail() == null || order.getOrderItems() == null || order.getTimestamp() == null)
			return false;
		if (order.getEmail().isBlank() || order.getEmail().isEmpty())
			return false;
		if (order.getOrderItems().length == 0)
			return false;
		for (Candy item : order.getOrderItems())
			if (item == null)
				return false;
		return true;
	}

	private void updateDisplay() {
		if (this.currentCandyOrder == null) {
			setInvisable();
		} else {
			setViseble();
		}

	}

	private void setViseble() {
		this.candyNameColumn.setVisible(true);
		this.candyPriceColumn.setVisible(true);
		this.candyQuantityColumn.setVisible(true);
		this.candyTableView.setVisible(true);
		this.dateLabel.setText(this.currentCandyOrder.getTimestamp().toString());
		this.emailLabel.setText(this.currentCandyOrder.getEmail());
		this.candyTableView.getItems().clear();
		this.candyTableView.getItems().addAll(Arrays.asList(this.currentCandyOrder.getOrderItems()));
		this.infoLabel.setText("New order came in.");
	}

	private void setInvisable() {
		this.candyNameColumn.setVisible(false);
		this.candyPriceColumn.setVisible(false);
		this.candyQuantityColumn.setVisible(false);
		this.candyTableView.setVisible(false);
		this.dateLabel.setText("");
		this.emailLabel.setText("");
		this.infoLabel.setText("Waiting for orders");
	}

	private static CandyOrder fromXML(String xml) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(CandyOrder.class);
		Unmarshaller unmarshaller = context.createUnmarshaller();
		StringReader reader = new StringReader(xml);
		return (CandyOrder) unmarshaller.unmarshal(reader);

	}

	public void reject() {
		if (currentDelivery == null) {
			this.infoLabel.setText("No order present to reject.");
			return;
		}
		this.infoLabel.setText("Processing order...");
		this.accepteButton.setDisable(true);
		this.rejectButton.setDisable(true);
		Thread t = new Thread(()-> {
			processOrder(false);
		});
		t.start();

		
	}

	public void accepte() {
		if (this.currentDelivery == null) {
			this.infoLabel.setText("No order present to accepte.");
			return;
		}
		this.infoLabel.setText("Processing order...");
		this.accepteButton.setDisable(true);
		this.rejectButton.setDisable(true);
		Thread t = new Thread(()-> {
			processOrder(true);
		});
		t.start();
		
	}

	public void processOrder(boolean accapted) {
		StringBuilder sb = new StringBuilder();
		sb.append("The order for:\n");
		for (Candy item : currentCandyOrder.getOrderItems())
			sb.append(item.getName() + " " + item.getQuantity()+"\n");
		if (!accapted)
			sb.append("Has been rejected. We are sorry.");
		else
			sb.append("Has been accapted. Thank you for your trust.");
		if (!accapted)
			this.mailService.sendMail(currentCandyOrder.getEmail(), "Your order has been rejected.", sb.toString());
		else
			this.mailService.sendMail(currentCandyOrder.getEmail(), "Your order has been accapted.", sb.toString());
		secureComunicationService.sendOrder(currentCandyOrder, accapted);
		this.mqReciverService.sendBasicAcknowledgement(currentDelivery);
		this.currentCandyOrder = null;
		this.currentDelivery = null;
		Platform.runLater(()-> {
			updateDisplay();
			this.accepteButton.setDisable(false);
			this.rejectButton.setDisable(false);
		});
		
	}

}
