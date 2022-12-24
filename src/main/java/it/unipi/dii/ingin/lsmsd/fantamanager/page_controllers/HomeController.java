package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import java.io.IOException;
import javafx.stage.Stage;

public class HomeController {
	
	//Stage stage = new Stage();
	
    @FXML
    private Label welcomeText;
    @FXML
    private Parent root;

    @FXML
    protected void click_option() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("option_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Option page");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void click_formation() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("formation_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Option page");
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    protected void click_trades() throws IOException {
    	
    	Stage stage = (Stage)root.getScene().getWindow();
    	FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("trade_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Trades page");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void click_collection() throws IOException {

        Stage stage= (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("collection_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Collection page");
        stage.setScene(scene);
        stage.show();

        //CollectionController.apertura_pool();  //ok?

        //CollectionController.create_table();   //ok?
    }
    
}