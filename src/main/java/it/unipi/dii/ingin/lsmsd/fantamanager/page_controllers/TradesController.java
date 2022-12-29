package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

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
import javafx.scene.control.ListView;
import javafx.fxml.Initializable;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.MultipleSelectionModel;
import javafx.beans.value.ObservableValue;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.collections.*;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import org.bson.conversions.Bson;
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

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		System.out.println("Opening trades page..."); 	
    	show_all_button_onclick();
    	
    	//turning off the buttons
    	delete_button.setDisable(true);
    	accept_button.setDisable(true);
    	
    	
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
               if(!selected_trade.getText().isEmpty()) 
            	   activate_buttons(selected_trade.getText());
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
	}

    @FXML
    protected void click_home() throws IOException {

    	System.out.println("Closing trades page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
    
    
    @FXML
    protected void click_delete() {
    	
    	String trade_text = selected_trade.getText();
    	if(trade_text.equals("")) {
    		System.out.println("No trade selected to delete!");
    		return;
    	}
    	
    	String words_arr[] = trade_text.split(" ");
		ObjectId trade_id = new ObjectId(words_arr[words_arr.length-1]);
		System.out.println("Deleting trade with object id: " + trade_id);
		
		//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Trades");
    	
    	//searching for the trades
    	try {
    		DeleteResult result = collection.deleteOne(Filters.eq("_id",trade_id)); 
    		System.out.println(result);
    	} catch (Exception e) {
    		System.out.println("An error has occured while deleting the trade!");
    		return;
    	}
    	
    	myClient.close();
    	show_all_button_onclick(); //refreshing the available trade list
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
		
		String my_user = global.user.username;
		search_user(my_user);
	}
	
	public void search_button_onaction() {
		
		String user_input = search_card_from.getText();
		String card_input = search_card_to.getText();
		if(!user_input.isEmpty() || !card_input.isEmpty()) { //not searching if the 'search' button is clicked when the text fields are empty
    		search_trade(user_input, card_input);
		}
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
	}
	
    public void search_trade(String from_input, String to_input) {
    	
    	System.out.println("Searching trades -> offered: "+ from_input + " // wanted: " + to_input);
    	
    	//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Trades");
    	MongoCursor<Document> resultDoc;
		
    	//preparing bsons
    	Pattern pattern0 = Pattern.compile(from_input, Pattern.CASE_INSENSITIVE);
    	Bson card_from_equal = Filters.regex("player_from", pattern0);	
    	
    	Pattern pattern1 = Pattern.compile(to_input, Pattern.CASE_INSENSITIVE);
    	Bson card_to_equal = Filters.regex("player_to", pattern1);
    	
    	
    	//searching for the trades
    	try {
    		if (!from_input.isEmpty() && !to_input.isEmpty()) { 	//both inputs
    			resultDoc = collection.find(Filters.and(card_from_equal,card_to_equal)).iterator(); 
    		}
    		else if(!from_input.isEmpty()) { 						//only user input
    			resultDoc = collection.find(card_from_equal).iterator(); 
    		}
    		else if(!to_input.isEmpty()) { 						//only card input
    			resultDoc = collection.find(card_to_equal).iterator(); 
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
    
    
    private void search_user(String my_user) {
    	
    	System.out.println("Searching trades made by: " + my_user);
    	
    	//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Trades");
    	MongoCursor<Document> resultDoc;
		
    	//preparing bson
    	Bson user_equal = Filters.eq("user_from", my_user);	
    	
    	//searching for the trades
    	try {
    		resultDoc = collection.find(user_equal).iterator(); 
    		
    	} catch (Exception e) {
    		System.out.println("An error has occured while viewing trades!");
    		return;
    	}
		
    	//print
    	show_trades(resultDoc);
    	myClient.close();	
    }
    
}
