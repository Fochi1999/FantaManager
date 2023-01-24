package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.users;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collectionRedisDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.formation.SeeUserFormationController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import org.bson.Document;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.SeeUserMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;


public class SeeUserController implements Initializable{

	@FXML private ChoiceBox choise_box_formation;
	@FXML private Button see_formation_button;
	@FXML private Pane confirm_delete;
	@FXML private Pane cancel_delete;
	
	@FXML private TextField region_field;
	@FXML private TextField points_field;
	@FXML private TextField email_field;

	@FXML private Text username_field;
	@FXML private Text err_text;
	
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
		hide_delete_buttons();
		err_text.setVisible(false);
		
		//hiding delete button from normal users
		int priv = global.user.get_privilege();
    	if(priv < 2){
    		admin_area.setVisible(false);
    		admin_edit_warning.setText("");
    	}
    	
    	search_user();
		ObservableList<Integer> availableChoices = FXCollections.observableArrayList();

		for(int i=1;i<=38;i++){
			availableChoices.add(i);
		}
		choise_box_formation.setItems(availableChoices);
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
			err_text.setVisible(true);
			return;
		}
		
    	//showing up the info
    	region_field.setText(user_doc.getString("region"));
    	points_field.setText(user_doc.get("points").toString());
    	email_field.setText(user_doc.get("email").toString());
    	System.out.println(user_doc.get("_id").toString());
    	
		//ArrayList<card_collection> collection_user_selected= collectionRedisDriver.load_collection(user_doc.get("_id").toString());
		//show_collection(collection_user_selected);
	}
/*
	private void show_collection(ArrayList<card_collection> collection_user_selected) {
		//showing off trades
		ObservableList<String> list = FXCollections.observableArrayList();
		list.removeAll(list);	//clearing the list


		for(card_collection card: collection_user_selected){

			String card_output="";

			card_output = card_output + card.get_name() + " - Team: "+card.get_team()+ " - Role: "+card.get_position() +
					" - Quantity: "+card.get_quantity() + " - ID: " + card.get_id();

			list.add(card_output);
		}
		collection_list.getItems().clear();
		collection_list.getItems().addAll(list);

	}
*/

	public void click_delete() throws IOException{
		
		if(SeeUserMongoDriver.delete_user(username, user_doc.get("_id").toString()))
			click_back();	//automatically go back to the ranking page
		else 
			admin_edit_warning.setText("Network error! Try again later");
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
    			admin_edit_warning.setText("Username successfully changed. An email has been sent to the user for more information about.");
    			username_field.setText(new_value+"'s");
    			username = new_value;
    			search_user();
    		}
    		else {
    			admin_edit_warning.setText("Network error! Try again later");
    		}
    		
    	}
    	catch(Exception e) {
    		System.out.println(e);
    	}
	}
	
	@FXML
	protected void click_see_formation() throws IOException {
		try {
			System.out.println("Going to user formations...");
			SeeUserFormationController.matchday = (int) choise_box_formation.getSelectionModel().getSelectedItem();
			SeeUserFormationController.user_document = user_doc;
			Stage stage = (Stage) root.getScene().getWindow();
			FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("see_formation_user_page.fxml"));
			Scene scene = new Scene(fxmlLoader.load());
			stage.setTitle("Formation");
			stage.setScene(scene);
			stage.show();
		}catch(Exception e){
			System.out.println("Impossibile caricare formazione");
		}
	}
	
	public void show_delete_buttons() {
		confirm_delete.setVisible(true);
		cancel_delete.setVisible(true);
		admin_edit_warning.setText("Are you sure?");
	}
	
	public void hide_delete_buttons() {
		confirm_delete.setVisible(false);
		cancel_delete.setVisible(false);
		admin_edit_warning.setText("");
	}
}
