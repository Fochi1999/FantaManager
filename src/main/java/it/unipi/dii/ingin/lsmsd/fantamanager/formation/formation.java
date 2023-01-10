package it.unipi.dii.ingin.lsmsd.fantamanager.formation;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.ChoisePlayerFormationController;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.utilities;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import static com.almasb.fxgl.core.math.FXGLMath.random;

public class formation {
    public HashMap<Integer,player_formation> players;
    // 0: portiere, poi 11 giocatori a seconda del modulo es 3-4-3, 1-3: D, 4-7:M, 8-10: A, panchina, 11-12:P, 13-14:D,15-16:M,17-18:A
    public int[] modulo;
    public int tot;
    public boolean valid;
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
        tot=0;
    }
    public formation(){
        players=new HashMap<>();
        modulo=new int[4];
        tot=0;
    }
    public static void choose_player(Stage stage, ArrayList<card_collection> p, String roles) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("choise_player_formation.fxml"));
        ChoisePlayerFormationController.p=p;
        ChoisePlayerFormationController.role=roles;
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Collection page");
        stage.setScene(scene);
        stage.show();
    }

    public static void save_formation_server(formation formation) {

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
        String[] positions= {"G","D","M","A"};
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
                index+=2;
            }
        }
        return -1;
    }

    public boolean isValid() {
        int[] ids=new int[19];
        for(int i=0;i<19;i++){
            if(players.get(i)==null){
                return false;
            }
            else{
                ids[i]=players.get(i).getId();
            }
        }
        return(!utilities.has_duplicates(ids));
    }
    public static formation getRandomFormation(ArrayList<card_collection> Cards){
        int[] mod=new int[4];
        ArrayList<player_formation>Attackers=new ArrayList<>();
        ArrayList<player_formation>Midfielders=new ArrayList<>();
        ArrayList<player_formation>Defenders=new ArrayList<>();
        ArrayList<player_formation>Gks=new ArrayList<>();
        for(int j=0;j<Cards.size();j++){
            card_collection card=Cards.get(j);
            if(card.get_position()=="Attacker"){
                Attackers.add(new player_formation(card));
            }
            if(card.get_position()=="Midfielder"){
                Midfielders.add(new player_formation(card));
            }
            if(card.get_position()=="Defender"){
                Defenders.add(new player_formation(card));
            }
            if(card.get_position()=="Goalkeeper"){
                Gks.add(new player_formation(card));
            }
        }
        int nAtt=Attackers.size();
        int nMid=Midfielders.size();
        int nDef=Defenders.size();
        int nGks=Gks.size();
        mod=get_casual_module();
        int casAtt=utilities.getRandomInt(0,nAtt-mod[3]-1);
        int casMid=utilities.getRandomInt(0,nMid-mod[2]-1);
        int casDef=utilities.getRandomInt(0,nDef-mod[1]-1);
        int casGk=utilities.getRandomInt(0,nGks-mod[0]-1);
        formation f=new formation(mod);
        for(int i=0;i<4;i++){
            String role="";
            if(i==0){
                role="P-";
            }
            else if(i==1){
                role="D-";
            }
            else if(i==2){
                role="M-";
            }
            else if(i==3){
                role="A-";
            }
            for(int j=0;j<mod[i];j++){
                role=role+(j+1);
                if(i==0){
                    f.insert_new_player(Gks.get(casGk),role);
                }
                else if(i==1){
                    f.insert_new_player(Defenders.get(casDef+j),role);
                }
                else if(i==2){
                    f.insert_new_player(Defenders.get(casMid+j),role);
                }
                else if(i==3){
                    f.insert_new_player(Defenders.get(casAtt+j),role);
                }
            }
        }
        for(int i=0;i<8;i++){
            String role="S-";
            if(i<2){
                role=role+"P-"+(i+1);
                f.insert_new_player(Gks.get(casGk+mod[0]+i),role);
            }
            else if(i<4){
                role=role+"D-"+(i+1);
                f.insert_new_player(Defenders.get(casDef+mod[1]+i),role);
            }
            else if(i<6){
                role=role+"M-"+(i+1);
                f.insert_new_player(Midfielders.get(casMid+mod[2]+i),role);
            }
            else{
                role=role+"A-"+(i+1);
                f.insert_new_player(Attackers.get(casAtt+mod[3]+i),role);
            }
        }
        return f;
    }

    private static int[] get_casual_module() {
        int[][] mod={
                { 1, 4,4,2 },
                { 1, 4,5,1 },
                { 1, 4,3,3 },
                { 1, 5,4,1 },
                { 1, 5,3,2 },
                { 1, 3,5,2 },
                { 1, 3,4,3 }
        };
        int nCasuale=(int) ((Math.random() * (7 - 0)));
        return mod[nCasuale];
    }
}
