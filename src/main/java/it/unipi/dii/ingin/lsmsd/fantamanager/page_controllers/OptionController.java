package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.hash;
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
import java.security.NoSuchAlgorithmException;
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
    @FXML private TextField points_field;
    
    @FXML private Text username_warning;
    @FXML private Text password_warning;
    @FXML private Text email_warning;
    
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
//// Updated upstream
		int livpriv=global.user.get_privilege();

		//int livpriv=global.user.liv_priv;

    	if(livpriv>=2){
    		
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

    	//hiding buttons and warnings
    	username_confirm.setVisible(false);
    	password_confirm.setVisible(false);
    	email_confirm.setVisible(false);
    	
    	username_warning.setText("");
    	password_warning.setText("");
    	email_warning.setText("");

    	//finding the user
    	find_user();
    	
    }
    
    //TODO decidere se va bene questo approccio
    protected void find_user() {
    
    	//connecting to mongoDB 
    	MongoClient myClient = MongoClients.create(global.MONGO_URI);
    	MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
    	MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
    	Document resultDoc;
    	
    	//searching user
    	Bson filter = Filters.eq("username", global.user.username);
    	try {
    		resultDoc = collection.find(filter).first();
    	}
    	catch(Exception e) {
    		System.out.println("Error on searching user: " + global.user.username);
    		return;
    	}
    	
    	//showing info
    	myClient.close();
    	try {
    		show_user_info(resultDoc);
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
    }
    
    
    
    protected void show_user_info (Document user_input) throws NoSuchAlgorithmException{
    	
    	username_field.setText(user_input.getString("username"));
    	password_field.setText("***********************");
    	region_field.setText(user_input.getString("region"));
    	email_field.setText(user_input.getString("email"));
    	credits_field.setText(user_input.get("credits").toString());
    	collection_field.setText(user_input.get("collection").toString()); 	
    	points_field.setText(user_input.get("points").toString()); 
    }
    
    
    
    //edit buttons
    public void edit_username_click() {
    	refresh_warnings();
    	
    	username_field.setEditable(true);
    	username_edit.setVisible(false);
    	username_confirm.setVisible(true);
    }
    
    public void edit_password_click() {
    	refresh_warnings();
    	
    	password_field.setEditable(true);
    	password_field.setText("");
    	password_edit.setVisible(false);
    	password_confirm.setVisible(true);
    }
    
    public void edit_email_click() {
    	refresh_warnings();
    	
    	email_field.setEditable(true);
    	email_edit.setVisible(false);
    	email_confirm.setVisible(true);
    }
    
    
    
    //confirm buttons
    public void confirm_username_click() {
    	username_field.setEditable(false);
    	username_edit.setVisible(true);
    	username_confirm.setVisible(false);
    	try {
    		String new_value = username_field.getText().toString();
    		Boolean find = profile_page.find_duplicate("username",new_value);	
    		if(find) {
    			username_warning.setText("Username already in use!");
    			System.out.println("Username already in use!");
    			return;
    		}
    		
    		Boolean res = profile_page.edit_attribute("username", new_value);
    		if(res) {
    			System.out.println("Username successfully changed to: " + new_value);
    			global.user.username = new_value;	//changing the global variable
    			username_warning.setText("Username successfully changed");
    		}
    		else {
    			username_warning.setText("Error! Try again later");
    		}
    		
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
    	find_user();	//refresh
    }
    
    public void confirm_password_click() {
    	password_field.setEditable(false);
    	password_edit.setVisible(true);
    	password_confirm.setVisible(false);
    	String new_value = password_field.getText().toString();
    	try {
    		Boolean res = profile_page.edit_attribute("password", password_field.getText().toString());
    		if(res) {
        		System.out.println("Password successfully changed to: " + new_value);
        		password_warning.setText("Password changed");
        	}
        	else {
        		username_warning.setText("Error! Try again later.");
        	}
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
    	find_user();	//refresh
    }
    
    public void confirm_email_click() {
    	email_field.setEditable(false);
    	email_edit.setVisible(true);
    	email_confirm.setVisible(false);
    	String new_value = email_field.getText().toString();
    	try {
    		Boolean find = profile_page.find_duplicate("email",new_value);
    		if(find) {
    			email_warning.setText("Email address already in use!");
    			return;
    		}
    		
    		Boolean res = profile_page.edit_attribute("email", new_value);
    		if(!res) {
    			email_warning.setText("Invalid email input!");
    		}
    		else {
    			email_warning.setText("Email address successfully changed!");
    		}
    		
    		
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
    	find_user();	//refresh
    }
    
    
    public void refresh_warnings() {
    	//refreshing text
    	username_warning.setText("");
    	password_warning.setText("");
    	email_warning.setText("");
    }
}