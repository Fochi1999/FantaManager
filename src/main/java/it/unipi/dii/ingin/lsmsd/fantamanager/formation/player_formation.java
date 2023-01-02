package it.unipi.dii.ingin.lsmsd.fantamanager.formation;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.player_collection;
import javafx.beans.property.Property;

public class player_formation {
    String name;
    int id;
    String team;
    float vote;
    public player_formation(player_collection p){
        this.name=p.get_name();
        this.id=p.get_id();
        this.team=p.get_team().substring(0,3);
        this.vote=0;

    }
    public String getName(){
        return name;
    }
    public int getId(){
        return id;
    }
    public String getTeam(){
        return team;
    }
}
