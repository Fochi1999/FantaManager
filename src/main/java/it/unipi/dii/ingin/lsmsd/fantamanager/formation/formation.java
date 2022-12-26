package it.unipi.dii.ingin.lsmsd.fantamanager.formation;

import java.util.ArrayList;

public class formation {
    private ArrayList<player_formation> defenders=new ArrayList<>();
    private ArrayList<player_formation> attackers=new ArrayList<>();
    private ArrayList<player_formation> midfielders=new ArrayList<>();
    private ArrayList<player_formation> bench=new ArrayList<>();
    private player_formation por;
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
}
