package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.see_card;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;


public class ShotsStatsController implements Initializable{
	

	
	@FXML private Parent root;

	private int last_updated_matchday;

	private Document card_doc; //saving the card's document
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		//buy_card.setDisable(true);	//disabling the buy button
		
		search_card();
		last_updated_matchday = 1;	//TODO implementare

	}
	
	
	@FXML
    protected void click_back() throws IOException {
		System.out.println("Returning back to the shop page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.go_to_shop(stage);   
    }
	
	@FXML
    protected void click_home() throws IOException {

		System.out.println("Returning back to the home page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
	
	
	public void search_card() {
		
		//searching the card from the DB
		ObjectId card_id = new ObjectId(ShopController.card_id_input);
		card_doc = see_card.search_card(card_id);
    	
		if(card_doc == null) {	//handling error
			String t = ("An error has occurred while searching for the card. Please, exit the page and try again later.");
			return;
		}

    }
	
	

}
