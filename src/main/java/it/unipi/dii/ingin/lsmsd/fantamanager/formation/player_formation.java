package it.unipi.dii.ingin.lsmsd.fantamanager.formation;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import javafx.beans.property.Property;

public class player_formation {
    public String name;
    public int id;
    public String team;
    public double vote;
    public player_formation(card_collection p){
        this.name=p.get_name();
        this.id=p.get_id();
        this.team=p.get_team();
        this.vote=0;

    }
    public player_formation() {
    //NON RIMUOVERE SERVE A JACKSON
    }
    public player_formation(String name, int id, String team, double vote) {
        this.name=name;
        this.id=id;
        this.team=team;
        this.vote=vote;
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
