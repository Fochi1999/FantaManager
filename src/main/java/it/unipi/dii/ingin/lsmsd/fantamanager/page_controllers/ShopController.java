package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import com.mongodb.client.result.*;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;

public class ShopController implements Initializable {

	@FXML
	private Button see_card;
	
	@FXML
	private ListView<String> card_list;
	
	@FXML
	private Button search_button;
	
	@FXML
	private TextField text_field;

	@FXML
	private TextField selected_card;
	
	@FXML
    private Parent root;
	
	static String card_id_input;
	
	
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		//disabling the button
		see_card.setDisable(true);
		
		//handling the event: clicking on a card from the list
        MultipleSelectionModel<String> card = card_list.getSelectionModel();

        card.selectedItemProperty().addListener(new ChangeListener<String>() {
           public void changed(ObservableValue<? extends String> changed, String oldVal, String newVal) {

        	  see_card.setDisable(true);
              String selItems = "";
              ObservableList<String> selected = card_list.getSelectionModel().getSelectedItems();

               for (int i = 0; i < selected.size(); i++) {
                  selItems += "" + selected.get(i); 
               }
               
               selected_card.setText(selItems); //the card will show up on the lower Area
               
               if(!selected_card.getText().isEmpty()) {
            	   see_card.setDisable(false);
               }
            	   
           }
        });
		
        //showing up cards
		retrieve_cards();
	}
	
	
	@FXML
    protected void click_home() throws IOException {

    	System.out.println("Closing shop page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
	
	
	public void retrieve_cards() {
		
		String cards_input = text_field.getText();
		System.out.println("Searching for: "+ cards_input);
		
		//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Player_Java_Final");
    	MongoCursor<Document> resultDoc;
		
    	//blank search field
    	if(cards_input.equals("")) {
    		
    		try {
    			resultDoc = collection.find().iterator();
    		}
    		catch(Exception e) {
    			System.out.println("Error on search.");
    			return;
    		}  		
    	}
    	
    	else {
    		//filter
    		Pattern pattern = Pattern.compile(cards_input, Pattern.CASE_INSENSITIVE);
        	Bson filter = Filters.regex("fullname", pattern);	
        	
    		try {
    			resultDoc = collection.find(filter).iterator();
    		}
    		catch(Exception e) {
    			System.out.println("Error on search.");
    			return;
    		}  	
    	}
		
    	show_cards(resultDoc);
    	myClient.close();
    	
	}
	
	public void show_cards(MongoCursor<Document> result) {
		
		//showing off trades
		ObservableList<String> list = FXCollections.observableArrayList();
    	list.removeAll(list);	//clearing the list
    	
    	
    	while(result.hasNext()) {	
    		Document trade_doc = result.next();
    		String card_id = trade_doc.get("_id").toString();
    		String card_fullname = trade_doc.get("fullname").toString();
    		String card_credits = trade_doc.get("credits").toString();
    		String card_team = trade_doc.getString("team");
    		String card_role = trade_doc.getString("position");
    		String trade_output = "Card: " + card_fullname + " /// Cost: " + card_credits +
    				" /// Role: " + card_role + " /// Team: " + card_team + " // id: " + card_id;
    		list.add(trade_output);
    	}
    	card_list.getItems().clear();
		card_list.getItems().addAll(list);
	}
	
	public void click_see_card() throws IOException {
		
		//retrieving card id
		String full_text[] = selected_card.getText().split(" ");
		card_id_input = full_text[full_text.length-1];
		System.out.println("Viewing card with object id: " + card_id_input);
		
		Stage stage = (Stage)root.getScene().getWindow();
		view_card(stage);
	}
	
	 public static void view_card(Stage stage) throws IOException {
		 	
		 	System.out.println("Opening card page...");
		 	FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("see_card_page.fxml"));
	        Scene scene = new Scene(fxmlLoader.load());
	        stage.setTitle("Card info");
	        stage.setScene(scene);
	        stage.show();
	    	
	    }
	
}
