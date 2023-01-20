package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.trades;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collectionRedisDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.CardMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.Trade;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.TradeMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import org.bson.Document;
import org.bson.types.ObjectId;

public class NewTradeController implements Initializable{

	@FXML private Parent root;
	@FXML private CheckBox credits_checkbox;
	@FXML private Button create_trade;
	@FXML private Button back;
	@FXML private Text error_text;
	@FXML private ListView<String> card_list;
	@FXML private TextField selected_card;
	
	@FXML private TextField card_from1;
	@FXML private TextField card_from2;
	@FXML private TextField card_from3;
	@FXML private TextField credits_from;
	
	@FXML private TextField card_to1;
	@FXML private TextField card_to2;
	@FXML private TextField card_to3;
	@FXML private TextField credits_to;
	
	@FXML private CheckBox card_from_checkbox1;
	@FXML private CheckBox card_from_checkbox2;
	@FXML private CheckBox card_from_checkbox3;
	@FXML private CheckBox card_to_checkbox1;
	@FXML private CheckBox card_to_checkbox2;
	@FXML private CheckBox card_to_checkbox3;
	
	@FXML private Button add_from1;
	@FXML private Button add_from2;
	@FXML private Button add_from3;
	@FXML private Button add_to1;
	@FXML private Button add_to2;
	@FXML private Button add_to3;

	@FXML private Button offer;
	@FXML private Button want;
	
	private ArrayList<Document> all_cards_list = global.full_card_list;
	private ArrayList<card_collection> cards_owned = global.owned_cards_list;
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		error_text.setText("");
		//--------per gestire nuovo meccanismo offered-wanted
		offer.setDisable(true);
		want.setDisable(false);
		card_to_checkbox1.setDisable(true);
		card_to_checkbox2.setDisable(true);
		card_to_checkbox3.setDisable(true);

		
		disable_fields();
		add_checkbox_listeners();
		
		open_cards_collection();
		
		//handling the event: clicking on a card from the list
        MultipleSelectionModel<String> card = card_list.getSelectionModel();

