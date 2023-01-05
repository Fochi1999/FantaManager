package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.player_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.Trade;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.control.ListView;

public class NewTradeController implements Initializable{

	@FXML private Parent root;
	@FXML private CheckBox credits_checkbox;
	@FXML private Button create_trade;
	@FXML private Button back;
	@FXML private Text error_text;
	@FXML private ListView<String> card_list;
	
	@FXML private TextField card_from1;
	@FXML private TextField card_from2;
	@FXML private TextField card_from3;
	@FXML private TextField credits_from;
	
	@FXML private TextField card_to1;
	@FXML private TextField card_to2;
	@FXML private TextField card_to3;
	@FXML private TextField credits_to;
	
	@FXML private CheckBox card_from_checkbox1;
	@FXML private CheckBox card_from_checkbox2;
	@FXML private CheckBox card_from_checkbox3;
	@FXML private CheckBox card_to_checkbox1;
	@FXML private CheckBox card_to_checkbox2;
	@FXML private CheckBox card_to_checkbox3;
	
	@FXML private Button add_from1;
	@FXML private Button add_from2;
	@FXML private Button add_from3;
	@FXML private Button add_to1;
	@FXML private Button add_to2;
	@FXML private Button add_to3;
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		error_text.setText("");
		create_trade.setDisable(true);	//TODO create button disabled till finished controller page
		open_cards_collection();
	}
	
	
	public void click_back() throws IOException{
		System.out.println("Closing 'new trade' page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.go_to_trades(stage);
	}
	
	
	@FXML
	private void create_trade(){
		
		if(!check_fields()) {
			System.out.println("Error on input fields!");
			return;
		}
		//TODO create with mongoDB
		System.out.println("OK!");

		//ArrayList<String> player_from=ricava_player_from();
		//Trade new_trade=new Trade("", global.id_user,"",player_from,player_to,credits_from.getText(),0);

		//TODO delete dalla propria collection su redis

	}

	/*private ArrayList<String> ricava_player_from() {
			ArrayList<String> player_from=new ArrayList<>();

			//if()
	}*/


	private void open_cards_collection() {
		//TODO retrieve cards from redis collection
			collection.apertura_pool();
			ArrayList<player_collection> collection_of_user= collection.load_collection(global.id_user);

			for(player_collection player:collection_of_user){
						//da qui ogni player ha le sue quattro informazioni e poi usarle agile
			}
			collection.closePool();
	}
	
	
	public Boolean check_fields(){
		
		//check if the card values are empty when ticked
		if(card_from_checkbox1.isSelected() && card_from1.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_from_checkbox2.isSelected() && card_from2.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_from_checkbox3.isSelected() && card_from3.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_to_checkbox1.isSelected() && card_to1.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_to_checkbox2.isSelected() && card_to2.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_to_checkbox3.isSelected() && card_to3.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		
		//check if credits fields are not empty when ticked
		if(credits_checkbox.isSelected() && (credits_to.getText().equals("") || credits_from.getText().equals(""))){
			error_text.setText("Missing value on credits!");
			return false;
		}
		
		//check if credit fields have numeric values
		if((credits_to.getText().matches("[0-9]*") || credits_from.getText().matches("[0-9]*"))){
			error_text.setText("Credits value must be an integer!");
			return false;
		}	
		
		//check if at least one card is selected
		if(!card_from_checkbox1.isSelected() && !card_to_checkbox1.isSelected() && !card_from_checkbox2.isSelected() 
				&& !card_to_checkbox3.isSelected() && !card_from_checkbox3.isSelected() && !card_to_checkbox3.isSelected()) {
			error_text.setText("You must select at least one card to create trade!");
			return false;
		}
		
		return true;
	}
	
}
