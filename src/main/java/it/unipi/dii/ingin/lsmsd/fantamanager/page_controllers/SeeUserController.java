package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class SeeUserController implements Initializable{

	
	@FXML
	private TextField user_id_field;

	@FXML
	private TextFlow text_flow;

	@FXML
	private Parent root;
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		System.out.println("Opening user page...");
		user_id_field.setText(RankingController.user_id_input);	//retrieving the user id
		search_user();
	}
	
	
	@FXML
    protected void click_back() throws IOException {

    	System.out.println("Returning back to the ranking page...");
        Stage stage = (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("ranking_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Ranking page");
        stage.setScene(scene);
        stage.show();
        
    }
	
	protected void search_user() {
		
		ObjectId user_id = new ObjectId(user_id_field.getText().toString());
		
		//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Users");
    	Document resultDoc;
    	try {
    		resultDoc = collection.find(Filters.eq("_id", user_id)).first();
    	}catch (Exception e) {
    		System.out.println("Error on viewing card");
    		return;
    	}
    	myClient.close();
    	
    	System.out.println(resultDoc);
    	
    	//showing up the infos
    	//TODO
    	Text t1 = new Text(resultDoc.getString("username"));
    	text_flow.getChildren().add(t1);
	}
	
	
}
