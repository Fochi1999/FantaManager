package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;


import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.player_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FormationController implements Initializable {
	
	//Stage stage = new Stage();
    ArrayList<player_collection> players;
    static formation f;
    ArrayList<HBox> formationBoxes;
    @FXML
    private VBox Bench;
    @FXML
    private static Parent root;
    @FXML
    private HBox box_att;
    @FXML
    private HBox box_mid;
    @FXML
    private HBox box_def;
    @FXML
    private HBox box_por;

    @FXML
    public static void player_selected(String role) {
        Scene scene=root.getScene();
        scene.lookup("#"+role);
    }

    @FXML
    protected void print(ActionEvent event) throws IOException {
        for (HBox h: formationBoxes){
            h.getChildren().clear();
        }
        Bench.getChildren().clear();
        box_por.getChildren().clear();
        MenuItem scelta= (MenuItem) event.getSource();
        String formationString=scelta.getText();
        formationString="1-"+formationString; //aggiungo il portiere
        System.out.println("E' stata scelta la formazione:");
        String[] numbers=formationString.split("-");
        for(int i=0;i< numbers.length;i++){
            System.out.println(numbers[i]);
            HBox act= formationBoxes.get(i);
            String role;
            if(i==0){
                role="P";
            }
            else if(i==1){
                role="D";
            }
            else if(i==2){
                role="M";
            }
            else{
                role="A";
            }
            role+="-";
            for(int j = 0; j<Integer.parseInt(numbers[i]);j++){
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
        choise_player.getStyleClass().add("choise_player");
        return choise_player;
    }



    @FXML
    protected void click_home() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        formationBoxes =new ArrayList<>();
        formationBoxes.add(box_por);
        formationBoxes.add(box_def);
        formationBoxes.add(box_mid);
        formationBoxes.add(box_att);
        f=(global.saved_formation);
        if(f==null){
            //TODO creare formazione vuota
        }
        collection.create_collection(); //TODO togliere perchè popola il db
        players= collection.load_collection(global.id_user);
        this.root=root;
    }
    @FXML
    protected synchronized void click_deploy_player(ActionEvent event) throws IOException {

        String role=((Button)event.getSource()).getText();
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

        ArrayList<player_collection> selectables=new ArrayList<>();
        for(int i=0;i<players.size();i++){
            if(players.get(i).get_position().equals(r)) {
                selectables.add(players.get(i));
            }
        }


        formation.choose_player(selectables,role);

    }

    }
