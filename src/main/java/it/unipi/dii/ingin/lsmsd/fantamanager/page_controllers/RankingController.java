package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.input.MouseEvent;


import org.bson.Document;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.RankingMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.utilities;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ChoiceBox;
import java.util.ArrayList;

public class RankingController implements Initializable{

	@FXML
	private ChoiceBox search_field_region;
	
	@FXML
	private Button search_button_region;
	
	@FXML
	private Button view_profile;
	
	@FXML
	private ListView<String> user_list;
	
	@FXML
	private Button search_button;
	
	@FXML
	private TextField search_field;

	@FXML
	private TextField selected_user;
	
	@FXML
    private Parent root;
	
	static String user_input;
	
	private ArrayList<Document> users_doc;
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		System.out.println("Opening ranking page..."); 	
		user_input = "";
		
		//disabling the button
		view_profile.setDisable(true);
		
		//add region list to his field
    	for(int i=0; i<utilities.regionList.length-1; i++) {
    		search_field_region.getItems().add(utilities.regionList[i]);
    	}
		
		//handling the event: clicking on a user from the list
        MultipleSelectionModel<String> card = user_list.getSelectionModel();

        card.selectedItemProperty().addListener(new ChangeListener<String>() {
        	public void changed(ObservableValue<? extends String> changed, String oldVal, String newVal) {

        	   view_profile.setDisable(true);
              	String selItems = "";
              	ObservableList<String> selected = user_list.getSelectionModel().getSelectedItems();

               	for (int i = 0; i < selected.size(); i++) {
            	   selItems += "" + selected.get(i); 
               	}
               	
               	if(!selItems.equals("")) {
               		String full_text[] = selItems.split(" ");
               		selected_user.setText(full_text[0] + " - Points: " + full_text[3]); //index: (0-3) without position - with position(1-4)
               	}
               	
       			if(!selected_user.getText().isEmpty()) {
       				view_profile.setDisable(false);
       			}
            	   
        	}
        });
		
        //showing up users
		view_global_rank();
	}
	
	@FXML
    protected void click_home() throws IOException {

    	System.out.println("Closing ranking page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
	
	@FXML
	protected void view_global_rank() {
		search_field.setText("");
		search_field_region.setValue(null);	//resetting the choice box
		retrieve_users();
	}
	
	@FXML
	protected void retrieve_users() {
		
		String user_input = search_field.getText().toString(); 
		search_field_region.setValue(null);	//resetting the choice box
		Boolean search = true;
		if(user_input.equals("")) {
			search = false;
		}
		
		//searching
		users_doc = RankingMongoDriver.retrieve_user(search, user_input);
		if(users_doc == null) {	//handling error
			selected_user.setText("An error has occurred while searching for users. Please, exit the page and try again later.");
			return;
		}
		
    	//show up users
    	show_ranking(users_doc);
    }
	
	
	protected void show_ranking(ArrayList<Document> result){

		selected_user.setText(""); //clearing the selected user field
		
		//showing off trades
		ObservableList<String> list = FXCollections.observableArrayList();
    	list.removeAll(list);	//clearing the list
    	
    	int i=0;
    	while(i < result.size()) {	
    		Document user_doc = result.get(i);
    		String user_nickname = user_doc.getString("username");
    		String user_points = user_doc.get("points").toString();
			String user_region = user_doc.getString("region");
    		String user_output = /*i+1 + ") " +*/ user_nickname + " - Points: " + user_points +" - Region: "+user_region;
    		list.add(user_output);
    		i=i+1;
    	}
    	user_list.getItems().clear();
		user_list.getItems().addAll(list);
	}

	
	public void click_see_user() throws IOException {
		
		//retrieving card id
		String full_text[] = selected_user.getText().split(" ");
		user_input = full_text[0];
		System.out.println("Viewing user: " + user_input);
		
		Stage stage = (Stage)root.getScene().getWindow();
		view_user(stage);
	}
	
	 public static void view_user(Stage stage) throws IOException {
		 	
		 System.out.println("Opening user page...");
		 FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("see_user_page.fxml"));
	     Scene scene = new Scene(fxmlLoader.load());
	     stage.setTitle("Card info");
	     stage.setScene(scene);
	     stage.show();
	 }

	public void best_for_region(MouseEvent mouseEvent) {

		search_field.setText("");
		search_field_region.setValue(null);	//resetting the choice box
		
		//searching
		users_doc = RankingMongoDriver.best_for_region();
		if(users_doc == null) {	//handling error
			selected_user.setText("An error has occurred while searching for users. Please, exit the page and try again later.");
			return;
		}
				
		//show up users
		show_ranking(users_doc);
		
	}

	public void retrieve_users_by_region(MouseEvent mouseEvent) {

		//case of no region selected
		if(search_field_region.getValue() == null) {
			return;
		}
		
		String region = search_field_region.getValue().toString();
		
		//searching
		users_doc = RankingMongoDriver.search_users_by_region(region);
		if(users_doc == null) {	//handling error
			selected_user.setText("An error has occurred while searching for users. Please, exit the page and try again later.");
			return;
		}
						
		//show up users
		show_ranking(users_doc);
	}
}
