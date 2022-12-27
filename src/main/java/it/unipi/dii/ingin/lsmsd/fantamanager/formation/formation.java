package it.unipi.dii.ingin.lsmsd.fantamanager.formation;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.player_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.ChoisePlayerFormationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class formation {
    public ArrayList<player_formation> players;
    // 0: portiere, poi 11 giocatori a seconda del modulo es 3-4-3, 1-3: D, 4-7:M, 8-10: A, panchina, 11:P, 12-13:D,14-15:M,16-17:A
    int[] modulo;
    public player_formation por;
    private static formation f;
    public formation(int[] m,ArrayList<player_formation> p){
        modulo=new int[3];
        for(int i=0;i<3;i++){
            modulo[i]=m[i];
        }
        for(int i=0;i<p.size();i++){
            players.add(p.get(i));
        }
    }
    public static synchronized void choose_player( ArrayList<player_collection> p) throws IOException {
        Stage stage=new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("choise_player_formation.fxml"));
        ChoisePlayerFormationController.p=p;
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Collection page");
        stage.setScene(scene);
        stage.show();
    }
}
