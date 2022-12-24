package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FormationController implements Initializable {
	
	//Stage stage = new Stage();
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
        ChoiceBox choise_por=new ChoiceBox();
        choise_por.getStyleClass().add("choise_player");
        box_por.getChildren().add(choise_por);
        for(int i=0;i< numbers.length;i++){
            System.out.println(numbers[i]);
            HBox act= formationBoxes.get(i);
            for(int j = 0; j<Integer.parseInt(numbers[i]);j++){
                ChoiceBox choise_player=new ChoiceBox();
                choise_player.getStyleClass().add("choise_player");
                act.getChildren().add(choise_player);
                //aggiungi a ogni ChoiceBox la lista delle carte presenti nella collection per quel ruolo per i=0 d,1 mid,2 att
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

    }

    }