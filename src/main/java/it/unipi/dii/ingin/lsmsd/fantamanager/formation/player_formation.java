package it.unipi.dii.ingin.lsmsd.fantamanager.formation;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import javafx.beans.property.Property;

public class player_formation {
    public String name;
    public int id;
    public String team;
    public float vote;
    public player_formation(card_collection p){
        this.name=p.get_name();
        this.id=p.get_id();
        this.team=p.get_team().substring(0,3);
        this.vote=0;

    }
    public player_formation() {
    //NON RIMUOVERE SERVE A JACKSON
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
