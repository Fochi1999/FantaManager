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
    // 0: portiere, poi 11 giocatori a seconda del modulo es 3-4-3, 1-3: D, 4-7:M, 8-10: A, panchina, 11-12:P, 13-14:D,15-16:M,17-18:A
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
    public static void choose_player(ArrayList<player_collection> p, String roles) throws IOException {
        Stage stage=new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("choise_player_formation.fxml"));
        ChoisePlayerFormationController.p=p;
        ChoisePlayerFormationController.role=roles;
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Collection page");
        stage.setScene(scene);
        stage.show();
    }

    public void insert_new_player(player_formation p, String role) {
        //passo dal ruolo alla posizione nell'array che deve avere il giocatore

    }
    public int get_index(player_formation p, String role,int[] mod){
        int index;
        String[] numbers=role.split("-");
        String[] positions= {"P","D","M","A"};
        if(numbers.length==2){
            //TITOLARE
            index=0;
            String posPl=numbers[0];
            int n=Integer.parseInt(numbers[1])-1;
            for(int i=0;i<4;i++){
                if(posPl.equals(positions[i])){
                    return index+n;
                }
                index+=mod[i];
            }

        }
        else{
            index=11;
            String posPl=numbers[1];
            int n=Integer.parseInt(numbers[2])-1;
            for(int i=0;i<4;i++){
                if(posPl.equals(positions[i])){
                    return index+n;
                }
                index+=mod[i];
            }
        }
        return -1;
    }
}
