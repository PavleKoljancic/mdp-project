package main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import model.FactoryUser;
import model.dao.FactoryUserDAO;

public class FactoryUsersViewController implements Initializable {


	private FactoryUserDAO dao;
	
	@FXML
	private TableView<FactoryUser> usersTable;
	@FXML
	private TableColumn<FactoryUser, String> usernameColumn;
	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private PasswordField passwordRepeatField;
	@FXML
	private Label infoLabel;
	
	

	public FactoryUsersViewController() throws FileNotFoundException, IOException {
		this.dao = new FactoryUserDAO(); 
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		usernameColumn.setCellValueFactory(new PropertyValueFactory<FactoryUser, String>("Username"));
		usersTable.getItems().addAll(dao.getAll());
		usersTable.setRowFactory(tv -> {
            TableRow<FactoryUser> row = new TableRow<>();
            
            return row;
        });
		
	}
	
	public void add() {
		String usernameStr = this.usernameField.getText();
		if(usernameStr==null||usernameStr.isBlank()||usernameStr.isEmpty())
		{
			infoLabel.setText("No username provided!");
			return;
		}
		String pass1 = this.passwordField.getText();
		if(pass1==null||pass1.isBlank()||pass1.isEmpty())
		{
			infoLabel.setText("No password provided!");
			return;
		}
		
		String pass2 = this.passwordRepeatField.getText();
		if(pass2==null||pass2.isBlank()||pass2.isEmpty())
		{
			infoLabel.setText("No password repeat provided!");
			return;
		}
		if(!pass1.equals(pass2))
		{
			infoLabel.setText("Passwords dont match!");
			return;
		}
		dao.create(new FactoryUser(usernameStr,pass1));
		usersTable.getItems().clear();
		usersTable.getItems().addAll(dao.getAll());
	}
	public void remove() {
		FactoryUser item = this.usersTable.getSelectionModel().getSelectedItem();
		if(item==null) 
		{
			this.infoLabel.setText("No user selected!");
			return;
		}
		this.dao.remove(item);
		
		usersTable.getItems().clear();
		usersTable.getItems().addAll(dao.getAll());
	}
}
