package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.CardMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.Trade;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.TradeMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.RankingMongoDriver;

import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.bson.Document;
import com.mongodb.client.MongoCursor;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.Initializable;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import javafx.collections.*;
import javafx.stage.Stage;
import org.bson.types.ObjectId;

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

		offered_wanted.getItems().add("Offered");
		offered_wanted.getItems().add("Wanted");


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
		String status = words_arr[2];
		int credits = Integer.parseInt(words_arr[words_arr.length-10]);
		
		if(myuser.equals(user_in) && status.equals("PENDING")){
			delete_button.setDisable(false);
		}

		if(!myuser.equals(user_in) && control_owned_cards() && check_credits(credits)){
			accept_button.setDisable(false);
		}
		else{
			accept_button.setDisable(true);
		}
	}

	private boolean control_owned_cards() {		//checks if the user owns the correct cards in order to accept the trade
			Trade chosen_trade=retrieve_trade();
			for(String card:chosen_trade.get_card_to()){
				card_collection card_to= CardMongoDriver.search_player_by_name(card);
				if(!collection.presence_card(card_to,global.id_user))
						return false;
			}
			return true;
	}
	
	private boolean check_credits(int value) {	//checks if the user owns a correct value of credits in order to accept the trade
		value = 0 - value;
		if(global.user.getCredits() > value) {
			return true;
		}
		return false;
	}

	@FXML
    protected void click_home() throws IOException {

    	System.out.println("Closing trades page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
    
    @FXML
    protected void create_trade() throws IOException{
    	System.out.println("Opening 'new trade' page...");
        Stage stage = (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("new_trade.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("New trade");
        stage.setScene(scene);
        stage.show();
    }
	
	
    @FXML
    protected void click_delete() throws NoSuchAlgorithmException {   //elimino solo una mia offerta di trade, questa funzione è cliccabile solo quando clicco su un trade mio
    	
    	String trade_text = selected_trade.getText();
    	if(trade_text.equals("")) {
    		System.out.println("No trade selected to delete!");
    		return;
    	}
    	
		//devo riaggiungere i giocatori alla mia collection, visto che quando li propongo mi vengono tolti temporaneamente dalla collection
		Trade chosen_trade=retrieve_trade();
		System.out.println(chosen_trade);
		for(String card:chosen_trade.get_card_from()){
			card_collection card_from= CardMongoDriver.search_player_by_name(card);
			System.out.println(card);
			collection.add_card_to_collection(card_from,global.id_user);  //riaggiungo in collection i giocatori che stavo offrendo
		}
		
		//delete
    	String words_arr[] = trade_text.split(" ");
		ObjectId trade_id = new ObjectId(words_arr[words_arr.length-1]);
		System.out.println("Deleting trade with object id: " + trade_id);
		TradeMongoDriver.delete_my_trade(trade_id);
    	
    	TradeMongoDriver.closeConnection();
		
    	//updating user's informations
    	if(chosen_trade.get_card_from().size() > 0) {	//if one or more cards has been offered, the user's collection value will be affected
    		TradeMongoDriver.update_user_collection(true,global.user.getUsername(),chosen_trade.get_card_from().size());
    	}
    			
    	//update user's credits info
    	if(chosen_trade.get_credits() < 0) {	//if a user offered credits, they will be refunded
    		TradeMongoDriver.update_user_credits(true,global.user.getUsername(),(0-chosen_trade.get_credits()));
    	}
    			
    	
		selected_trade.setText("");
    	my_requests_button_onclick(); //refreshing the available trade list
    }
    
    
	public void show_all_button_onclick() {

    	
    	show_trades(TradeMongoDriver.trades_pending(),false);
		TradeMongoDriver.closeConnection();

	}
	
	public void my_requests_button_onclick() {
		
		String my_user = global.user.username;
		show_trades(TradeMongoDriver.search_user(my_user),true);
		TradeMongoDriver.closeConnection();
	}
	
	public void search_button_onaction() {
		
		String user_input = search_card_from.getText();
		String card_input = search_card_to.getText();
		if(!user_input.isEmpty() || !card_input.isEmpty()) { //not searching if the 'search' button is clicked when the text fields are empty
    		show_trades(TradeMongoDriver.search_trade(user_input, card_input),false);
		}
		TradeMongoDriver.closeConnection();
	}
	
	public void show_trades(MongoCursor<Document> result, Boolean status) {
		
		//showing off trades
		ObservableList<String> list = FXCollections.observableArrayList();
    	list.removeAll(list);	//clearing the list
    	
    	
    	while(result.hasNext()) {	
    		Document trade_doc = result.next();
    		String trade_output="";
    		
    		if(status) {
    			String trade_status = trade_doc.get("status").toString();
    			trade_output = trade_output + "??? Status: ";
    			if(trade_status.equals("1")) {
    				trade_output = trade_output + "COMPLETED \n";
    			}
    			else {
    				trade_output = trade_output + "PENDING \n";
    			}
    		}
    		
    		String trade_id = trade_doc.get("_id").toString();
    		String card_from = trade_doc.get("card_from").toString();
    		String card_to = trade_doc.get("card_to").toString();
    		String credits = trade_doc.get("credits").toString();
    		String user_from = trade_doc.getString("user_from");
    		trade_output = trade_output + ">> Players offered: " + card_from + " \n<< Players wanted: " + card_to 
    				+ " \n$$$ Credits: " + credits +" \n--- Trade request made by: " + user_from + " \n%% trade_id: " + trade_id;
    		
    		list.add(trade_output);
    	}
    	trade_list.getItems().clear();
		trade_list.getItems().addAll(list);

		TradeMongoDriver.closeConnection();
	}
	

	public void show_most_present(MouseEvent mouseEvent) {

		String card_trade="";

			try(MongoCursor<Document> cursor= TradeMongoDriver.retrieve_most_present((String) offered_wanted.getValue())){
				while(cursor.hasNext()){
					//System.out.println(cursor.next().toJson());
					//show_trades(cursor);
					Document card=cursor.next();
					card_trade += "" + card.get("_id")+"-->"+card.get("count")+"times\n";
				}
				selected_trade.setText(card_trade); //the trade will show up on the lower Area
			}
			TradeMongoDriver.closeConnection();
	}


	public Trade retrieve_trade() {
				String words_arr[] = selected_trade.getText().split(" ");
				String elem = words_arr[words_arr.length-1];
				Trade trade = TradeMongoDriver.search_trade_byId(elem);
				return trade;  //elem ora contiene l' id del trade
	}
	
	public void accept_trade(MouseEvent mouseEvent) throws NoSuchAlgorithmException{


				Trade chosen_trade=retrieve_trade();

				for(String card:chosen_trade.get_card_from()){
					card_collection card_from= CardMongoDriver.search_player_by_name(card);
					collection.add_card_to_collection(card_from,global.id_user);  //aggiunti all' utente che ha accettato, ovvero quello loggato
						//dalla collection dell' altro tizio non vanno tolti in quanto si sono tolti al momento in cui lui li ha offerti
				}

				for(String card:chosen_trade.get_card_to()){
					card_collection card_to= CardMongoDriver.search_player_by_name(card);
					collection.delete_card_from_collection(card_to); //elimino dalla collection del giocatore che ha accettato, i giocatori richiesti da chi ha generato il trade
					collection.add_card_to_collection(card_to,(RankingMongoDriver.retrieve_user(true,chosen_trade.get_user_from())).get(0).get("_id").toString()); //aggiunti alla collection di quello che aveva proposto il trade
				}

				
				//update user's credits informations
				int total_credits = chosen_trade.get_credits();
				if(total_credits < 0) {	//negative credits value means: credits wanted from the trade owner 
					total_credits = 0-total_credits;
					TradeMongoDriver.update_user_credits(true, chosen_trade.get_user_from(), total_credits);
					TradeMongoDriver.update_user_credits(false, global.user.username, total_credits);
				}
				else {	//positive credits value means: credits offered by the trade owner (credits already removed when trade was created)
					TradeMongoDriver.update_user_credits(true, global.user.username, total_credits);
				}
				
				//update user's collection informations
				if(chosen_trade.get_card_from().size() > 0) { //cards offered > 0: adding cards only to the user that accepted the trade, the removal of cards for the user that has created the trade request has been done after the trade creation
					TradeMongoDriver.update_user_collection(true,global.user.username, chosen_trade.get_card_from().size());
				}
				if(chosen_trade.get_card_to().size() > 0) { //cards wanted > 0: updating both sides of the user's informations
					TradeMongoDriver.update_user_collection(true, chosen_trade.get_user_from(), chosen_trade.get_card_to().size());
					TradeMongoDriver.update_user_collection(false, global.user.username, chosen_trade.get_card_to().size());
				}
				
				//update status trade
				TradeMongoDriver.update_trade(chosen_trade,"status");
				accept_button.setDisable(true);
				delete_button.setDisable(true);
				show_all_button_onclick();
				
	}
	
}
