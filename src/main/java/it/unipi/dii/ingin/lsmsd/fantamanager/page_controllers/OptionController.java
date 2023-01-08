package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.admin.calculate_matchday;
import it.unipi.dii.ingin.lsmsd.fantamanager.admin.calculate_matchday.*;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.control.TextField;

import static it.unipi.dii.ingin.lsmsd.fantamanager.admin.calculate_matchday.*;
import static it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.player_class.*;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.OptionsMongoDriver;

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
    public void initialize(URL url, ResourceBundle resourceBundle){
        
    	//case of admin logged in: showing the 'calculate matchday' option
    	int livpriv=global.user.get_privilege();
    	if(livpriv>=2){
    		
    		for(int i=1;i<=38;i++){
                matchday_list.getItems().add(i);
            }
            
            calculate_button.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent e) {
                    //System.out.println("QUI INSERIRE COME CALCOLARE LA GIORNATA");
					//qui da player_class
                    //retrieve_matchday((Integer) matchday_list.getValue());
                    //calculate_matchday((Integer) matchday_list.getValue());
					//calculate_player_score((Integer) matchday_list.getValue());

					//qui da admin/calculate_matchday
					retrieve_info_matchday((Integer) matchday_list.getValue());
					calculate_card_score((Integer) matchday_list.getValue());
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
    	//find_user();
    	show_user_info();
    }
    

    public void show_user_info (){
    	
    	username_field.setText(global.user.getUsername());
    	password_field.setText("***********************");
    	region_field.setText(global.user.getRegion());
    	email_field.setText(global.user.getEmail());
    	credits_field.setText(Integer.toString(global.user.getCredits()));
    	collection_field.setText(Integer.toString(global.user.getCollection())); 	
    	points_field.setText(Integer.toString(global.user.getPoints())); 
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
    	String new_value = username_field.getText().toString();
    	try {
    		Boolean find = OptionsMongoDriver.find_duplicate("username",new_value);	
    		if(find) {
    			username_warning.setText("Username already in use!");
    			System.out.println("Username already in use!");
    			return;
    		}
    		
    		Boolean res = OptionsMongoDriver.edit_attribute(global.user.username,"username", new_value);
    		if(res) {
    			System.out.println("Username successfully changed to: " + new_value);
    			global.user.changeUsername(new_value);	//changing the global variable
    			username_warning.setText("Username successfully changed");
    		}
    		else {
    			username_warning.setText("Error! Try again later");
    		}
    		
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
    	show_user_info();	//refresh fields
    }
    
    public void confirm_password_click() {
    	password_field.setEditable(false);
    	password_edit.setVisible(true);
    	password_confirm.setVisible(false);
    	String new_value = password_field.getText().toString();
    	
    	if(new_value.equals("")){	//empty field case
    		System.out.println("Password not changed.");
    		password_warning.setText("Password not changed");
    		show_user_info();
    		return;
    	}
    	
    	try {
    		Boolean res = OptionsMongoDriver.edit_attribute(global.user.username,"password", password_field.getText().toString());
    		if(res) {
        		System.out.println("Password successfully changed to: " + new_value);
        		global.user.changePassword(new_value);	//changing the global variable
        		password_warning.setText("Password changed");
        	}
        	else {
        		username_warning.setText("Error! Try again later.");
        	}
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
    	show_user_info();	//refresh fields
    }
    
    public void confirm_email_click() {
    	email_field.setEditable(false);
    	email_edit.setVisible(true);
    	email_confirm.setVisible(false);
    	String new_value = email_field.getText().toString();
    	try {
    		Boolean find = OptionsMongoDriver.find_duplicate("email",new_value);
    		if(find) {
    			email_warning.setText("Email address already in use!");
    			return;
    		}
    		
    		Boolean res = OptionsMongoDriver.edit_attribute(global.user.username,"email", new_value);
    		if(!res) {
    			email_warning.setText("Invalid email input!");
    		}
    		else {
    			global.user.changeEmail(new_value);	//changing the global variable
    			email_warning.setText("Email address successfully changed!");
    		}
    		
    		
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
    	show_user_info();	//refresh fields
    }
    
    
    public void refresh_warnings() {
    	//refreshing text
    	username_warning.setText("");
    	password_warning.setText("");
    	email_warning.setText("");
    }
}