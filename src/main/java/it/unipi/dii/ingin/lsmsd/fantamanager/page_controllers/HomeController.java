package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.login;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collectionRedisDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.CardMongoDriver;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HomeController implements Initializable {
	
	
    @FXML
    private Text welcomeText;
    @FXML
    private Text wait_text; 
    @FXML
    private Text err_text;
    @FXML
    private ImageView wait_gif;
    
    
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
    	
    	//hiding 'loading' items
    	err_text.setVisible(false);
    	wait_text.setVisible(false);
    	wait_gif.setVisible(false);
    	welcomeText.setText("Welcome "+ global.user.username + "!");
    	
    	if(global.owned_cards_list == null || global.full_card_list == null) {
    			load_collections();
    		}
    	
    }
    
    public void logout() throws IOException{
    	login.logout();
    	Stage stage = (Stage)root.getScene().getWindow();
    	util_controller.go_to_login(stage);
    }
    
    
    public void load_collections(){
    	new Thread(() -> {
    		wait_text.setVisible(true);
        	wait_gif.setVisible(true);
        	System.out.println("Reading card collection..");
        	welcomeText.setText("Loading card collection...");
        	try{
        		global.owned_cards_list = collectionRedisDriver.load_collection(global.id_user);
        	}		
        	catch(Exception e){
        		wait_text.setText("Error");
        		welcomeText.setText("Error");
            	wait_gif.setVisible(false);
        		err_text.setVisible(true);
        		return;
        	}
        	
        	if(global.full_card_list == null) {
        		System.out.println("Reading full card list..");
            	welcomeText.setText("Loading full card list...");
            	try {
            		global.full_card_list = CardMongoDriver.retrieve_cards("");
            	}
            	catch(Exception e){
            		wait_text.setText("Error");
            		welcomeText.setText("Error");
                	wait_gif.setVisible(false);
            		err_text.setVisible(true);
            		return;
            	}
            }
        	wait_text.setVisible(false);
        	wait_gif.setVisible(false);
        	System.out.println("Load complete.");
        	
        	welcomeText.setText("Welcome "+ global.user.username + "!");
    	}).start();	
    }    
}