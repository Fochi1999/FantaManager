package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import javafx.scene.control.TextField;

import static it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.player_class.*;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.profile_page;

public class OptionController implements Initializable {
	


    @FXML
    private Parent root;
    
    @FXML
    private HBox matchday_adm;
    
    @FXML
    private ChoiceBox matchday_list;
    
    @FXML
    private Button calculate_button;
    
    @FXML
    private HBox admin_hbox;
    
    @FXML private TextField username_field;
    @FXML private TextField password_field;
    @FXML private TextField region_field;
    @FXML private TextField email_field;
    @FXML private TextField credits_field;
    @FXML private TextField collection_field;
    
    @FXML private ImageView username_edit;
    @FXML private ImageView username_confirm;
    
    @FXML private ImageView password_edit;
    @FXML private ImageView password_confirm;
    
    @FXML private ImageView email_edit;
    @FXML private ImageView email_confirm;
    
    
    @FXML
    protected void click_home() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    	//case of admin logged in: showing the 'calculate matchday' option
    	if(global.liv_priv>=2){
    		
    		for(int i=1;i<=38;i++){
                matchday_list.getItems().add(i);
            }
            
            calculate_button.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    //System.out.println("QUI INSERIRE COME CALCOLARE LA GIORNATA");
                    retrieve_info_matchday((Integer) matchday_list.getValue());
                    calculate_matchday((Integer) matchday_list.getValue());
                }
            });
        }
    	
    	//normal user logged: hiding the options above
        else {
        	admin_hbox.setVisible(false);
        }

    	//hiding buttons
    	username_confirm.setVisible(false);
    	password_confirm.setVisible(false);
    	email_confirm.setVisible(false);

    	//finding the user
    	find_user();
    	
    }
    
    
    protected void find_user() {
    
    	//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Users");
    	Document resultDoc;
    	
    	//searching user
    	Bson filter = Filters.eq("username", global.nick);
    	try {
    		resultDoc = collection.find(filter).first();
    	}
    	catch(Exception e) {
    		System.out.println("Error on searching user: " + global.nick);
    		return;
    	}
    	
    	//showing info
    	myClient.close();
    	show_user_info(resultDoc);
    }
    
    
    
    protected void show_user_info(Document user){
    	
    	username_field.setText(user.getString("username"));
    	password_field.setText(user.getString("password"));
    	region_field.setText(user.getString("region"));
    	email_field.setText(user.getString("email"));
    	credits_field.setText(user.get("credits").toString());
    	collection_field.setText(user.get("collection").toString()); 	
    }
    
    
    
    //edit buttons
    public void edit_username_click() {
    	username_field.setEditable(true);
    	username_edit.setVisible(false);
    	username_confirm.setVisible(true);
    }
    
    public void edit_password_click() {
    	password_field.setEditable(true);
    	password_edit.setVisible(false);
    	password_confirm.setVisible(true);
    }
    
    public void edit_email_click() {
    	email_field.setEditable(true);
    	email_edit.setVisible(false);
    	email_confirm.setVisible(true);
    }
    
    
    
    //confirm buttons
    public void confirm_username_click() {
    	username_field.setEditable(false);
    	username_edit.setVisible(true);
    	username_confirm.setVisible(false);
    	String new_value = username_field.getText().toString();
    	Boolean res = profile_page.edit_attribute("username", new_value);
    	if(res) {
    		System.out.println("Username successfully changed to: " + new_value);
    	}
    	global.nick = new_value;	//changing the global variable
    	find_user();	//refresh
    }
    
    public void confirm_password_click() {
    	password_field.setEditable(false);
    	password_edit.setVisible(true);
    	password_confirm.setVisible(false);
    	String new_value = password_field.getText().toString();
    	Boolean res = profile_page.edit_attribute("password", password_field.getText().toString());
    	if(res) {
    		System.out.println("Password successfully changed to: " + new_value);
    	}
    	find_user();	//refresh
    }
    
    public void confirm_email_click() {
    	email_field.setEditable(false);
    	email_edit.setVisible(true);
    	email_confirm.setVisible(false);
    	String new_value = email_field.getText().toString();
    	Boolean res = profile_page.edit_attribute("email", new_value);
    	if(res) {
    		System.out.println("Email successfully changed to: " + new_value);
    	}
    	find_user();	//refresh
    }
}