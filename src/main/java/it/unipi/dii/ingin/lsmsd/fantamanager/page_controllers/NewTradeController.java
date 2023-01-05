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
import javafx.scene.control.MultipleSelectionModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public class NewTradeController implements Initializable{

	@FXML private Parent root;
	@FXML private CheckBox credits_checkbox;
	@FXML private Button create_trade;
	@FXML private Button back;
	@FXML private Text error_text;
	@FXML private ListView<String> card_list;
	@FXML private TextField selected_card;
	
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
		
		disable_fields();
		add_checkbox_listeners();
		
		open_cards_collection();
		
		//handling the event: clicking on a card from the list
        MultipleSelectionModel<String> card = card_list.getSelectionModel();

        card.selectedItemProperty().addListener(new ChangeListener<String>() {
        	public void changed(ObservableValue<? extends String> changed, String oldVal, String newVal) {

        	   String selItems = "";
              	ObservableList<String> selected = card_list.getSelectionModel().getSelectedItems();

               	for (int i = 0; i < selected.size(); i++) {
            	   selItems += "" + selected.get(i); 
               	}
               	
               	if(!selItems.equals("")) {
               		selected_card.setText(selItems);
               	}
               	   
        	}
        });
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
		
		//check if at least one card is selected
		if(!card_from_checkbox1.isSelected() && !card_to_checkbox1.isSelected() && !card_from_checkbox2.isSelected() 
			&& !card_to_checkbox3.isSelected() && !card_from_checkbox3.isSelected() && !card_to_checkbox3.isSelected()) {
			error_text.setText("You must select at least one card to create trade!");
			return false;
		}
		
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
		
		return true;
	}
	
	
	public void add_checkbox_listeners(){
		
		card_from_checkbox1.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_from1.setDisable(!add_from1.isDisabled());
			         card_from1.setDisable(!card_from1.isDisabled());
			         if(card_from1.isDisabled()) {
			        	 card_from1.setText("");
			         }
			      });
		card_from_checkbox2.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_from2.setDisable(!add_from2.isDisabled());
			         card_from2.setDisable(!card_from2.isDisabled());
			         if(card_from2.isDisabled()) {
			        	 card_from2.setText("");
			         }
			      });
		card_from_checkbox3.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_from3.setDisable(!add_from3.isDisabled());
			         card_from3.setDisable(!card_from3.isDisabled());
			         if(card_from3.isDisabled()) {
			        	 card_from3.setText("");
			         }
			      });
		
		card_to_checkbox1.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_to1.setDisable(!add_to1.isDisabled());
			         card_to1.setDisable(!card_to1.isDisabled());
			         if(card_to1.isDisabled()) {
			        	 card_to1.setText("");
			         }
			      });
		card_to_checkbox2.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_to2.setDisable(!add_to2.isDisabled());
			         card_to2.setDisable(!card_to2.isDisabled());
			         if(card_to2.isDisabled()) {
			        	 card_to2.setText("");
			         }
			      });
		card_to_checkbox3.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_to3.setDisable(!add_to3.isDisabled());
			         card_to3.setDisable(!card_to3.isDisabled());
			         if(card_to3.isDisabled()) {
			        	 card_to3.setText("");
			         }
			      });
		
		credits_checkbox.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
				         credits_to.setDisable(!credits_to.isDisabled());
				         credits_from.setDisable(!credits_from.isDisabled());
				      });
		
	}

	private void disable_fields() {
		add_from1.setDisable(true);
		add_from2.setDisable(true);
		add_from3.setDisable(true);
		add_to1.setDisable(true);
		add_to2.setDisable(true);
		add_to3.setDisable(true);
		credits_to.setDisable(true);
		credits_from.setDisable(true);
		card_from1.setDisable(true);
		card_from2.setDisable(true);
		card_from3.setDisable(true);
		card_to1.setDisable(true);
		card_to2.setDisable(true);
		card_to3.setDisable(true);
	}
	
}
