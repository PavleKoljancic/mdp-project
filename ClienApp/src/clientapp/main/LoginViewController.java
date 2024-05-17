package clientapp.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
import model.User;
import service.CandyService;
import service.UserService;

public class LoginViewController {
	private Stage parentStage;
	private Scene currentScene;

	@FXML
	private TextField usernameField;
	@FXML
	private PasswordField passwordField;
	@FXML
	private Label infoLabel;
	
	private UserService userService;
	private Logger logger;
	
	public LoginViewController(Stage parentStage) {
		super();
		logger = Logger.getLogger("ClientApp Logger");
		try {
			logger.addHandler(new FileHandler("Main.log"));
		} catch (SecurityException | IOException einner) {
			logger.log(Level.WARNING, "Failed to add handler.",einner);
			
		} 
		this.parentStage = parentStage;
		this.userService = new UserService();
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
		User temp = new User();
		temp.setUsername(username);
		temp.setPassword(password);
		HttpURLConnection con = userService.login(temp);
		if(con==null)
		{
			this.infoLabel.setText("Couldn't connect to the server!");
			return;
		}
		
		try {
			int responseCode = con.getResponseCode();
			switch(responseCode) 
			{
				case HttpURLConnection.HTTP_OK:
					 BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
					 String result =  br.readLine();
					 loadMainView(result,temp.getUsername());
					break;
				case HttpURLConnection.HTTP_UNAUTHORIZED:
					this.infoLabel.setText("Login failed.");
					break;
				case HttpURLConnection.HTTP_FORBIDDEN:
					this.infoLabel.setText("Logout from your other session!");
					break;
				default:
					this.infoLabel.setText("Couldn't connect to the server!");
					break;
			}
			
		} catch (IOException e) {
			this.infoLabel.setText("Couldn't connect to the server!"); 
			logger.log(Level.WARNING, "Couldn't connect to the server!",e);
		}
		
			
		con.disconnect();
		
				 
	}
	private void loadMainView(String key, String username) {
		try {
			this.parentStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent arg0) {
					userService.logout(key, username);
					
					
				}
			}); 
		FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("MainView.fxml"));
        fxmlLoader.setController(new MainViewController(new CandyService(key, username)));
        Scene scene = new Scene(fxmlLoader.load()); 
		this.parentStage.setScene(scene);
		
		}
		catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load MainView.fxml!",e);
		}
	}

	public void setCurrentScene(Scene currentScene) {
		this.currentScene = currentScene;
	}

	 
	public void switchToRegisterView() 
	{
		
        try {

        	FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("RegistrationView.fxml"));
            fxmlLoader.setController(new RegisterViewController(this.parentStage,this.userService,this.currentScene));
            Scene scene = new Scene(fxmlLoader.load()); 
			this.parentStage.setScene(scene);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load RegistrationView.fxml!",e);
		}

	}
}
