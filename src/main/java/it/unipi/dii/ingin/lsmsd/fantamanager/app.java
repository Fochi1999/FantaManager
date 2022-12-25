package it.unipi.dii.ingin.lsmsd.fantamanager;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.*;

public class app extends Application {
	
	
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("login_registration_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        
    	//trade_main.createTrade();
    	launch();
        
    }
}