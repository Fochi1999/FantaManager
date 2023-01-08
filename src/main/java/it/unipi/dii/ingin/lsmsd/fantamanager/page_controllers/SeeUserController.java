package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import org.bson.Document;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.SeeUserMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.OptionsMongoDriver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;

public class SeeUserController implements Initializable{

	
	@FXML private TextField region_field;
	@FXML private TextField collection_field;
	@FXML private TextField points_field;
	@FXML private TextFlow text_flow;
	@FXML private Text username_field;
	
	@FXML private AnchorPane admin_area;
	@FXML private Text admin_edit_warning;
	@FXML private TextField admin_edit_field;
	
	@FXML private Parent root;
	
	private String username = RankingController.user_input;
	private Document user_doc;
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		System.out.println("Opening user page...");
		username_field.setText(username+"'s");
		
		//hiding delete button from normal users
		int priv = global.user.get_privilege();
    	if(priv < 2){
    		admin_area.setVisible(false);
    		admin_edit_warning.setText("");
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
		
		user_doc = SeeUserMongoDriver.search_user(username);
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
		
		SeeUserMongoDriver.delete_user(username);
		click_back();	//automatically go back to the ranking page
	}
	
	public void edit_username() {
		
		String new_value = admin_edit_field.getText();
		if(new_value.equals("")) {
			admin_edit_warning.setText("Empty username field");
			System.out.println("Empty field!");
			return;
		}
		admin_edit_field.setText("");
		
		try {
    		Boolean find = OptionsMongoDriver.find_duplicate("username",new_value);	
    		if(find) {
    			admin_edit_warning.setText("Username already in use!");
    			System.out.println("Username already in use!");
    			return;
    		}
    		
    		Boolean res = OptionsMongoDriver.edit_attribute(username, "username", new_value);
    		if(res) {
    			System.out.println("Username successfully changed to: " + new_value);
    			global.user.changeUsername(new_value);	//changing the global variable
    			admin_edit_warning.setText("Username successfully changed. An email has been sent to the user for more information about.");
    			username = new_value;
    			username_field.setText(username+"'s");
    		}
    		else {
    			admin_edit_warning.setText("Error! Try again later");
    		}
    		
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
	}
	
}
