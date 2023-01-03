package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import org.bson.Document;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.see_user;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
	private Document user_doc;
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		System.out.println("Opening user page...");
		username_field.setText(username+"'s");
		
		//hiding delete button from normal users
		int priv = global.user.get_privilege();
    	if(priv < 2){
    		delete_user.setVisible(false);
    	}
    	
    	search_user();
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
		
		user_doc = see_user.search_user(username);
		if(user_doc == null) { //handling user not found error
			Text t1 = new Text("An error has occurred while searching for the user. Please, exit the page and try again later.");
			t1.setStyle("-fx-font: 30 system;");
			text_flow.getChildren().add(t1);
			return;
		}
		
    	//showing up the info
    	region_field.setText(user_doc.getString("region"));
    	collection_field.setText(user_doc.get("collection").toString());
    	points_field.setText(user_doc.get("points").toString());
    	
    	//TODO adding user profile info (?)
    	Text t1 = new Text("Analytics will be added here (?)");
    	text_flow.getChildren().add(t1);
	}
	
	
	public void click_delete() throws IOException{
		
		see_user.delete_user(username);
		click_back();	//automatically go back to the ranking page
	}
	
}
