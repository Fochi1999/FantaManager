package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.CardMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.OptionsMongoDriver;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.*;
import com.mongodb.client.result.*;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Indexes.descending;
import static com.mongodb.client.model.Projections.*;

public class ShopController implements Initializable {

	@FXML
	private Button see_card;
	
	@FXML
	private ListView<String> card_list;
	
	@FXML
	private Button search_button;
	
	@FXML
	private TextField text_field;

	@FXML
	private TextField selected_card;
	
	@FXML
    private Parent root;
	
	static String card_id_input;

	@FXML
	private ChoiceBox skill;
	@FXML
	private ChoiceBox team;
	@FXML
	private ChoiceBox role;

	@FXML
	private Button search_by_skill;
	
	
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		System.out.println("Opening shop page..."); 	
		//disabling the button
		see_card.setDisable(true);

		//disabling button for aggregation
		//search_by_skill.setDisable(true);

		//choiceBox of role
		String[] roles={"Goalkeeper","Defender","Midfielder","Attacker"};
		for(String role_:roles){
				role.getItems().add(role_);
		}
		//choiceBox of team
		String[] teams={"Atalanta","Bologna","Cagliari","Empoli","Fiorentina","Genoa","Inter","Juventus","Lazio",
				"AC Milan","Napoli","AS Roma","Salernitana","Sampdoria","Sassuolo","Spezia","Torino","Udinese","Venezia","Verona"};
		for(String team_:teams){
			team.getItems().add(team_);
		}

		
		//handling the event: clicking on a card from the list
        MultipleSelectionModel<String> card = card_list.getSelectionModel();

        card.selectedItemProperty().addListener(new ChangeListener<String>() {
        	public void changed(ObservableValue<? extends String> changed, String oldVal, String newVal) {

        		see_card.setDisable(true);
        		String selItems = "";
        		ObservableList<String> selected = card_list.getSelectionModel().getSelectedItems();

        		for (int i = 0; i < selected.size(); i++) {
        			selItems += "" + selected.get(i); 
        		}
        		
        		if(!selItems.equals("")) {	//handling indexing error
        			String full_text[] = selItems.split(" ");
        			int text_size = full_text.length;
        			String team = full_text[text_size-4];
        			String text;
        			if(team.equals("Roma") || team.equals("Milan")) {	//handling formatting error caused by 2 words team name
        				text = full_text[text_size-14] + " - " + full_text[text_size-8] + " - " + full_text[text_size-4]; 
        			}
        			else {
        				text = full_text[text_size-13] + " - " + full_text[text_size-7] + " - " + full_text[text_size-4]; 
        			}
        			card_id_input = full_text[text_size-1];
        			selected_card.setText(text); //the card will show up on the lower Area
        		}
        		
       			if(!selected_card.getText().isEmpty()) {
       				see_card.setDisable(false);
       			}
            	   
        	}
        });
		
        //showing up cards
		try {   //modifica da controllare che vada bene
			retrieve_cards();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	@FXML
    protected void click_home() throws IOException {

    	System.out.println("Closing shop page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
	
	
	public void retrieve_cards() throws ParseException {
		
		String cards_input = text_field.getText();
		System.out.println("Searching for: "+ cards_input);
		

    	ArrayList<Document> resultDoc = CardMongoDriver.retrieve_cards(cards_input);
    	

    	int i=0;
    	//while(i < resultDoc.size()) {  //questa cosa va fatta una volta sola
    		Document card_doc = resultDoc.get(i);

			//retrieve all general_statistics and put them on the choice box

			String player=card_doc.toJson();
			//System.out.println(player);
			JSONParser parser = new JSONParser();
			JSONObject json_player = (JSONObject) parser.parse(player);

			JSONObject general_stats= (JSONObject) json_player.get("general_statistics");
			//System.out.println(general_stats);
			Set<String> set_skills = general_stats.keySet();

			//skill=new ChoiceBox();
			for (String generic_skill:set_skills ) {
				//System.out.println(generic_skill);
				skill.getItems().add(generic_skill);
			}
			//i=i+1;
    	//}
		
    	show_cards(resultDoc);
    	CardMongoDriver.closeConnection();
    	
	}
	
	public void show_cards(ArrayList<Document> result) throws ParseException {
		
		//showing off trades
		ObservableList<String> list = FXCollections.observableArrayList();
    	list.removeAll(list);	//clearing the list
    	int i = 0;
    	while(i < result.size()) {	
    		Document card_doc = result.get(i);
    		String card_id = card_doc.get("_id").toString();
    		String card_fullname = card_doc.get("fullname").toString();
    		String card_credits = card_doc.get("credits").toString();
    		String card_team = card_doc.getString("team");
    		String card_role = card_doc.getString("position");
    		String card_output = card_fullname + " - Cost: " + card_credits +
    				" - Role: " + card_role + " - Team: " + card_team + " - ID: " + card_id;
    		list.add(card_output);
    		i=i+1;
    	}
    	card_list.getItems().clear();
		card_list.getItems().addAll(list);
	}
	
	public void click_see_card() throws IOException {
		
		//retrieving card id
		System.out.println("Viewing card with object id: " + card_id_input);
		Stage stage = (Stage)root.getScene().getWindow();
		view_card(stage);
	}
	
	 public static void view_card(Stage stage) throws IOException {
		 	
		 	System.out.println("Opening card page...");
		 	FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("see_card_page.fxml"));
	        Scene scene = new Scene(fxmlLoader.load());
	        stage.setTitle("Card info");
	        stage.setScene(scene);
	        stage.show();
	    	
	}


	public void search_by_skill(MouseEvent mouseEvent) throws ParseException {

			System.out.println(role.getValue());
			System.out.println(team.getValue());
			System.out.println(skill.getValue());

			if(skill.getValue() == null && team.getValue() == null && role.getValue() == null) {
				System.out.println("No values inserted!");
				return;
			}
			
			show_cards(CardMongoDriver.search_card_by((String) skill.getValue(), (String) team.getValue(), (String) role.getValue()));

			CardMongoDriver.closeConnection();

	}

	public void reload_page(MouseEvent mouseEvent) throws IOException {

		//ricarico la pagina
		Stage stage= (Stage)root.getScene().getWindow();
		FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("shop_page.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("Shop page");
		stage.setScene(scene);
		stage.show();
	}

	public void buy_packet(MouseEvent mouseEvent) throws NoSuchAlgorithmException, ParseException {

		ArrayList<Document> card_extracted_list=new ArrayList<>();

		if(global.user.getCredits()>=100){
			OptionsMongoDriver.update_user_credits(false,global.user.username,100);
			OptionsMongoDriver.update_user_collection(true,global.user.username,5);
			ArrayList<Document> resultDoc = CardMongoDriver.retrieve_cards("");
			for(int i=0; i<5;i++) {
				Random random=new Random();
				//String card=card_list.getItems().get(random.nextInt(533));
				Document card_doc=resultDoc.get(random.nextInt(533));
				System.out.println(card_doc);
				card_collection card_extracted=new card_collection(card_doc.getInteger("player_id"), card_doc.getString("fullname"), 1, card_doc.getString("team"), card_doc.getString("position"));

				collection.add_card_to_collection(card_extracted, global.id_user);
				card_extracted_list.add(card_doc);
			}
			show_cards(card_extracted_list);
		}
		else{
			selected_card.setText("Credito insufficiente per acquistare il packet");
		}


	}
}
