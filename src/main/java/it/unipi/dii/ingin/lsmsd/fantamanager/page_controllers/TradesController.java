package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.player_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.CardMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.Trade;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.TradeMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.RankingMongoDriver;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import com.mongodb.client.result.*;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.fxml.Initializable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.collections.*;
import javafx.stage.Stage;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.descending;

public class TradesController implements Initializable{

	@FXML
	private ListView<String>trade_list;
    
	@FXML 
	private TextField search_card_from;
	
	@FXML
	private TextField search_card_to;

	@FXML
	private TextArea selected_trade;
	
	@FXML
    private Parent root;
	
	@FXML
	private Button delete_button;
	
	@FXML
	private Button accept_button;

	@FXML
	private ChoiceBox offered_wanted;



	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		System.out.println("Opening trades page..."); 	
    	show_all_button_onclick();
    	
    	//turning off the buttons
    	delete_button.setDisable(true);
    	accept_button.setDisable(true);

		//assign value to choice box

		offered_wanted.getItems().add("offered");
		offered_wanted.getItems().add("wanted");


    	//handling the event: clicking on a trade from the list
        MultipleSelectionModel<String> trades = trade_list.getSelectionModel();

        trades.selectedItemProperty().addListener(new ChangeListener<String>() {
           public void changed(ObservableValue<? extends String> changed, String oldVal, String newVal) {

        	  delete_button.setDisable(true);
              String selItems = "";
              ObservableList<String> selected = trade_list.getSelectionModel().getSelectedItems();

               for (int i = 0; i < selected.size(); i++) {
                  selItems += "" + selected.get(i); 
               }
               
               selected_trade.setText(selItems); //the trade will show up on the lower Area
               
               //calling the function only if a trade is selected
               if(!selected_trade.getText().isEmpty()) {
				   //chosen_trade=retrieve_trade(selected_trade.getText());
				   activate_buttons(selected_trade.getText());
			   }
           }
        });
    	
	}
	@FXML
	public void activate_buttons(String trade_text) {
		
		String myuser = global.user.username;
		
		String words_arr[] = trade_text.split(" ");
		String user_in = words_arr[words_arr.length-4]; //is the position where the user is saved in the string
		if(myuser.equals(user_in)){
			delete_button.setDisable(false);
		}

		if(!myuser.equals(user_in) && control_owned_cards()){
			accept_button.setDisable(false);
		}
	}

	private boolean control_owned_cards() {
			Trade chosen_trade=retrieve_trade();
			for(String player:chosen_trade.get_player_to()){
				player_collection player_to= CardMongoDriver.search_player_by_name(player);
				if(!collection.presence_player(player_to,global.id_user))
						return false;
			}
			return true;
	}

	@FXML
    protected void click_home() throws IOException {

    	System.out.println("Closing trades page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
    
    
    @FXML
    protected void click_delete() {   //elimino solo una mia offerta di trade, questa funzione è cliccabile solo quando clicco su un trade mio
    	
    	String trade_text = selected_trade.getText();
    	if(trade_text.equals("")) {
    		System.out.println("No trade selected to delete!");
    		return;
    	}
    	
    	String words_arr[] = trade_text.split(" ");
		ObjectId trade_id = new ObjectId(words_arr[words_arr.length-1]);
		System.out.println("Deleting trade with object id: " + trade_id);
		

		TradeMongoDriver.delete_my_trade(trade_id);
    	
    	//myClient.close();
		TradeMongoDriver.closeConnection();

		//devo riaggiungere i giocatori alla mia collection, visto che quando li propongo mi vengono tolti temporaneamente dalla collection
		Trade chosen_trade=retrieve_trade();
		for(String player:chosen_trade.get_player_from()){
			player_collection player_from= CardMongoDriver.search_player_by_name(player);
			collection.add_player_to_collection(player_from,global.id_user);  //riaggiungo in collection i giocatori che stavo offrendo
		}

    	my_requests_button_onclick(); //refreshing the available trade list
    }
    
    
	public void show_all_button_onclick() {

    	
    	show_trades(TradeMongoDriver.trades_pending());
		TradeMongoDriver.closeConnection();

	}
	
	public void my_requests_button_onclick() {
		
		String my_user = global.user.username;
		show_trades(TradeMongoDriver.search_user(my_user));
		TradeMongoDriver.closeConnection();
	}
	
	public void search_button_onaction() {
		
		String user_input = search_card_from.getText();
		String card_input = search_card_to.getText();
		if(!user_input.isEmpty() || !card_input.isEmpty()) { //not searching if the 'search' button is clicked when the text fields are empty
    		show_trades(TradeMongoDriver.search_trade(user_input, card_input));
		}
		TradeMongoDriver.closeConnection();
	}
	
	public void show_trades(MongoCursor<Document> result) {
		
		//showing off trades
		ObservableList<String> list = FXCollections.observableArrayList();
    	list.removeAll(list);	//clearing the list
    	
    	
    	while(result.hasNext()) {	
    		Document trade_doc = result.next();
    		String trade_id = trade_doc.get("_id").toString();
    		String player_from = trade_doc.get("player_from").toString();
    		String player_to = trade_doc.get("player_to").toString();
    		String credits = trade_doc.get("credits").toString();
    		String user_from = trade_doc.getString("user_from");
    		String trade_output = ">> Players offered: " + player_from + " \n<< Players wanted: " + player_to 
    				+ " \n$$$ Credits: " + credits +" \n--- Trade request made by: " + user_from + " \n%% trade_id: " + trade_id;
    		list.add(trade_output);
    	}
    	trade_list.getItems().clear();
		trade_list.getItems().addAll(list);

		TradeMongoDriver.closeConnection();
	}
	

	public void show_most_present(MouseEvent mouseEvent) {

		String player_trade="";



			try(MongoCursor<Document> cursor= TradeMongoDriver.retrieve_most_present((String) offered_wanted.getValue())){
				while(cursor.hasNext()){
					//System.out.println(cursor.next().toJson());
					//show_trades(cursor);
					Document player=cursor.next();
					player_trade += "" + player.get("_id")+"-->"+player.get("count")+"times\n";
				}
				selected_trade.setText(player_trade); //the trade will show up on the lower Area
			}
			TradeMongoDriver.closeConnection();
	}


	public Trade retrieve_trade() {
				String words_arr[] = selected_trade.getText().split("\n");

				String elem = null;

				for(int i=0;i< words_arr.length;i++){
						elem=words_arr[i].split(": ")[1];

				}
				return TradeMongoDriver.search_trade_byId(elem);  //elem ora contiene l' id del trade
	}
	public void accept_trade(MouseEvent mouseEvent) {


				Trade chosen_trade=retrieve_trade();

				for(String player:chosen_trade.get_player_from()){
						player_collection player_from= CardMongoDriver.search_player_by_name(player);
						collection.add_player_to_collection(player_from,global.id_user);  //aggiunti all' utente che ha accettato, ovvero quello loggato
						//dalla collection dell' altro tizio non vanno tolti in quanto si sono tolti al momento in cui lui li ha offerti
				}

				for(String player:chosen_trade.get_player_to()){
						player_collection player_to= CardMongoDriver.search_player_by_name(player);
						collection.delete_player_from_collection(player_to); //elimino dalla collection del giocatore che ha accettato, i giocatori richiesti da chi ha generato il trade

						collection.add_player_to_collection(player_to,(RankingMongoDriver.retrieve_user(true,chosen_trade.get_user_from())).get(0).get("_id").toString()); //aggiunti alla collection di quello che aveva proposto il trade
				}

				//update status trade
				TradeMongoDriver.update_trade(chosen_trade,"status");
				show_all_button_onclick();
	}
}
