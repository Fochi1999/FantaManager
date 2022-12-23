package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.IOException;
import java.net.URL;
import java.util.*;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.LineTable;

import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CollectionController implements Initializable {

    @FXML
    private TableView<LineTable> table_collection;

    @FXML
    private Parent root;

    @FXML
    protected void click_home() throws IOException {

        closePool();

        Stage stage= (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Opening collection page...");
        apertura_pool();
        create_table();
    }



    /*public void show_trades(MongoCursor<Document> result) {

        //showing off trades
        ObservableList<String> list = FXCollections.observableArrayList();
        list.removeAll(list);	//clearing the list


        while(result.hasNext()) {
            Document trade_doc = result.next();
            String player_from = trade_doc.get("player_from").toString();
            String player_to = trade_doc.get("player_to").toString();
            String credits = trade_doc.get("credits").toString();
            String user_from = trade_doc.getString("user_from");
            String trade_output = ">> Players offered: " + player_from + " /// <<  Players wanted: " + player_to +
                    " /// $$$ Credits: " + credits +" - Trade request made by: "+ user_from;
            list.add(trade_output);
        }
        trade_list.getItems().clear();
        trade_list.getItems().addAll(list);
    }*/

    static JedisPool pool;

    static void apertura_pool(){
        pool=new JedisPool("localhost",6379);
    }

    public static String crea_chiave_load(int user_id) {  //solo per fare il retrieve delle info dell-user in questione
        //return "user:"+this.userId+"*";   //dovrebbe essere cosi dopo la creazione di user
        return "user_id:"+user_id+"*";
    }

    public void create_table()
    {

        //TableView<LineTable> table = new TableView<>();
        //List<Map<String, String>> databaseObject = load_collection();
        System.out.println("creazione tabella");

        //TableView<LineTable> table = new TableView<>();
        JSONArray databaseObject = load_collection();

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
            JSONObject player=(JSONObject) databaseObject.get(i);

            System.out.println(player);

            Map<String, String> record = new HashMap<>();
            //record.put("id", (String) player.get("id"));
            record.put("Position", (String) player.get("position"));
            record.put("Team", (String) player.get("team"));
            record.put("Name", (String) player.get("name"));
            record.put("Quantity", (String) player.get("quantity"));
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

    public static JSONArray load_collection(){

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
    }
}
