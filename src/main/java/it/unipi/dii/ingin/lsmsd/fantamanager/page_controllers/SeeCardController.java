package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;


public class SeeCardController implements Initializable{

	@FXML
	private TextField card_id_field;

	@FXML
	private TextFlow text_flow;

	@FXML
	private Text buy_card_text;

	@FXML
	private Button buy_card;
	
	@FXML
	private Parent root;
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		card_id_field.setText(ShopController.card_id_input);	//retrieving the card id
		buy_card.setDisable(true);								//disabling the buy button
		buy_card_text.setText(""); 								//setting to empty the error message string
		search_card();
	}
	
	
	@FXML
    protected void click_back() throws IOException {

    	System.out.println("Returning back to the shop page...");
        Stage stage = (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("shop_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Store page");
        stage.setScene(scene);
        stage.show();
        
    }
	
	public void search_card() {
		
		ObjectId card_id = new ObjectId(card_id_field.getText().toString());
		
		//connecting to mongoDB 
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.CARDS_COLLECTION_NAME);
    	Document resultDoc;
    	try {
    		resultDoc = collection.find(Filters.eq("_id", card_id)).first();
    	}catch (Exception e) {
    		System.out.println("Error on viewing card");
    		return;
    	}
    	myClient.close();
    	
    	//showing up the infos
    	//TODO
    	Text t1 = new Text(resultDoc.getString("fullname"));
    	text_flow.getChildren().add(t1);
    	
	}
	
	
	
	
}
