package main;

import java.io.FileNotFoundException;
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
import model.FactoryUser;
import model.User;
import model.dao.UserDAO;
import service.MulticastPromotionService;

public class UserViewController implements Initializable {
	private UserDAO dao;
	private MulticastPromotionService multicastPromotionService;
	private Logger logger;
	
	public UserViewController() throws FileNotFoundException, IOException {
		this.dao = new UserDAO();
		this.multicastPromotionService = new MulticastPromotionService();
		logger = Logger.getLogger("Factory app Logger");
    	try {
    		logger.addHandler(new FileHandler("Main.log"));
    	} catch (SecurityException | IOException einner) {
    		logger.log(Level.WARNING, "Failed to add handler.",einner);
    		
    	}
	}
	@FXML
	private TableView<User> usersTable;
	@FXML
	private TableColumn<User, String> companyColumn;	
	@FXML
	private TableColumn<User, String> addressColumn;
	@FXML
	private TableColumn<User, String> phoneNumberColumn;
	@FXML
	private TableColumn<User, Boolean> activatedColumn;
	@FXML
	private TableColumn<User, Boolean> blockedColumn;
	@FXML
	private TableColumn<User, String> usernameColumn;
	@FXML
	private Label infoLabel;
	@FXML
	private TextField promotionTextField;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		usernameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("Username"));
		companyColumn.setCellValueFactory(new PropertyValueFactory<User, String>("CompanyName"));
		addressColumn.setCellValueFactory(new PropertyValueFactory<User, String>("Address"));
		phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<User, String>("PhoneNumber"));
		blockedColumn.setCellValueFactory(new PropertyValueFactory<User, Boolean>("Blocked"));
		activatedColumn.setCellValueFactory(new PropertyValueFactory<User, Boolean>("Activated"));
		usersTable.getItems().addAll(dao.getAll());
		usersTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            
            return row;
        });
		
	}
	
	public void activate() 
	{
		User item = this.usersTable.getSelectionModel().getSelectedItem();
		if(item==null) 
		{
			this.infoLabel.setText("No user selected!");
			return;
		}
		if(item.isActivated())
		{
			this.infoLabel.setText("User is already activated!");
			return;
		}
		item.setActivated(true);
		this.dao.update(item);
		this.usersTable.getItems().clear();
		this.usersTable.getItems().addAll(this.dao.getAll());
		this.infoLabel.setText("User activated!");
	}
	public void block()  
	{
		User item = this.usersTable.getSelectionModel().getSelectedItem();
		if(item==null) 
		{
			this.infoLabel.setText("No user selected!");
			return;
		}
		if(item.isBlocked())
		{
			this.infoLabel.setText("User is already blocked!");
			return;
		}
		item.setBlocked(true);
		this.dao.update(item);
		this.usersTable.getItems().clear();
		this.usersTable.getItems().addAll(this.dao.getAll());
		this.infoLabel.setText("User blocked!");
		
	}
	public void remove() 
	{
		User item = this.usersTable.getSelectionModel().getSelectedItem();
		if(item==null) 
		{
			this.infoLabel.setText("No user selected!");
			return;
		}
		if(this.dao.remove(item))
		{	
			this.infoLabel.setText("User removed.");
			this.usersTable.getItems().clear();
			this.usersTable.getItems().addAll(this.dao.getAll());
		}
	}
	public void refresh() 
	{
		try {
			this.dao.reloadFromFile();
			this.usersTable.getItems().clear();
			this.usersTable.getItems().addAll(this.dao.getAll());
		} catch (IOException e) {
			
			logger.log(Level.WARNING, "Failed to read the user data from file.",e);
		}
	}
	
	public void sendPromtion() 
	{
		String promotionText = this.promotionTextField.getText();
		if(promotionText==null||promotionText.isBlank()||promotionText.isEmpty()) 
		{	
			this.infoLabel.setText("Promotion text cannot be empty");
			return;
		}
		this.multicastPromotionService.sendPromtion(promotionText);
		this.promotionTextField.setText("");
		this.infoLabel.setText("Promotion sent!");
	}
}
