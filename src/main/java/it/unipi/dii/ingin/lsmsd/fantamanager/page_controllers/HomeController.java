package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.login;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HomeController implements Initializable {
	
	//Stage stage = new Stage();
	
    @FXML
    private Text welcomeText;
    @FXML
    private Parent root;

    @FXML
    protected void click_option() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("option_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Options page");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void click_formation() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("formation_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Formation page");
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
    }

    @FXML
    protected void click_shop() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("shop_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Shop page");
        stage.setScene(scene);
        stage.show();
    }
    
    
    @FXML
    protected void click_ranking() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("ranking_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Ranking page");
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcomeText.setText("Welcome "+ global.user.username + "!");
    }
    
    public void logout() throws IOException{
    	login.logout();
    	Stage stage = (Stage)root.getScene().getWindow();
    	util_controller.go_to_login(stage);
    }
    
}