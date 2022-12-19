package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;


import it.unipi.dii.ingin.lsmsd.fantamanager.trades.trade_class;
import org.bson.Document;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class TradesController implements Initializable{

	@FXML
	private ListView<String>trade_list;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		System.out.println("Opening trades page...");
    	//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Trades");
    	MongoCursor<Document> resultDoc;
    	try {
    		resultDoc = collection.find().iterator(); 
    	} catch (Exception e) {
    		System.out.println("An error has occured while viewing trades!");
    		return;
    	}
    	
    	show_trades(resultDoc);
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
    	trade_list.getItems().addAll(list);
	}
	
    	
}
