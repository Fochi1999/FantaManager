package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.collections.*;
import javafx.scene.control.TextField;
import org.bson.conversions.Bson;

public class TradesController implements Initializable{

	@FXML
	private ListView<String>trade_list;
    
	@FXML 
	private TextField search_user;
	
	@FXML
	private TextField search_card;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		System.out.println("Opening trades page..."); 	
    	show_all_button_onclick();
	}
	
	
	public void show_all_button_onclick() {
		
		//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Trades");
    	MongoCursor<Document> resultDoc;
		
    	//searching for the trades
    	try {
    		resultDoc = collection.find().iterator(); 
    	} catch (Exception e) {
    		System.out.println("An error has occured while viewing trades!");
    		return;
    	}
    	
    	show_trades(resultDoc);
    	myClient.close();
	}
	
	public void my_requests_button_onclick() {
		
		String my_user = "user1";
		search_user(my_user,"");
	}
	
	public void search_button_onaction() {
		
		String user_input = search_user.getText();
		String card_input = search_card.getText();
		System.out.println(card_input);
		if(!user_input.isEmpty() || !card_input.isEmpty()) { //not searching if the 'search' button is clicked when the text fields are empty
    		search_user(user_input, card_input);
		}
	}
	
	public void show_trades(MongoCursor<Document> result) {
		
		//showing off trades
		ObservableList<String> list = FXCollections.observableArrayList();
    	list.removeAll(list);	//clearing the list
    	
    	
    	while(result.hasNext()) {	
    		Document trade_doc = result.next();
    		String player_from = trade_doc.get("player_from").toString();
    		String player_to = trade_doc.get("player_to").toString();
    		String credits = trade_doc.get("credits").toString();
    		String user_from = trade_doc.getString("user_from");
    		String trade_output = ">> Players offered: " + player_from + " /// <<  Players wanted: " + player_to +
    				" /// $$$ Credits: " + credits +" - Trade request made by: "+ user_from;
    		list.add(trade_output);
    	}
    	trade_list.getItems().clear();
		trade_list.getItems().addAll(list);
	}
	
    public void search_user(String user_input, String card_input) {
    	
    	System.out.println("Searching for user: "+ user_input +" and card: " + card_input + " trades...");
    	
    	//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Trades");
    	MongoCursor<Document> resultDoc;
		
    	//preparing bsons
    	Pattern pattern0 = Pattern.compile(user_input, Pattern.CASE_INSENSITIVE);
    	Bson user_equal = Filters.regex("user_from", pattern0);	//search user
    	
    	Pattern pattern1 = Pattern.compile(card_input, Pattern.CASE_INSENSITIVE);
    	Bson filter1 = Filters.regex("player_from", pattern1);
    	Bson filter2 = Filters.regex("player_to", pattern1);
    	Bson card_equal = Filters.or(filter1, filter2); //search card
    	
    	
    	//searching for the trades
    	try {
    		if (!user_input.isEmpty() && !card_input.isEmpty()) { 	//both inputs
    			resultDoc = collection.find(Filters.and(user_equal,card_equal)).iterator(); 
    		}
    		else if(!user_input.isEmpty()) { 						//only user input
    			resultDoc = collection.find(user_equal).iterator(); 
    		}
    		else if(!card_input.isEmpty()) { 						//only card input
    			resultDoc = collection.find(card_equal).iterator(); 
    		}
    		else {													//no inputs
    			System.out.println("No elements to search....");
    			return;
    		}
    		
    	} catch (Exception e) {
    		System.out.println("An error has occured while viewing trades!");
    		return;
    	}
		
    	//print
    	show_trades(resultDoc);
    	myClient.close();
    	
    }
}
