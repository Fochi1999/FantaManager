package it.unipi.dii.ingin.lsmsd.fantamanager.user;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.hash;

public class user {
    public String username;

    String password;
    String region;
    String _id;
    String email;
    public HashMap<Integer,formation> formations;
    int credits;
    int collection;
    int points;
    int privilege;

    //constructor
    public user(String nick, String hashPass,String region,String email, int credits, int privilege, int points){

        username=nick;
        password=hashPass;
        this.region=region;
        this.email=email;
        this.points = points;
        this.credits=credits;
        //this.collection=collection;
        this.privilege=privilege;
        formations=new HashMap<>();
    }
    public user(String nick, String hashPass,String region,String email, int credits, int privilege, int points,String formationJson){

        username=nick;
        password=hashPass;
        this.region=region;
        this.email=email;
        this.points = points;
        this.credits=credits;
        //this.collection=collection;
        this.privilege=privilege;
        if(formationJson!=null){
            try {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<HashMap<Integer, formation>> typeRef = new TypeReference<HashMap<Integer, formation>>() {};
                Map<Integer,formation> map = mapper.readValue(formationJson, typeRef);
                this.formations= new HashMap<Integer, formation>(map);
                System.out.println(this.formations.toString());
            } catch(Exception e){
                //e.printStackTrace();
                this.formations=new HashMap<>();
                System.out.println("formazione non trovata");
            }
        }
    }
    //get attributes
    public int get_privilege() {
        return privilege;
    }
    public String getUsername(){
        return username;
    }
    public String getPassword(){return password;};
    
    public void setCredits(int new_value) {
    	this.credits = new_value;
    }
    public int getCredits() {
    	return credits;
    }
    
    //public int getCollection() {
    	//return collection;
   // }
    //public void setCollection(int new_value) {
    	//this.collection = new_value;
    //}
    
    public int getPoints() {
    	return points;
    }
    public String getEmail() {
    	return email;
    }
    public String getRegion() {
    	return region;
    }
    
    //change attributes
    public void changePassword(String value) throws NoSuchAlgorithmException{
    	this.password = hash.MD5(value);
    }
    public void changeEmail(String value) {
    	this.email = value;
    }
    public void changeUsername(String value) {
    	this.username = value;
    }
}
