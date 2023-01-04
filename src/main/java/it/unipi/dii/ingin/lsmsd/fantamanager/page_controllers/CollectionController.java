package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
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

import java.io.IOException;
import java.net.URL;
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

    @FXML
    private Button delete_button;

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
    protected void click_delete() throws IOException {

        String name_player=player_selected.getText();

        ArrayList<player_collection> coll=collection.load_collection(global.id_user);

        for(int i=0; i<coll.size();i++){
                if(coll.get(i).get_name().equals(name_player)){
                        System.out.println(name_player);
                        collection.delete_player_from_collection(coll.get(i).get_id());
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

        //TableView.TableViewSelectionModel<LineTable> player = table_collection.getSelectionModel();



        table_collection.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {

                //System.out.println(newValue.fieldProperty("Name").getValue());
                player_selected.setText(newValue.fieldProperty("Name").getValue());

                if(!player_selected.getText().isEmpty()){
                        delete_button.setDisable(false);
                }
        });
    }



    public void create_table(ArrayList<player_collection> databaseObject)
    {

        //TableView<LineTable> table = new TableView<>();
        //List<Map<String, String>> databaseObject = load_collection();
        System.out.println("creazione tabella");

        //TableView<LineTable> table = new TableView<>();
        //JSONArray databaseObject = collection.load_collection(1);  //mi serve id globale
        //ArrayList<player_collection> databaseObject = collection.load_collection(1);
        //System.out.println(databaseObject);


        String[] key_set={"Position","Team","Name","Quantity"};
        //double[] column_width={0.2,0.2,0.4,0.2};

        for (String key : key_set)
        {
            TableColumn<LineTable, String> col = new TableColumn<>(key);
            col.setCellValueFactory((TableColumn.CellDataFeatures<LineTable, String> cellData) -> cellData.getValue().fieldProperty(key));
            col.prefWidthProperty().bind(table_collection.widthProperty().multiply(0.25));
            table_collection.getColumns().add(col);

        }
        /*for(double width:column_width){
            col.prefWidthProperty().bind(table_collection.widthProperty().multiply());
        }*/
        List<LineTable> data = new ArrayList<>();



        for(int i=0; i<databaseObject.size();i++){
            //JSONObject player=(JSONObject) databaseObject.get(i);
            player_collection player=databaseObject.get(i);

            //System.out.println(player);

            Map<String, String> record = new HashMap<>();


            record.put("Position", player.get_position());
            record.put("Team", player.get_team());
            record.put("Name", player.get_name());
            record.put("Quantity", String.valueOf(player.get_quantity()));

            //values.add(record);
            LineTable sequence =new LineTable(record);
            //System.out.println(sequence);
            data.add(sequence);

        }

        //System.out.println(data);

        table_collection.setItems(FXCollections.observableArrayList(data));

        //table_collection.setPrefWidth(500);

        //HBox root = new HBox();
        //root.addAll(table);

        //Scene scene = new Scene(root, 500, 500);

    }


}
