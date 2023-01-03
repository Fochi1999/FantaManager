package it.unipi.dii.ingin.lsmsd.fantamanager.user;

import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;

public class user {
    public String username;

    String password;
    String region;
    String _id;
    String mail;
    formation[] formations;
    int credits;
    int collection;
    int privilege;


    public user(String nick, String hashPass,String region,String mail, int credits,int collection, int privilege){

        username=nick;
        password=hashPass;
        this.region=region;
        this.mail=mail;
        this.credits=credits;
        this.collection=collection;
        this.privilege=privilege;
        formations=new formation[38];
    }

    public int get_privilege() {
        return privilege;
    }
    public String getUsername(){
        return username;
    }

}
