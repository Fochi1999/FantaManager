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
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FormationController implements Initializable {
	
	//Stage stage = new Stage();
    ArrayList<player_collection> players;
    formation f;
    ArrayList<HBox> formationBoxes;
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
    protected void print(ActionEvent event) throws IOException {
        for (HBox h: formationBoxes){
            h.getChildren().clear();
        }
        box_por.getChildren().clear();
        MenuItem scelta= (MenuItem) event.getSource();
        String formationString=scelta.getText();
        System.out.println("E' stata scelta la formazione:");
        String[] numbers=formationString.split("-");
        Button choise_por=new Button("P-1");
        choise_por.getStyleClass().add("choise_player");
        choise_por.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                try {
                    click_deploy_player(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }});
        box_por.getChildren().add(choise_por);
        for(int i=0;i< numbers.length;i++){
            System.out.println(numbers[i]);
            HBox act= formationBoxes.get(i);
            String role;
            if(i==0){
                role="D";
            }
            else if(i==1){
                role="M";
            }
            else{
                role="A";
            }
            role+="-";
            for(int j = 0; j<Integer.parseInt(numbers[i]);j++){
                role+=Integer.toString(j+1);
                Button choise_player=new Button(role);
                choise_player.setOnAction(new EventHandler<ActionEvent>() {
                    @Override public void handle(ActionEvent e) {
                        try {
                            click_deploy_player(e);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }});
                choise_player.getStyleClass().add("choise_player");
                act.getChildren().add(choise_player);
                role=role.substring(0,role.length()-1);
                //aggiungi a ogni Button la lista delle carte presenti nella collection per quel ruolo per i=0 d,1 mid,2 att
            }
            }
        }
    @FXML
    protected void click_home() throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        formationBoxes =new ArrayList<>();
        formationBoxes.add(box_def);
        formationBoxes.add(box_mid);
        formationBoxes.add(box_att);
        f=formation.getFormation(global.id_user);
        collection.create_collection(); //TODO togliere perchè popola il db
        players= collection.load_collection(global.id_user);
    }
    @FXML
    protected synchronized void click_deploy_player(ActionEvent event) throws IOException {

        String role=((Button)event.getSource()).getText();
        System.out.println(role);
        String[] roles=role.split("-");
        String r;
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
        formation.choose_player(r,f,selectables);

    }

    }
