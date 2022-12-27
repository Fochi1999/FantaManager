package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.player_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.player_formation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ChoisePlayerFormationController implements Initializable {
    public static ArrayList<player_collection> p;
    public static int index;
    @FXML
    Parent root;
    @FXML
    TableView table_collection;
    @FXML
    TableColumn id;
    @FXML
    TableColumn squad;
    @FXML
    TableColumn player;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        id.setCellValueFactory(new PropertyValueFactory<player_formation, Integer>("id"));
        squad.setCellValueFactory(new PropertyValueFactory<player_formation, String>("team"));
        player.setCellValueFactory(new PropertyValueFactory<player_formation,String>("name"));
        for(int i=0;i<p.size();i++){
            table_collection.getItems().add(new player_formation(p.get(i)));
        }
    }
    @FXML
    public void click_table(ActionEvent e){
        player_formation p= (player_formation) table_collection.getSelectionModel().getSelectedItem();
        FormationController.f.players.add(index,p);
    }
}
