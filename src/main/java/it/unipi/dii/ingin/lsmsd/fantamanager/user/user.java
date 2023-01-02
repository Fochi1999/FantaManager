package it.unipi.dii.ingin.lsmsd.fantamanager.user;

import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;

public class user {
    public String username;
    String password;
    String region;
    String _id;
    formation[] formations;
    int credits;
    int collection;
    int liv_priv;
    public user(String nick, String hashPass,String region,String _id, int credits,int collection, int liv_priv){
        username=nick;
        password=hashPass;
        this.region=region;
        this._id=_id;
        this.credits=credits;
        this.collection=collection;
        this.liv_priv=liv_priv;
        formations=new formation[38];
    }
    public user(String nick, String hashPass,String region, int credits,int collection, int liv_priv){
        username=nick;
        password=hashPass;
        this.region=region;
        this._id=null;
        this.credits=credits;
        this.collection=collection;
        this.liv_priv=liv_priv;
        formations=new formation[38];
    }
    public int get_liv_priv() {
        return liv_priv;
    }
    public String getUsername(){
        return username;
    }
}
