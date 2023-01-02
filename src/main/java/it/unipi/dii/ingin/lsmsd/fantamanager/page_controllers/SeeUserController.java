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
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
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

	
	@FXML private TextField region_field;
	@FXML private TextField collection_field;
	@FXML private TextField points_field;
	@FXML private TextFlow text_flow;
	@FXML private Text username_field;
	
	@FXML
	private Button delete_user;
	
	@FXML
	private Parent root;
	
	private String username = RankingController.user_input;
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		System.out.println("Opening user page...");
		username_field.setText(username+"'s");
		search_user();
		
		//hiding delete button from normal users
		int priv = global.user.get_privilege();
    	if(priv < 2){
    		delete_user.setVisible(false);
    	}
    		
		
	}
	
	
	@FXML
    protected void click_back() throws IOException {

    	System.out.println("Returning back to the ranking page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.go_to_ranking(stage);   
    }
	
	@FXML
    protected void click_home() throws IOException {

    	System.out.println("Returning back to the home page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
	
	protected void search_user() {
		
		//connecting to mongoDB 
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
    	
		//searching user
		Document user_doc;
    	try {
    		user_doc = collection.find(Filters.eq("username", username)).first();
    	}catch (Exception e) {
    		System.out.println("Error on viewing card");
    		return;
    	}
    	myClient.close();
    	
    	//showing up the infos
    	region_field.setText(user_doc.getString("region"));
    	collection_field.setText(user_doc.get("collection").toString());
    	points_field.setText(user_doc.get("points").toString());
    	
    	//TODO additional info (?)
    	Text t1 = new Text("Analytics will be added here (?)");
    	text_flow.getChildren().add(t1);
	}
	
	
	public void click_delete() throws IOException{
		
		//connecting to mongoDB 
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
	
		//delete user
		try {
			collection.deleteOne(Filters.eq("username", username));
		}
		catch(Exception e) {
			System.out.println("Error! Cannot delete this user now. Try later");
		}
		myClient.close();
		
		//go back
		System.out.println("User '"+ username + "' successfully deleted.");
		click_back();	//automatically go back to the ranking page
	}
	
}
