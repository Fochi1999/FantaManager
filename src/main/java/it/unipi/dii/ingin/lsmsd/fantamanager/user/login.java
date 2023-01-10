package it.unipi.dii.ingin.lsmsd.fantamanager.user;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class login {

    public static boolean login(String nick, String password){
        
    	MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME);
        
        Document user_doc = usersCollection.find(Filters.and(Filters.eq("username", nick), Filters.eq("password", password))).first();

        if(user_doc == null) {
        	return false;
        }

        System.out.println(user_doc.get("formations"));
        Document formation= (Document) user_doc.get("formations");
        System.out.println(formation.toJson());


        global.id_user=user_doc.get("_id").toString();
        global.user=new user(nick,password,user_doc.getString("region"),user_doc.getString("email"),user_doc.getInteger("credits"),user_doc.getInteger("_privilege"), user_doc.getInteger("points"),formation.toJson());
        global.saved_formation_server=global.user.formations.get(global.current_matchday);
        global.saved_formation_local=global.saved_formation_server;
        System.out.println("User logged in. ID: " + global.id_user);
        return true;
    }
    
    public static boolean register(String Nick,String Pass,String Email, String Region)  {
    	
        MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME);
        
        Document user = usersCollection.find(Filters.eq("username", Nick)).first();
        if (user != null) {
        	System.out.println("This username is already in use!");
            return false;
        }

        
        /*usersCollection.insertOne(new Document().append("username", Nick).append("password", Pass).append("privilege",1).
        		append("region",Region).append("credits",100).append("points", 0).append("collection",0).append("email",Email)); */

        //INIZIALIZZARE LE VARIABILI GLOBAL


        user u=new user(Nick,Pass,Region,Email,100,0,1);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(u);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(json);
        Document doc = Document.parse( json );
        usersCollection.insertOne(doc);
        System.out.println("User registered.");
        return login(Nick,Pass);
    }
}
