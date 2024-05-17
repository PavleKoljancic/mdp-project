package clientapp.main;


import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
	
	   
    
    private static Scene scene;
    @Override
    public void start(Stage stage)  {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
            LoginViewController controller = new LoginViewController(stage); 
            fxmlLoader.setController(controller);
			scene = new Scene(fxmlLoader.load());
			controller.setCurrentScene(scene);
		    stage.setScene(scene);
		    stage.setTitle("Client App");
	        stage.show();
		} catch (IOException e) {
			Logger logger = Logger.getLogger("ClientApp Logger");
			try {
				logger.addHandler(new FileHandler("Main.log"));
			} catch (SecurityException | IOException einner) {
				logger.log(Level.WARNING, "Failed to add handler.",einner);
				
			}
			logger.log(Level.WARNING, "Failed to read LoginView.fxml.",e);
		}
     
      
    }


    public static void main(String[] args) {
    	
    	launch(args);
}}