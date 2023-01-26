package it.unipi.dii.ingin.lsmsd.fantamanager;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Arrays;

import it.unipi.dii.ingin.lsmsd.fantamanager.trades.*;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.*;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.*;

public class app extends Application {
	
	
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("login_registration_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
    	try {
    		
    		//utilities.update_matchday(-1);						//initialize the update_matchday matrix with all zero
            //utilities.set_next_matchday_redis(1);                 //initialize the next_matchday a 1, prima che inizi il campionato
    		global.updated_matchdays = utilities.get_updated_matchdays();
    		
    	} catch(Exception e){
    		e.printStackTrace();
    		return;
    	}

    	launch();
       
    }
    
}