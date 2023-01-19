package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.cards;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collectionRedisDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.see_card;


public class SeeCardController implements Initializable{
	
	@FXML private Text fullname_field;
	@FXML private TextField team_field;
	@FXML private TextField position_field;
	@FXML private TextArea text_area;
	
	@FXML private Text buy_card_text;
	@FXML private Button buy_card;
	
	@FXML private ChoiceBox matchday_list;
	@FXML private Text matchday_error;
	
	@FXML private Parent root;
	
	private int last_updated_matchday;
	
	private Document card_doc; //saving the card's document 
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		//buy_card.setDisable(true);	//disabling the buy button
		buy_card_text.setText(""); 	//setting to empty the error message string
		matchday_error.setText("");
		text_area.setWrapText(true);
		
		search_card();
		get_general_info();
		
		last_updated_matchday = 1;	
		for(int i=1; i<=last_updated_matchday; i++) {
			matchday_list.getItems().add(i);
		}
	}
	
	
	@FXML
    protected void click_back() throws IOException {

		System.out.println("Returning back to the shop page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.go_to_shop(stage);   
    }
	@FXML
	protected void click_shot_stats() throws IOException {
	try {
		System.out.println("going to shot stats...");
		Stage stage = (Stage) root.getScene().getWindow();
		FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("shot_stats_page.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("Shots page");
		stage.setScene(scene);
		stage.show();
	}
	catch(Exception e){
		e.printStackTrace();
		System.out.println("errore nel caricare gli shots");
		}
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
			text_area.setText(t);;
			return;
		}
		
    	//showing up the infos
    	fullname_field.setText(card_doc.getString("fullname"));
    	team_field.setText(card_doc.getString("team")); 
    	position_field.setText(card_doc.getString("position")); 
    	buy_card.setText("Buy card - " + card_doc.get("credits").toString() + " credits");
    }
	
	
	public void get_general_info() {
		
		matchday_error.setText("");
		matchday_list.setValue(null);
		
		String text = see_card.get_general_info(card_doc);
		text_area.setText(text);
	}
	
	public void get_general_stats() throws ParseException{
		
		matchday_error.setText("");
		matchday_list.setValue(null);
		
		String text = see_card.get_general_stats(card_doc);
		text_area.setText(text);
	}
	
	public void get_matchday_info() {
		
		matchday_error.setText("");
		if(matchday_list.getValue() == null) {	//error handling
			System.out.println("Insert a matchday value.");
			matchday_error.setText("Insert a matchday value");
			return;
		}
		
		int matchday = Integer.parseInt(matchday_list.getValue().toString());
		String text = see_card.get_matchday_info(card_doc,matchday);
		text_area.setText(text);
	}

    public void buy_card(MouseEvent mouseEvent) throws NoSuchAlgorithmException {

		int credits=card_doc.getInteger("credits");
		if(check_credits((int)credits)) {
			OptionsMongoDriver.update_user_credits(false, global.user.username, credits);
			card_collection bought_card = new card_collection(card_doc.getInteger("player_id"), card_doc.getString("fullname"), 1, card_doc.getString("team"), card_doc.getString("position"));
			collectionRedisDriver.add_card_to_collection(bought_card, global.id_user);
			System.out.println("Card bought");
			buy_card_text.setText("Card successfully bought.");
		}
		else{
			System.out.println("Not enough credits.");
			buy_card_text.setText("Not enough credits.");
		}
    }
	private boolean check_credits(int value) {	//checks if the user owns a correct value of credits to buy this card

		if(global.user.getCredits() > value) {
			return true;
		}
		return false;
	}
}
