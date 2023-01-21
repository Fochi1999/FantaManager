package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collectionRedisDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.CardMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.login;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoadingScreenController implements Initializable{

	@FXML
    private Text loading_text;
    @FXML
    private Text wait_text; 
    @FXML
    private Text err_text;
    @FXML
    private ImageView wait_gif;
    
	@FXML private Button logout_button;
	@FXML private Button retry_button;
	@FXML private Button enter_button;
    
    @FXML
    private Parent root;
	
	 @Override
	    public void initialize(URL url, ResourceBundle resourceBundle){
	    	
	    	//hiding 'loading' items
	    	err_text.setVisible(false);
	    	wait_text.setVisible(false);
	    	wait_gif.setVisible(false);
	    	
	    	logout_button.setDisable(true);
	    	retry_button.setDisable(true);
	    	enter_button.setDisable(true);
	    	
	    	if(global.owned_cards_list == null || global.full_card_list == null) {
	    			load_collections();
	    		}
	    	
	    }
	 
	
	 public void load_collections(){
	    	new Thread(() -> {
	    		wait_text.setVisible(true);
	        	wait_gif.setVisible(true);
	        	System.out.println("Reading card collection..");
	        	loading_text.setText("Loading card collection...");
	        	try{
	        		global.owned_cards_list = collectionRedisDriver.load_collection(global.id_user);
	        	}		
	        	catch(Exception e){
	        		wait_text.setText("Error");
	        		loading_text.setText("Error");
	            	wait_gif.setVisible(false);
	        		err_text.setVisible(true);
	        		logout_button.setDisable(false);
	    	    	retry_button.setDisable(false);
	        		return;
	        	}
	        	
	        	if(global.full_card_list == null) {
	        		System.out.println("Reading full card list..");
	        		loading_text.setText("Loading full card list...");
	            	try {
	            		global.full_card_list = CardMongoDriver.retrieve_cards("");
	            	}
	            	catch(Exception e){
	            		wait_text.setText("Error");
	            		loading_text.setText("Error");
	                	wait_gif.setVisible(false);
	            		err_text.setVisible(true);
	            		logout_button.setDisable(false);
		    	    	retry_button.setDisable(false);
	            		return;
	            	}
	            }
	        	wait_text.setVisible(false);
	        	wait_gif.setVisible(false);
	        	loading_text.setText("Load complete! Press enter to continue");
	        	System.out.println("Load complete.");
	        	
	        	logout_button.setDisable(false);
		    	enter_button.setDisable(false);
	        	
	    	}).start();	
	    }  
	
	 public void logout() throws IOException{
	    	login.logout();
	    	Stage stage = (Stage)root.getScene().getWindow();
	    	util_controller.go_to_login(stage);
	    }
	 
	 public void enter() throws IOException{
	    	Stage stage = (Stage)root.getScene().getWindow();
	    	util_controller.back_to_home(stage);
	 }
	 
	 public void retry() throws IOException{
	    	Stage stage = (Stage)root.getScene().getWindow();
	    	util_controller.go_to_loading(stage);
	 }
	 
}
