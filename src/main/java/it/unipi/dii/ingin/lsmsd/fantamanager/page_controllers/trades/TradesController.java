package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.trades;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collectionRedisDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.Trade;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.TradeMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.RankingMongoDriver;

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
import java.util.ArrayList;
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

			ArrayList<Document> cards=chosen_trade.get_card_to();
			
			for(Document card:cards){
				card_collection card_to= new card_collection((int)card.get("card_id"),(String)card.get("card_name"),1,(String)card.get("card_team"),(String)card.get("card_position"));
				if(!collectionRedisDriver.presence_card(card_to,global.id_user))
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
    	Trade chosen_trade=retrieve_trade();
    	
    	//MongoDB
    	String words_arr[] = trade_text.split(" ");
    	ObjectId trade_id = new ObjectId(words_arr[words_arr.length-1]);
    	try{
    		
    		System.out.println("Deleting trade with object id: " + trade_id);
    		TradeMongoDriver.delete_my_trade(trade_id);
        	TradeMongoDriver.closeConnection();

        	//update user's credits info
        	if(chosen_trade.get_credits() < 0) {	//if a user offered credits, they will be refunded
        		OptionsMongoDriver.update_user_credits(true,global.user.getUsername(),(0-chosen_trade.get_credits()));
        	}
    		System.out.print("MongoDB...OK\t");
    	}
    	catch(Exception e){	//handling error
    		selected_trade.setText("Network error! Try again later");
    		return;
    	}
    			
    	//REDIS
    	try {
    		//return cards into a user's collection (cards were removed after the creation of the trade)
    		for(Document card:chosen_trade.get_card_from()){
    			card_collection card_from= new card_collection((int)card.get("card_id"),(String)card.get("card_name"),1,(String)card.get("card_team"),(String)card.get("card_position"));
    			collectionRedisDriver.add_card_to_collection(card_from,global.id_user);  //riaggiungo in collection i giocatori che stavo offrendo
    		}
    		System.out.print("Redis...OK\t\n");
    	}
    	catch(Exception e){ //handling error
    		selected_trade.setText("Network error! Try again later");
    				
    		//revert credit value
        	if(chosen_trade.get_credits() < 0) {	
        		OptionsMongoDriver.update_user_credits(false,global.user.getUsername(),(0-chosen_trade.get_credits()));
        	}
        	
    		//reinsert the newly delete trade
    		TradeMongoDriver.create_new_trade(chosen_trade);
    		System.out.println("Trade reinserted due to network error with key-valueDB");
    		return;
    	}
    				  	
		selected_trade.setText("");
    	my_requests_button_onclick(); //refreshing the available trade list
    }
    
    
	public void show_all_button_onclick() {

		//resets input values
    	search_card_from.setText("");
    	search_card_to.setText("");
    	offered_wanted.setValue(null);
    	
    	
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
    		ArrayList<Document> card_from = (ArrayList<Document>) trade_doc.get("card_from");
			//System.out.println(card_from.get(0).toString());
			String card_from_string=convert_array_trade(card_from);
			ArrayList<Document> card_to = (ArrayList<Document>) trade_doc.get("card_to");
			String card_to_string=convert_array_trade(card_to);
    		String credits = trade_doc.get("credits").toString();
    		String user_from = trade_doc.getString("user_from");
    		trade_output = trade_output + ">> Players offered: " + card_from_string + " \n<< Players wanted: " + card_to_string
    				+ " \n$$$ Credits: " + credits +" \n--- Trade request made by: " + user_from + " \n%% trade_id: " + trade_id;
    		
    		list.add(trade_output);
    	}
    	trade_list.getItems().clear();
		trade_list.getItems().addAll(list);

		TradeMongoDriver.closeConnection();
	}

	private String convert_array_trade(ArrayList<Document> cards) {
			String cards_trade = "";
			for(int i=0;i<cards.size();i++){
				String card_name=(cards.get(i).getString("card_name"));
				String card_team=(cards.get(i).getString("card_team"));
				
				cards_trade+= card_name + " ("+card_team+")";
				if(i<cards.size()-1) {
					cards_trade+=" - ";
				}
			}
			return cards_trade;
	}


	public void show_most_present(MouseEvent mouseEvent) {

		String card_trade="";

			try(MongoCursor<Document> cursor= TradeMongoDriver.retrieve_most_present((String) offered_wanted.getValue())){
				while(cursor.hasNext()){
					Document card=cursor.next();
					Document card_value = (Document) card.get("_id");
					card_trade += "" + card_value.get("card_name").toString()+" ("+card_value.get("card_team").toString()+")"+" --> "+card.get("count")+"times\n";
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
				int total_credits = chosen_trade.get_credits();
				//CHANGES ON DBs
				
				//MONGODB
				try {
					//update user's credits informations
					if(total_credits < 0) {	//negative credits value means: credits wanted from the trade owner 
						total_credits = 0-total_credits;
						OptionsMongoDriver.update_user_credits(true, chosen_trade.get_user_from(), total_credits);
						OptionsMongoDriver.update_user_credits(false, global.user.username, total_credits);
					}
					else {	//positive credits value means: credits offered by the trade owner (credits already removed when trade was created)
						OptionsMongoDriver.update_user_credits(true, global.user.username, total_credits);
					}
					total_credits = 0-total_credits;
					//update status trade
					TradeMongoDriver.update_trade(chosen_trade,"status");
					
					System.out.print("MongoDB...OK\t");
				}
				catch(Exception e) {
					selected_trade.setText("Network error! Try again later");
					return;
				}
				
				//REDIS
				try {
					for(Document card:chosen_trade.get_card_from()){
						card_collection card_from= new card_collection((int)card.get("card_id"),(String)card.get("card_name"),1,(String)card.get("card_team"),(String)card.get("card_position"));
						collectionRedisDriver.add_card_to_collection(card_from,global.id_user);  //aggiunti all' utente che ha accettato, ovvero quello loggato
						//dalla collection dell' altro tizio non vanno tolti in quanto si sono tolti al momento in cui lui li ha offerti
					}

					for(Document card:chosen_trade.get_card_to()){
						card_collection card_to= new card_collection((int)card.get("card_id"),(String)card.get("card_name"),1,(String)card.get("card_team"),(String)card.get("card_position"));
						collectionRedisDriver.delete_card_from_collection(card_to); //elimino dalla collection del giocatore che ha accettato, l'utente loggato, i giocatori richiesti da chi ha generato il trade
						collectionRedisDriver.add_card_to_collection(card_to,(RankingMongoDriver.retrieve_user(true,chosen_trade.get_user_from())).get(0).get("_id").toString()); //aggiunti alla collection di quello che aveva proposto il trade
					}
				}
				catch(Exception e) {
					
					//reverting mongoDB changes:
					//update user's credits informations
					if(total_credits < 0) {	//negative credits value means: credits wanted from the trade owner 
						total_credits = 0-total_credits;
						OptionsMongoDriver.update_user_credits(false, chosen_trade.get_user_from(), total_credits);
						OptionsMongoDriver.update_user_credits(true, global.user.username, total_credits);
					}
					else {	//positive credits value means: credits offered by the trade owner (credits already removed when trade was created)
						OptionsMongoDriver.update_user_credits(false, global.user.username, total_credits);
					}
					
					//revert trade status to pending
					TradeMongoDriver.revert_trade(chosen_trade,"status");
					
					selected_trade.setText("Network error! Try again later");
					return;
				}
				
				accept_button.setDisable(true);
				delete_button.setDisable(true);
				show_all_button_onclick();
				
	}
	
}