        card.selectedItemProperty().addListener(new ChangeListener<String>() {
        	public void changed(ObservableValue<? extends String> changed, String oldVal, String newVal) {

        		String selItems = "";
              	ObservableList<String> selected = card_list.getSelectionModel().getSelectedItems();

               	for (int i = 0; i < selected.size(); i++) {
            	   selItems += "" + selected.get(i); 
               	}
               	
               	//handling formatting problems on selecting strings
               	if(!selItems.equals("")) {
               		//String full_text[] = selItems.split("<>");
               		//String text = full_text[0] + " <> " + full_text[1] + " <> " + full_text[2]; 
               		selected_card.setText(selItems); //the card will show up on the lower Area
               	}	
        	}
        });
	}
	
	
	public void click_back() throws IOException{
		System.out.println("Closing 'new trade' page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.go_to_trades(stage);
	}

	
	@FXML
	private void create_trade() throws IOException, NoSuchAlgorithmException {
		
		if(!check_fields()) {
			System.out.println("Error on input fields!");
			return;
		}
		
		
		//check on credits input
		int user_credits = global.user.getCredits();
		if(Integer.parseInt(credits_from.getText()) > user_credits){
			System.out.println("Credit input value exceeded! Sorry, you don't have that much credits :/");
			error_text.setText("Credit input value exceeded! Insufficient funds!");
			return;
		}
		
		//create with mongoDB
		ArrayList<String> card_from_collection=new ArrayList<>();
		
		ArrayList<Document> card_from=new ArrayList<>();
		if(card_from_checkbox1.isSelected() && !card_from1.getText().equals("")) {
			String words[] = card_from1.getText().toString().split(" ");
			card_from_collection.add(words[words.length-1]);
			String words2[] = card_from1.getText().toString().split(" <> ");
			Document card_doc = new Document();
			card_doc.append("card_id", Integer.parseInt(words[words.length-1]));
			card_doc.append("card_name", words2[0]);
			card_doc.append("card_team", words2[2].substring(6));
			card_doc.append("card_position", words2[1].substring(10));
			card_from.add(card_doc);
		}
		if(card_from_checkbox2.isSelected() && !card_from2.getText().equals("")) {
			String words[] = card_from2.getText().toString().split(" ");
			card_from_collection.add(words[words.length-1]);
			String words2[] = card_from2.getText().toString().split(" <> ");
			Document card_doc = new Document();
			card_doc.append("card_id", Integer.parseInt(words[words.length-1]));
			card_doc.append("card_name", words2[0]);
			card_doc.append("card_team", words2[2].substring(6));
			card_doc.append("card_position", words2[1].substring(10));
			card_from.add(card_doc);
		}
		if(card_from_checkbox3.isSelected() && !card_from3.getText().equals("")) {
			String words[] = card_from3.getText().toString().split(" ");
			card_from_collection.add(words[words.length-1]);
			String words2[] = card_from3.getText().toString().split(" <> ");
			Document card_doc = new Document();
			card_doc.append("card_id", Integer.parseInt(words[words.length-1]));
			card_doc.append("card_name", words2[0]);
			card_doc.append("card_team", words2[2].substring(6));
			card_doc.append("card_position", words2[1].substring(10));
			card_from.add(card_doc);
		}
		ArrayList<Document> card_to=new ArrayList<>();
		if(card_to_checkbox1.isSelected() && !card_to1.getText().equals("")) {
			String words2[] = card_to1.getText().toString().split(" <> ");
			Document card_doc = new Document();
			card_doc.append("card_id", Integer.parseInt(words2[3].substring(4)));
			card_doc.append("card_name", words2[0]);
			card_doc.append("card_team", words2[2].substring(6));
			card_doc.append("card_position", words2[1].substring(10));
			card_to.add(card_doc);
		}	
		if(card_to_checkbox2.isSelected() && !card_to2.getText().equals("")) {
			String words2[] = card_to2.getText().toString().split(" <> ");
			Document card_doc = new Document();
			card_doc.append("card_id", Integer.parseInt(words2[3].substring(4)));
			card_doc.append("card_name", words2[0]);
			card_doc.append("card_team", words2[2].substring(6));
			card_doc.append("card_position", words2[1].substring(10));
			card_to.add(card_doc);
		}
		if(card_to_checkbox3.isSelected() && !card_to3.getText().equals("")) {
			String words2[] = card_to3.getText().toString().split(" <> ");
			Document card_doc = new Document();
			card_doc.append("card_id", Integer.parseInt(words2[3].substring(4)));
			card_doc.append("card_name", words2[0]);
			card_doc.append("card_team", words2[2].substring(6));
			card_doc.append("card_position", words2[1].substring(10));
			card_to.add(card_doc);
		}
		int new_trade_total_credits = Integer.parseInt(credits_to.getText())-Integer.parseInt(credits_from.getText());
		Trade new_trade=new Trade("", global.user.username,"",new_trade_total_credits,card_from,card_to,0);
		
		System.out.println("Creating new trade...");
		
		//INSERT ON DB
		
		//MongoDB
		ObjectId new_trade_id = null;
		try{
			//insert new trade on mongoDB
			new_trade_id = TradeMongoDriver.create_new_trade(new_trade);
			
			//update user's credits info (mongoDB)
			if(new_trade_total_credits < 0) {	//if a user offered credits(negative credits value), they will be temporary removed from his account
				OptionsMongoDriver.update_user_credits(true,global.user.getUsername(),new_trade_total_credits);
			}
			System.out.print("MongoDB...OK\t");
		}
		catch(Exception e){	//handling error
			error_text.setText("Network error! Try again later");
			return;
		}
		
		//REDIS
		try {
			for(String card:card_from_collection){
				collectionRedisDriver.delete_card_from_collection(card);
			}
			System.out.print("Redis...OK\t\n");
		}
		catch(Exception e){ //handling error
			error_text.setText("Network error! Try again later");
			
			//return user's credits
			if(new_trade_total_credits < 0) {	
				OptionsMongoDriver.update_user_credits(false,global.user.getUsername(),new_trade_total_credits);
			}
			//delete the newly created trade from mongoDB
			TradeMongoDriver.delete_my_trade(new_trade_id);
			System.out.println("New trade deleted due to network error");
			return;
		}
		
		System.out.println("Trade created - "+card_from +" || "+ card_to +" || "+ card_from_collection);
		
		//reload of the page
		//System.out.println("Reloading collection");
		//global.owned_cards_list = collectionRedisDriver.load_collection(global.id_user);
		reload_page_trades();
	}

	private void reload_page_trades() throws IOException {
		System.out.println("Opening 'new trade' page...");
		Stage stage = (Stage)root.getScene().getWindow();
		FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("new_trade.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("New trade");
		stage.setScene(scene);
		stage.show();
	}

	private void open_cards_collection() {

		//showing off cards
		ObservableList<String> list = FXCollections.observableArrayList();
		list.removeAll(list);	//clearing the list
		
		if (cards_owned == null){	//retrieve cards only one time
			collectionRedisDriver.apertura_pool();
			cards_owned = collectionRedisDriver.load_collection(global.id_user);
			System.out.println("Card owned list retrieved from DB");
		}
		
		for(card_collection card:cards_owned){
						//da qui ogni player ha le sue quattro informazioni e poi usarle agile
			String card_output = card.get_name() + " <> Position: " + card.get_position() +" <> Team: " 
					+ card.get_team() + " <> Quantity: " + card.get_quantity() + " <> ID: " + card.get_id();
			list.add(card_output);
		}
		card_list.getItems().clear();
		card_list.getItems().addAll(list);
		collectionRedisDriver.closePool();

	}
	
	
	public Boolean check_fields(){
		
		//check if at least one card is selected
		if(!card_from_checkbox1.isSelected() && !card_to_checkbox1.isSelected() && !card_from_checkbox2.isSelected() 
			&& !card_to_checkbox3.isSelected() && !card_from_checkbox3.isSelected() && !card_to_checkbox3.isSelected()) {
			error_text.setText("You must select at least one card to create trade!");
			return false;
		}
		
		//check if the card values are empty when ticked
		if(card_from_checkbox1.isSelected() && card_from1.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_from_checkbox2.isSelected() && card_from2.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_from_checkbox3.isSelected() && card_from3.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_to_checkbox1.isSelected() && card_to1.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_to_checkbox2.isSelected() && card_to2.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		if(card_to_checkbox3.isSelected() && card_to3.getText().equals("")) {
			error_text.setText("Card not selected!");
			return false;
		}
		
		//check if credits fields are not empty when ticked
		if(credits_checkbox.isSelected() && (credits_to.getText().equals("") || credits_from.getText().equals(""))){
			error_text.setText("Missing value on credits!");
			return false;
		}
		
		//check if credit fields have numeric values
		if( (!credits_to.getText().matches("[0-9]*")) || (!credits_from.getText().matches("[0-9]*")) ){   //inserito !
			error_text.setText("Credits value must be an integer!");
			return false;
		}	
		
		return true;
	}
	
	
	public void add_checkbox_listeners(){
		
		card_from_checkbox1.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_from1.setDisable(!add_from1.isDisabled());
			         card_from1.setDisable(!card_from1.isDisabled());
			         if(card_from1.isDisabled()) {
			        	 card_from1.setText("");
			         }
					 else{
						 	add_from1.setOnAction(new EventHandler<ActionEvent>() {
								@Override
								public void handle(ActionEvent actionEvent) {
									card_from1.setText(selected_card.getText());
									if(!check_duplicates("from")) {
										error_text.setText("Cannot insert duplicates!");
										card_from1.setText("");
									}
								}
							});
					 }
			      });
		card_from_checkbox2.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_from2.setDisable(!add_from2.isDisabled());
			         card_from2.setDisable(!card_from2.isDisabled());
			         if(card_from2.isDisabled()) {
			        	 card_from2.setText("");
			         }else{
						 add_from2.setOnAction(new EventHandler<ActionEvent>() {
							 @Override
							 public void handle(ActionEvent actionEvent) {
								 card_from2.setText(selected_card.getText());
									if(!check_duplicates("from")) {
										error_text.setText("Cannot insert duplicates!");
										card_from2.setText("");
									}
							 }
						 });
					 }
			      });
		card_from_checkbox3.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_from3.setDisable(!add_from3.isDisabled());
			         card_from3.setDisable(!card_from3.isDisabled());
			         if(card_from3.isDisabled()) {
			        	 card_from3.setText("");
			         }else{
						 add_from3.setOnAction(new EventHandler<ActionEvent>() {
							 @Override
							 public void handle(ActionEvent actionEvent) {
								 card_from3.setText(selected_card.getText());
									if(!check_duplicates("from")) {
										error_text.setText("Cannot insert duplicates!");
										card_from3.setText("");
									}
							 }
						 });
					 }
			      });
		
		card_to_checkbox1.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_to1.setDisable(!add_to1.isDisabled());
			         card_to1.setDisable(!card_to1.isDisabled());
			         if(card_to1.isDisabled()) {
			        	 card_to1.setText("");
			         }else{
						 add_to1.setOnAction(new EventHandler<ActionEvent>() {
							 @Override
							 public void handle(ActionEvent actionEvent) {
								 card_to1.setText(selected_card.getText());
									if(!check_duplicates("to")) {
										error_text.setText("Cannot insert duplicates!");
										card_to1.setText("");
									}
							 }
						 });
					 }
			      });
		card_to_checkbox2.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_to2.setDisable(!add_to2.isDisabled());
			         card_to2.setDisable(!card_to2.isDisabled());
			         if(card_to2.isDisabled()) {
			        	 card_to2.setText("");
			         }else{
						 add_to2.setOnAction(new EventHandler<ActionEvent>() {
							 @Override
							 public void handle(ActionEvent actionEvent) {
								 card_to2.setText(selected_card.getText());
									if(!check_duplicates("to")) {
										error_text.setText("Cannot insert duplicates!");
										card_to2.setText("");
									}
							 }
						 });
					 }
			      });
		card_to_checkbox3.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
			         add_to3.setDisable(!add_to3.isDisabled());
			         card_to3.setDisable(!card_to3.isDisabled());
			         if(card_to3.isDisabled()) {
			        	 card_to3.setText("");
			         }else{
						 add_to3.setOnAction(new EventHandler<ActionEvent>() {
							 @Override
							 public void handle(ActionEvent actionEvent) {
								 card_to3.setText(selected_card.getText());
									if(!check_duplicates("to")) {
										error_text.setText("Cannot insert duplicates!");
										card_to3.setText("");
									}
							 }
						 });
					 }
			      });
		
		credits_checkbox.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
				         credits_to.setDisable(!credits_to.isDisabled());
				         credits_from.setDisable(!credits_from.isDisabled());
				         credits_from.setText("0");
				         credits_to.setText("0");
				      });
		
	}

	private void disable_fields() {
		add_from1.setDisable(true);
		add_from2.setDisable(true);
		add_from3.setDisable(true);
		add_to1.setDisable(true);
		add_to2.setDisable(true);
		add_to3.setDisable(true);
		credits_to.setDisable(true);
		credits_from.setDisable(true);
		card_from1.setDisable(true);
		card_from2.setDisable(true);
		card_from3.setDisable(true);
		card_to1.setDisable(true);
		card_to2.setDisable(true);
		card_to3.setDisable(true);
	}

	public void set_card_offered(MouseEvent mouseEvent) {

			card_from_checkbox1.setDisable(false);
			card_from_checkbox2.setDisable(false);
			card_from_checkbox3.setDisable(false);
	
			add_from1.setDisable(true);
			add_from2.setDisable(true);
			add_from3.setDisable(true);
			
			if(card_from_checkbox1.isSelected()) {
				add_from1.setDisable(false);
			}
			if(card_from_checkbox2.isSelected()) {
				add_from2.setDisable(false);
			}
			if(card_from_checkbox3.isSelected()) {
				add_from3.setDisable(false);
			}

			add_to1.setDisable(true);
			add_to2.setDisable(true);
			add_to3.setDisable(true);
			
			card_to_checkbox1.setDisable(true);
			card_to_checkbox2.setDisable(true);
			card_to_checkbox3.setDisable(true);

			offer.setDisable(true);
			want.setDisable(false);
			selected_card.setText("");

			open_cards_collection();
	}

	public void set_card_wanted(MouseEvent mouseEvent) {

			card_to_checkbox1.setDisable(false);
			card_to_checkbox2.setDisable(false);
			card_to_checkbox3.setDisable(false);

			add_to1.setDisable(true);
			add_to2.setDisable(true);
			add_to3.setDisable(true);
			
			if(card_to_checkbox1.isSelected()) {
				add_to1.setDisable(false);
			}
			if(card_to_checkbox2.isSelected()) {
				add_to2.setDisable(false);
			}
			if(card_to_checkbox3.isSelected()) {
				add_to3.setDisable(false);
			}

			add_from1.setDisable(true);
			add_from2.setDisable(true);
			add_from3.setDisable(true);

			card_from_checkbox1.setDisable(true);
			card_from_checkbox2.setDisable(true);
			card_from_checkbox3.setDisable(true);

			offer.setDisable(false);
			want.setDisable(true);
			selected_card.setText("");
			
			show_all_cards();
	}

	private void show_all_cards() {
		//showing off cards
		ObservableList<String> list = FXCollections.observableArrayList();
		list.removeAll(list);	//clearing the list

		if(all_cards_list == null) {	//retrieve cards only one time
			all_cards_list = CardMongoDriver.retrieve_cards("");
			System.out.println("All cards list retrieved from DB");
		}
		int i=0;
		while( i < all_cards_list.size()) {
			Document card = all_cards_list.get(i);
			//da qui ogni player ha le sue quattro informazioni e poi usarle agile
			String card_output = card.get("fullname") + " <> Position: " + card.get("position") +" <> Team: " 
					+ card.get("team") + " <> ID: " + card.get("player_id");
			list.add(card_output);
			i=i+1;
		}
		card_list.getItems().clear();
		card_list.getItems().addAll(list);


	}


	private boolean check_duplicates(String side) {
		if(side.equals("from")) {
			if((!card_from1.getText().equals("") && card_from1.getText().equals(card_from2.getText())) || (!card_from3.getText().equals("") && card_from1.getText().equals(card_from3.getText())) 
					|| (!card_from2.getText().equals("") && card_from2.getText().equals(card_from3.getText())))
					return false;
			return true;
		}
		if(side.equals("to")) {
			if((!card_to1.getText().equals("") && card_to1.getText().equals(card_to2.getText())) || (!card_to3.getText().equals("") && card_to1.getText().equals(card_to3.getText())) 
					|| (!card_to2.getText().equals("") && card_to2.getText().equals(card_to3.getText())))
				return false;
			return true;
		}
		else 
			return false;
	}
	
}
