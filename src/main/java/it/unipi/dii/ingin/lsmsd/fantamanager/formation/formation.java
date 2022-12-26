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
    public ArrayList<player_formation> defenders=new ArrayList<>();
    public ArrayList<player_formation> attackers=new ArrayList<>();
    public ArrayList<player_formation> midfielders=new ArrayList<>();
    public ArrayList<player_formation> bench=new ArrayList<>();
    public player_formation por;
    private static formation f;
    public static formation getFormation(int user_id){
        if(f==null){
            f=new formation(user_id);
        }
        return f;
    }
    private formation(int user_id){
        //TODO get formation da mongoDB utilizzando mathday_next
        this.por=null;
    }
    public static void del_formation(){
        f=null;
    }
    public static synchronized void choose_player(String role, formation f, ArrayList<player_collection> p) throws IOException {
        Stage stage=new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("choise_player_formation.fxml"));
        ChoisePlayerFormationController.p=p;
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Collection page");
        stage.setScene(scene);
        stage.show();
    }
}
