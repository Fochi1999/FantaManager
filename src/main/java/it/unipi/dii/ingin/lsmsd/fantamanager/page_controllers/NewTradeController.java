package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import com.mongodb.client.MongoCursor;
import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.player_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.CardMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.Trade;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.TradeMongoDriver;
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
               	
               	if(!selItems.equals("")) {
               		selected_card.setText(selItems);
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
	private void create_trade() throws IOException {
		
		if(!check_fields()) {
			System.out.println("Error on input fields!");
			return;
		}
		//create with mongoDB
		ArrayList<String> player_from_collection=new ArrayList<>();
		ArrayList<String> player_from=new ArrayList<>();
		if(card_from_checkbox1.isSelected() && !card_from1.getText().equals("")) {
			player_from.add(retrieve_playerName_from_string(card_from1.getText()));
			player_from_collection.add(card_from1.getText());
		}
		if(card_from_checkbox2.isSelected() && !card_from2.getText().equals("")) {
			player_from.add(retrieve_playerName_from_string(card_from2.getText()));
			player_from_collection.add(card_from2.getText());
		}
		if(card_from_checkbox3.isSelected() && !card_from3.getText().equals("")) {
			player_from.add(retrieve_playerName_from_string(card_from3.getText()));
			player_from_collection.add(card_from3.getText());
		}
		ArrayList<String> player_to=new ArrayList<>();
		if(card_to_checkbox1.isSelected() && !card_to1.getText().equals(""))
			player_to.add(retrieve_playerName_from_string(card_to1.getText()));
		if(card_to_checkbox2.isSelected() && !card_to2.getText().equals(""))
			player_to.add(retrieve_playerName_from_string(card_to2.getText()));
		if(card_to_checkbox3.isSelected() && !card_to3.getText().equals(""))
			player_to.add(retrieve_playerName_from_string(card_to3.getText()));
		Trade new_trade=new Trade("", global.user.username,"",Integer.parseInt(credits_to.getText())-Integer.parseInt(credits_from.getText()),player_from,player_to,0);
		TradeMongoDriver.create_new_trade(new_trade);

		//delete dalla propria collection su redis
		for(String player:player_from_collection){
				player_collection playerCollection=retrieve_player_from_string(player);
				collection.delete_player_from_collection(playerCollection);
		}


		//reload of the page

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

	private player_collection retrieve_player_from_string(String player) {
						ArrayList<String> values=new ArrayList<>();
						String[] words=player.split("//");
						for(String word:words){
								String[] elem=word.split(": ");
								values.add(elem[1]);
						}
						return new player_collection(Integer.parseInt(values.get(0)),values.get(1),Integer.parseInt(values.get(4)),values.get(2),values.get(3));
	}

	private String retrieve_playerName_from_string(String player) {
		ArrayList<String> values=new ArrayList<>();
		String[] words=player.split("//");
		for(String word:words){
			String[] elem=word.split(": ");
			values.add(elem[1]);
		}
		return values.get(1);
	}


	private void open_cards_collection() {

		//showing off cards
		ObservableList<String> list = FXCollections.observableArrayList();
		list.removeAll(list);	//clearing the list
		
		//TODO retrieve cards from redis collection
		collection.apertura_pool();
		ArrayList<player_collection> collection_of_user= collection.load_collection(global.id_user);

		for(player_collection player:collection_of_user){
						//da qui ogni player ha le sue quattro informazioni e poi usarle agile
			String card_output = "id: " + player.get_id() + "//name: " + player.get_name()
					+ "//role: " + player.get_position() +"//team: " + player.get_team() + "//quantity: " + player.get_quantity();
			list.add(card_output);
		}
		card_list.getItems().clear();
		card_list.getItems().addAll(list);
		collection.closePool();

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
		if(!(credits_to.getText().matches("[0-9]*") || !credits_from.getText().matches("[0-9]*"))){   //inserito !
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
							 }
						 });
					 }
			      });
		
		credits_checkbox.selectedProperty().addListener(
			      (ObservableValue<? extends Boolean> ov, Boolean old_val, Boolean new_val) -> {
				         credits_to.setDisable(!credits_to.isDisabled());
				         credits_from.setDisable(!credits_from.isDisabled());
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

	public void set_player_offered(MouseEvent mouseEvent) {

			card_from_checkbox1.setDisable(false);
			card_from_checkbox2.setDisable(false);
			card_from_checkbox3.setDisable(false);

			card_to_checkbox1.setDisable(true);
			card_to_checkbox2.setDisable(true);
			card_to_checkbox3.setDisable(true);

			offer.setDisable(true);
			want.setDisable(false);

			open_cards_collection();
	}

	public void set_player_wanted(MouseEvent mouseEvent) {

			card_to_checkbox1.setDisable(false);
			card_to_checkbox2.setDisable(false);
			card_to_checkbox3.setDisable(false);

			card_from_checkbox1.setDisable(true);
			card_from_checkbox2.setDisable(true);
			card_from_checkbox3.setDisable(true);

			offer.setDisable(false);
			want.setDisable(true);

			show_all_cards();
	}

	private void show_all_cards() {
		//showing off cards
		ObservableList<String> list = FXCollections.observableArrayList();
		list.removeAll(list);	//clearing the list

		//TODO retrieve cards from mongo

		MongoCursor<Document> players= CardMongoDriver.retrieve_player_for_trade();
		for (MongoCursor<Document> it = players; it.hasNext(); ) {
			Document player = it.next();
			//da qui ogni player ha le sue quattro informazioni e poi usarle agile
			String card_output = "id: " + player.get("player_id") + "//name: " + player.get("fullname")
					+ "//role: " + player.get("position") +"//team: " + player.get("team");
			list.add(card_output);
			//System.out.println(player.toJson());
		}
		card_list.getItems().clear();
		card_list.getItems().addAll(list);


	}


}
