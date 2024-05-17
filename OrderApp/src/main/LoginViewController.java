package main;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.FactoryUser;
import service.SecureComunicationService;

public class LoginViewController {
	private Stage parentStage;

	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label infoLabel;
	
	private Logger logger;
	
	private SecureComunicationService secureComunicationService;
	
	public LoginViewController(Stage parentStage) {
		super();
		logger = Logger.getLogger("OrderApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException einner) {
			logger.log(Level.WARNING, "Failed to add handler.",einner);
			
		} 
		this.parentStage = parentStage;
		this.parentStage.setOnCloseRequest( new EventHandler<WindowEvent>() {
			
			@Override
			public void handle(WindowEvent arg0) {
				
				secureComunicationService.close();
			}
		});
		this.secureComunicationService = new SecureComunicationService();
	}

	public void login() 
	{
		String username = usernameField.getText();

		String password = passwordField.getText();
		if(username==null||username.isBlank()||username.isEmpty())
		{
			this.infoLabel.setText("Username cannot be empty!");
			return;
		}
		if(password==null||password.isBlank()||password.isEmpty())
		{
			this.infoLabel.setText("Password cannot be empty!");
			return;
		}
		FactoryUser temp = new FactoryUser();
		temp.setUsername(username);
		temp.setPassword(password);
		if(secureComunicationService.login(temp))
			loadMainView();
		else 
		{
			this.infoLabel.setText("Inccorect username or password!");
		}
		
		
				 
	}
	private void loadMainView() {
		
		
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("OrderMainView.fxml"));
	        fxmlLoader.setController(new OrderMainViewController(this.parentStage,this.secureComunicationService));
			Scene scene;
			scene = new Scene(fxmlLoader.load());
		    parentStage.setScene(scene);
		} catch (IOException e) {
			this.logger.log(Level.WARNING,"Error while loading MainOrderView.fxml",e);
		}
	
	}


}
