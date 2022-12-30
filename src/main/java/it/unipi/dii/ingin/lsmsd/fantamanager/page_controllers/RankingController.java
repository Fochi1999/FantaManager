package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Indexes;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import javafx.scene.input.MouseEvent;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Projections.*;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Sorts.descending;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
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
import org.json.simple.parser.ParseException;

public class RankingController implements Initializable{


	public TextField search_field_region;
	public Button search_button_region;
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
	
	static String user_id_input;
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		System.out.println("Opening ranking page..."); 	
		//disabling the button
		view_profile.setDisable(true);
		
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
               
               selected_user.setText(selItems); //the user will show up on the lower Area
               
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
		retrieve_users();
	}
	
	@FXML
	protected void retrieve_users() {
		
		String user_input = search_field.getText().toString(); 
		
		//connecting to mongoDB 
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
    	MongoCursor<Document> resultDoc;
    	
    	
		//searching for the user
    	if(user_input.equals("")) {	//blank search field
    		
    		try {
    			resultDoc = collection.find().sort(descending("points")).iterator();
    		}
    		catch(Exception e) {
    			System.out.println("Error on search.");
    			return;
    		}  		
    	}
    	
    	else {
    		//filter
    		Pattern pattern = Pattern.compile(user_input, Pattern.CASE_INSENSITIVE);
        	Bson filter = Filters.regex("username", pattern);	
        	
    		try {
    			resultDoc = collection.find(filter).sort(descending("points")).iterator();
    		}
    		catch(Exception e) {
    			System.out.println("Error on search.");
    			return;
    		}  	
    	}
    	
    	
    	//print
    	show_ranking(resultDoc);
    	myClient.close();
	}
	
	
	protected void show_ranking(MongoCursor<Document> result){

		//showing off trades
		ObservableList<String> list = FXCollections.observableArrayList();
    	list.removeAll(list);	//clearing the list
    	
    	int i=0;
    	while(result.hasNext()) {	
    		i=i+1;
    		Document user_doc = result.next();
    		String user_id = user_doc.get("_id").toString();
    		String user_nickname = user_doc.getString("username");
    		String user_points = user_doc.get("points").toString();
    		String user_output = i + ") " + user_nickname + " /// Points: " + user_points + " /// user_id: " + user_id;
    		list.add(user_output);
    	}
    	user_list.getItems().clear();
		user_list.getItems().addAll(list);
	}

	
	public void click_see_user() throws IOException {
		
		//retrieving card id
		String full_text[] = selected_user.getText().split(" ");
		user_id_input = full_text[full_text.length-1];
		System.out.println("Viewing user with object id: " + user_id_input);
		
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

		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);

		//Bson p1=project(fields(include("")));
		Bson group=group("$region", Accumulators.first("username","$username"));

		try(MongoCursor<Document> cursor=collection.aggregate(Arrays.asList(group)).iterator()){
			while(cursor.hasNext()){
				System.out.println(cursor.next().toJson());
				//show_cards(cursor);
			}
		}
	}

	public void retrieve_users_by_region(MouseEvent mouseEvent) {

		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);

		Bson match1=match(eq("region",search_field_region.getText()));

		try(MongoCursor<Document> cursor=collection.aggregate(Arrays.asList(match1)).iterator()){
			while(cursor.hasNext()){
				//System.out.println(cursor.next().toJson());
				show_ranking(cursor);
			}
		}
	}
}
