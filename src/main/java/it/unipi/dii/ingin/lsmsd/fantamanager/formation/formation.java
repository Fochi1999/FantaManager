package it.unipi.dii.ingin.lsmsd.fantamanager.formation;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.player_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.ChoisePlayerFormationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class formation {
    public HashMap<Integer,player_formation> players;
    // 0: portiere, poi 11 giocatori a seconda del modulo es 3-4-3, 1-3: D, 4-7:M, 8-10: A, panchina, 11-12:P, 13-14:D,15-16:M,17-18:A
    public int[] modulo;
    public formation(int[] m,ArrayList<player_formation> p){
        modulo=new int[3];
        for(int i=0;i<3;i++){
            modulo[i]=m[i];
        }
        players=new HashMap<>();
        for(int i=0;i<3;i++){
            players.put(i,p.get(i));
        }
    }
    public formation(int[] m){
        modulo=new int[4];
        for(int i=0;i<4;i++){
            modulo[i]=m[i];
        }
        players=new HashMap<>();
    }
    public static void choose_player(Stage stage, ArrayList<player_collection> p, String roles) throws IOException {

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
        int index=get_index(role, modulo);
        players.put(index,p);
        System.out.println("inserito il player "+p.getName()+" nella posizione di "+role+"corrispondente all'indice "+ index);

    }
    public static int get_index( String role,int[] mod){
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
