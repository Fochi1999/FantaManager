package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import org.bson.Document;
import org.bson.types.ObjectId;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.see_card;


public class SeeCardController implements Initializable{
	
	@FXML private Text fullname_field;
	@FXML private TextField team_field;
	@FXML private TextField position_field;
	@FXML private TextFlow text_flow;
	
	@FXML private Text buy_card_text;
	@FXML private Button buy_card;
	
	@FXML
	private Parent root;
	
	private Document card_doc; //saving the card's document 
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		buy_card.setDisable(true);	//disabling the buy button
		buy_card_text.setText(""); 	//setting to empty the error message string
		search_card();
		get_general_info();
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
			Text t1 = new Text("An error has occurred while searching for the card. Please, exit the page and try again later.");
			text_flow.getChildren().add(t1);
			return;
		}
		
    	//showing up the infos
    	fullname_field.setText(card_doc.getString("fullname"));
    	team_field.setText(card_doc.getString("team")); 
    	position_field.setText(card_doc.getString("position")); 
    	buy_card.setText("Buy card - " + card_doc.get("credits").toString() + " credits");
    }
	
	
	public void get_general_info() {
		
		//cleaning the field
		text_flow.getChildren().clear();
		
		//showing up the text
		String text = see_card.get_general_info(card_doc);
		Text t1 = new Text(text);
		t1.setStyle("-fx-font: 16 system;");
		text_flow.getChildren().add(t1);
	}
	
}
