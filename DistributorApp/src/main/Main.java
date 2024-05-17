package main;



import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mylogger.LoggerSingleton;


public class Main extends Application {
	
	   
    private static final  String RESOURCE_PATH="resources";
    private static Scene scene;
    @Override
    public void start(Stage stage)  {

        try {
			scene = new Scene(this.loadFXML(stage));
		    stage.setScene(scene);
		    stage.setTitle("RMI Distributor APP");
	        stage.show();
		} catch (IOException e) {
			LoggerSingleton.getInstance().log(Level.WARNING,"Couldn't load fxml file for gui..",e);
		}
    
      
    }

    private  Parent loadFXML(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FirstView.fxml"));
        fxmlLoader.setController(new FirstViewController(stage));
        return fxmlLoader.load();

      
        
    }
    public static void main(String[] args) {
    	System.setProperty("java.security.policy", RESOURCE_PATH+File.separator+"client_policy.txt" );
		if(System.getSecurityManager()==null)
			System.setSecurityManager(new SecurityManager());
    	launch(args);
}}