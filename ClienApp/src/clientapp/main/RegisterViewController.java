package clientapp.main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.RegistrationRequest;
import service.UserService;

public class RegisterViewController {
	
	private Stage parentStage;
	private UserService userService;
	private Scene parentScene;
	
	@FXML
	 private TextField companyName;
	@FXML
	private TextField address;
	@FXML 
	private TextField phoneNumber;
	@FXML 
	private TextField username;
	@FXML 
	private TextField password;
	@FXML 
	private TextField passwordRepeat;
	
	@FXML
	private Label infoLabel;
	private Logger logger;
	
	public RegisterViewController(Stage parentStage, UserService userService, Scene parentScene) {
		logger = Logger.getLogger("ClientApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException einner) {
			logger.log(Level.WARNING, "Failed to add handler.",einner);
			
		} 
		this.parentStage = parentStage;
		this.userService = userService;
		this.parentScene = parentScene;
	}
	
	
	public void register() 
	{
		
		RegistrationRequest registrationRequest = new RegistrationRequest();
		
		if(username.getText()==null||username.getText().isEmpty()||username.getText().isBlank())
		{
			this.infoLabel.setText("Username cannot be empty!");
			return;
		}
		registrationRequest.setUsername(username.getText());
		
		if(companyName.getText()==null||companyName.getText().isEmpty()||companyName.getText().isBlank())
		{
			this.infoLabel.setText("Company name cannot be empty!");
			return;
		}
		registrationRequest.setCompanyName(companyName.getText());
		
		if(address.getText()==null||address.getText().isEmpty()||address.getText().isBlank())
		{
			this.infoLabel.setText("Address name cannot be empty!");
			return;
		}
		registrationRequest.setAddress(address.getText());
		
		if(phoneNumber.getText()==null||phoneNumber.getText().isEmpty()||phoneNumber.getText().isBlank())
		{
			this.infoLabel.setText("Phone number name cannot be empty!");
			return;
		}
		registrationRequest.setPhoneNumber(phoneNumber.getText());
		
		if(password.getText()==null||password.getText().isEmpty()||password.getText().isBlank())
		{
			this.infoLabel.setText("Password number name cannot be empty!");
			return;
		}
		registrationRequest.setPassword(password.getText());
		
		if(passwordRepeat.getText()==null||passwordRepeat.getText().isEmpty()||passwordRepeat.getText().isBlank())
		{
			this.infoLabel.setText("Password repeat number name cannot be empty!");
			return;
		}
		registrationRequest.setPasswordRepeat(passwordRepeat.getText());
		
		if(!registrationRequest.checkPasswordMatch()) 
		{
			this.infoLabel.setText("Password dont match!");
			return;
		}
		HttpURLConnection con = userService.register(registrationRequest);
		if(con!=null) 
		{
			try {
				switch(con.getResponseCode()) 
				{
					case HttpURLConnection.HTTP_CONFLICT:
						this.infoLabel.setText("Username taken!");
						break;
					case HttpURLConnection.HTTP_CREATED:
						this.infoLabel.setText("Account created!");
						break;
					default:
						this.infoLabel.setText("Server error");
						break;
				}
			} catch (IOException e) {
				this.infoLabel.setText("Server error");
				logger.log(Level.INFO, "Server error.",e);
			}
			
		}
		
		
	}
	
	public void goToParentScene() 
	{ 
		this.parentStage.setScene(parentScene);
	}

}
