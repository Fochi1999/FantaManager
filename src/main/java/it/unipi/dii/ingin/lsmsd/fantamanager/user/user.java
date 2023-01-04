package it.unipi.dii.ingin.lsmsd.fantamanager.user;

import java.security.NoSuchAlgorithmException;

import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.hash;

public class user {
    public String username;

    String password;
    String region;
    String _id;
    String mail;
    formation[] formations;
    int credits;
    int collection;
    int points;
    int privilege;

    //constructor
    public user(String nick, String hashPass,String region,String mail, int credits,int collection, int privilege, int points){

        username=nick;
        password=hashPass;
        this.region=region;
        this.mail=mail;
        this.points = points;
        this.credits=credits;
        this.collection=collection;
        this.privilege=privilege;
        formations=new formation[38];
    }
    
    //get attributes
    public int get_privilege() {
        return privilege;
    }
    public String getUsername(){
        return username;
    }
    public int getCredits() {
    	return credits;
    }
    public int getCollection() {
    	return collection;
    }
    public int getPoints() {
    	return points;
    }
    public String getMail() {
    	return mail;
    }
    public String getRegion() {
    	return region;
    }
    
    //change attributes
    public void changePassword(String value) throws NoSuchAlgorithmException{
    	this.password = hash.MD5(value);
    }
    public void changeMail(String value) {
    	this.mail = value;
    }
    public void changeUsername(String value) {
    	this.username = value;
    }
}
