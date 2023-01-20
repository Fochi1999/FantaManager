package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.formation;


import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collectionRedisDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.player_formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.formationMongoDriver;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import org.json.simple.parser.ParseException;

public class FormationController implements Initializable {
	
	//Stage stage = new Stage();
    ArrayList<card_collection> players;

    ArrayList<HBox> formationBoxes;
    @FXML
    private Text module_text;
    @FXML
    private Text err_mess;
    @FXML
    private VBox Bench;
    @FXML
    private Parent root;
    @FXML
    private HBox box_att;
    @FXML
    private HBox box_mid;
    @FXML
    private HBox box_def;
    @FXML
    private HBox box_por;



    @FXML
    protected void click_choose_formation(ActionEvent event) throws IOException {
        for (HBox h: formationBoxes){
            h.getChildren().clear();
        }
        Bench.getChildren().clear();
        box_por.getChildren().clear();
        MenuItem scelta= (MenuItem) event.getSource();
        String formationString=scelta.getText();

        formationString="1-"+formationString; //aggiungo il portiere
        System.out.println("Chosen formation: ");
        String[] modulo=formationString.split("-");
        int[] mod=new int[4];
        for(int i=0;i<4;i++){
            mod[i]=Integer.parseInt(modulo[i]);
        }
        global.saved_formation_local=new formation(mod); //creo una nuova formazione con player vuoti e come modulo il modulo inserito
        create_layout_formation(modulo);
    }
    public void create_layout_formation(String[] modulo ){
        module_text.setText(modulo[1]+modulo[2]+modulo[3]);
        for(int i=0;i< modulo.length;i++){
            System.out.println(modulo[i]);
            HBox act= formationBoxes.get(i);
            String role;
            if(i==0){
                role="G";    //Goalkeeper
            }
            else if(i==1){
                role="D";    //Defender
            }
            else if(i==2){
                role="M";    //Midfielder
            }
            else{
                role="A";   //Attacker
            }
            role+="-";
            for(int j = 0; j<Integer.parseInt(modulo[i]);j++){
                role+=Integer.toString(j+1);
                Button choise_player=create_button_player(role);
                act.getChildren().add(choise_player);
                role=role.substring(0,role.length()-1);
            }
            role="S-"+role;
            for(int j=1;j<3;j++){
                role=role+Integer.toString(j);
                Button choise_player=create_button_player(role);
                Bench.getChildren().add(choise_player);
                role=role.substring(0,role.length()-1);
            }
        }

    }
    public Button create_button_player(String role){
        Button choise_player=new Button(role);
        choise_player.setId(role);
        choise_player.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    click_deploy_player(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }});
        int index=formation.get_index(role,global.saved_formation_local.module);
        player_formation p=global.saved_formation_local.players.get(index);
        if(p!=null){
            choise_player.setText(p.getName());
        }
        choise_player.getStyleClass().add("choise_player");
        return choise_player;
    }


    @FXML
    protected void click_save_formation() throws IOException, ParseException {

        if(global.saved_formation_local.isValid()){
            
            global.user.formations.put(global.next_matchday,global.saved_formation_local);
            //formationMongoDriver.change_formation();
            formationMongoDriver.insert_formation(global.user.username,global.user.formations.get(global.next_matchday),global.next_matchday);
            show_error_message("Formation saved");
        }
        else {
            show_error_message("Invalid formation");
        }
    }
    @FXML
    protected void click_home() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	err_mess.setText("");
        formationBoxes =new ArrayList<>();
        formationBoxes.add(box_por);
        formationBoxes.add(box_def);
        formationBoxes.add(box_mid);
        formationBoxes.add(box_att);
        formation f=global.saved_formation_local;
        if(f!=null){
            //inizializzare la formazione
            System.out.println("The saved formation has module: ");
            String[] moduleString=new String[4];
            for(int i=0;i<4;i++){
                moduleString[i]=Integer.toString(f.module[i]);
                System.out.println(moduleString[i]+" ");

            }

            for(int i=0;i<19;i++) {
                player_formation p = f.players.get(i);
                if(p!=null){
                    System.out.println("index:"+i+" player: "+p.getName());
                }
            }
            create_layout_formation(moduleString);

        }

        players= global.owned_cards_list;

    }
    @FXML
    protected synchronized void click_deploy_player(ActionEvent event) throws IOException {

        String role=((Button)event.getSource()).getId();
        System.out.println(role);
        String[] roles=role.split("-");
        String r;
        String p;
        if(roles.length==2){
            //titolare
           r=roles[0];

        }
        else{
            //panchinaro
            r=roles[1];
        }

        ArrayList<card_collection> selectables=new ArrayList<>();
        for(int i=0;i<players.size();i++){

            if(players.get(i).get_position().substring(0,1).equals(r)) {

                selectables.add(players.get(i));

            }
        }

        formation.choose_player((Stage)root.getScene().getWindow(),selectables,role);

    }
    private void show_error_message(String err) {
        err_mess.setText(err);
    }

    }


