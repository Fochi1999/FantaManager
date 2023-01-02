package it.unipi.dii.ingin.lsmsd.fantamanager.user;

import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;

public class user {
    public String username;
<<<<<<< Updated upstream
    String password;
    String region;
    String _id;
    formation[] formations;
    int credits;
    int collection;
    int privilege;
    public user(String nick, String hashPass,String region,String _id, int credits,int collection, int privilege){
        username=nick;
        password=hashPass;
        this.region=region;
        this._id=_id;
        this.credits=credits;
        this.collection=collection;
        this.privilege=privilege;
        formations=new formation[38];
    }
    public user(String nick, String hashPass,String region, int credits,int collection, int privilege){
=======
    public String password;
    public String region;

    public String mail;
    public formation[] formations;
    public int credits;
    public int collection;
    public int liv_priv;

    public user(String nick, String hashPass,String region,String mail, int credits,int collection, int liv_priv){
>>>>>>> Stashed changes
        username=nick;
        password=hashPass;
        this.region=region;
        this.mail=mail;
        //this._id=null;
        this.credits=credits;
        this.collection=collection;
        this.privilege=privilege;
        formations=new formation[38];
    }
<<<<<<< Updated upstream
    public int get_privilege() {
        return privilege;
    }
    public String getUsername(){
        return username;
    }
=======


>>>>>>> Stashed changes
}
