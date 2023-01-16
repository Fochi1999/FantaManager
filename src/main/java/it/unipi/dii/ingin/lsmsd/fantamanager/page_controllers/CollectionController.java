package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.CardMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.LineTable;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.*;

public class CollectionController implements Initializable {

    @FXML
    private TableView<LineTable> table_collection;

    @FXML
    private Parent root;

    @FXML
    private TextField player_selected;

    @FXML private Button delete_button;
    @FXML private Text delete_warning;
    @FXML private Pane delete_confirm;
    @FXML private Pane delete_cancel;
    @FXML private Text delete_credits_info;
    
    @FXML
    protected void click_home() throws IOException {

        collection.closePool();

        Stage stage= (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }

    @FXML
    protected void click_shop() throws IOException {

        collection.closePool();

        Stage stage= (Stage)root.getScene().getWindow();
        util_controller.go_to_shop(stage);
    }

    @FXML
    protected void click_delete() throws IOException, NoSuchAlgorithmException {

        String name_player=player_selected.getText();

        ArrayList<card_collection> coll=collection.load_collection(global.id_user);

        for(int i=0; i<coll.size();i++){
            if(coll.get(i).get_name().equals(name_player)){
                System.out.println("player_deleted:"+name_player);
                collection.delete_card_from_collection(coll.get(i));
                int credits_received = CardMongoDriver.retrieve_card_credits(name_player)/2; //recupero valore carta e lo dimezzo
                OptionsMongoDriver.update_user_credits(true,global.user.username,credits_received); //e rendo la metà arrotondata per difetto all'utente
            }
        }

        //ricarico la pagina
        Stage stage= (Stage)root.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("collection_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Collection page");
        stage.setScene(scene);
        stage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Opening collection page...");
        collection.apertura_pool();
        create_table(collection.load_collection(global.id_user));

        delete_button.setDisable(true);
        hide_delete_buttons();
        
        table_collection.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

                //System.out.println(newValue.fieldProperty("Name").getValue());
                player_selected.setText(newValue.fieldProperty("Name").getValue());
                hide_delete_buttons();
                int credits_received = CardMongoDriver.retrieve_card_credits(newValue.fieldProperty("Name").getValue())/2;
                delete_credits_info.setText("You will receive "+credits_received+" credits");
                if(!player_selected.getText().isEmpty()){
                        delete_button.setDisable(false);
                }
        });
    }



    public void create_table(ArrayList<card_collection> databaseObject){
      
        String[] key_set={"Position","Team","Name","Quantity"};
        //double[] column_width={0.2,0.2,0.4,0.2};

        for (String key : key_set)
        {
            TableColumn<LineTable, String> col = new TableColumn<>(key);
            col.setCellValueFactory((TableColumn.CellDataFeatures<LineTable, String> cellData) -> cellData.getValue().fieldProperty(key));
            col.prefWidthProperty().bind(table_collection.widthProperty().multiply(0.25));
            table_collection.getColumns().add(col);

        }
        
        List<LineTable> data = new ArrayList<>();

        for(int i=0; i<databaseObject.size();i++){
           
            card_collection player=databaseObject.get(i);
         
            Map<String, String> record = new HashMap<>();

            record.put("Position", player.get_position());
            record.put("Team", player.get_team());
            record.put("Name", player.get_name());
            record.put("Quantity", String.valueOf(player.get_quantity()));

            LineTable sequence =new LineTable(record);          
            data.add(sequence);

        }

        table_collection.setItems(FXCollections.observableArrayList(data));
    }

    public void show_delete_buttons() {
    	delete_warning.setText("Confirm");
    	delete_cancel.setVisible(true);
    	delete_confirm.setVisible(true);
    	delete_credits_info.setVisible(true);
    }

    public void hide_delete_buttons() {
    	delete_warning.setText("");
    	delete_cancel.setVisible(false);
    	delete_confirm.setVisible(false);
    	delete_credits_info.setVisible(false);
    }
    
}
