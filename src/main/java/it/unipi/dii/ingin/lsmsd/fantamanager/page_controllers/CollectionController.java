package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.LineTable;

import javafx.scene.control.TableColumn;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.*;

public class CollectionController implements Initializable {

    @FXML
    private TableView<LineTable> table_collection;

    @FXML
    private Parent root;

    @FXML
    protected void click_home() throws IOException {

        collection.closePool();

        Stage stage= (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Opening collection page...");
        collection.apertura_pool();
        create_table();
    }



    public void create_table()
    {

        //TableView<LineTable> table = new TableView<>();
        //List<Map<String, String>> databaseObject = load_collection();
        System.out.println("creazione tabella");

        //TableView<LineTable> table = new TableView<>();
        //JSONArray databaseObject = collection.load_collection(1);  //mi serve id globale
        ArrayList<player_collection> databaseObject = collection.load_collection(1);
        System.out.println(databaseObject);


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
        //LineTable sequence1 = new LineTable(databaseObject.get(0));
        //LineTable sequence2 = new LineTable(databaseObject.get(1));
        //data.add(sequence1);
        //data.add(sequence2);



        for(int i=0; i<databaseObject.size();i++){
            //JSONObject player=(JSONObject) databaseObject.get(i);
            player_collection player=databaseObject.get(i);

            System.out.println(player);

            Map<String, String> record = new HashMap<>();
            //record.put("id", (String) player.get("id"));
            /*record.put("Position", (String) player.get("position"));
            record.put("Team", (String) player.get("team"));
            record.put("Name", (String) player.get("name"));
            record.put("Quantity", (String) player.get("quantity"));*/

            record.put("Position", player.get_position());
            record.put("Team", player.get_team());
            record.put("Name", player.get_name());
            record.put("Quantity", String.valueOf(player.get_quantity()));

            //values.add(record);
            LineTable sequence =new LineTable(record);
            System.out.println(sequence);
            data.add(sequence);

        }

        System.out.println(data);

        table_collection.setItems(FXCollections.observableArrayList(data));

        //table_collection.setPrefWidth(500);

        //HBox root = new HBox();
        //root.addAll(table);

        //Scene scene = new Scene(root, 500, 500);

    }

   /* public static JSONArray load_collection(){

        //List<Map<String, String>> values = new ArrayList<>();
        JSONArray players=new JSONArray();

        //Gson gson=new Gson();
        //ShoppingCart cart=null;
        String key_load=crea_chiave_load(1);  //qui dovremmo inserire this.user_id, oppure togliere il parametro e farlo come commentato sopra
        System.out.println(key_load);
        //JedisPool pool=new JedisPool("localhost",6379);
        try(Jedis jedis=pool.getResource()){

            Set<String> set_keys=jedis.keys(key_load);

            System.out.println(set_keys);

            Iterator<String> it = set_keys.iterator();
            while (it.hasNext()) {
                String key= it.next();
                String value=jedis.get(key);

                System.out.println(key+" "+value);

                int id=retrieve_id_player(key);

                //JSONObject player=new JSONObject();
                int present=0;
                for(int i=0;i<players.size();i++) {
                        JSONObject player=(JSONObject) players.get(i);

                        if((Integer) player.get("id")==id){
                            present=1;
                            player.put(retrieve_key_info(key),value);
                            break;
                        }
                }
                if(present==0){
                    JSONObject player1=new JSONObject();
                    player1.put("id",id);
                    player1.put(retrieve_key_info(key),value);
                    players.add(player1);
                }


            }
        }
        System.out.println(players);
        return players;
    }

    private static String retrieve_key_info(String key) {

            String[] words=key.split(":");
            return words[4];
    }

    private static int retrieve_id_player(String key) {

                String[] words=key.split(":");
                return Integer.parseInt(words[3]);
    }

    public static void closePool(){
        pool.close();
    }*/
}
