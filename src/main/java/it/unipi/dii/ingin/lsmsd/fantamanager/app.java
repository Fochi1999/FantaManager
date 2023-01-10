package it.unipi.dii.ingin.lsmsd.fantamanager;

import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.player_class;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

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
    		//populateDB.create_users_collection_mongoDB(200000);		//users collection - mongo
            //populateDB.create_cards_collection_mongoDB();  -->prendi file cardsDUMP.json da chat gruppo
    		//populateDB.create_trade_collection_mongoDB(1000000);		//trades collection - mongo
    		//populateDB.create_user_card_collection_redis();			//user's card collection - redis
    	} catch(Exception e){
    		e.printStackTrace();
    	}

    	launch();
       
    }
    
}