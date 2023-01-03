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

import java.util.ArrayList;

public class login {

    public static boolean login(String nick, String password){
        
    	MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME);
        
        Document user = usersCollection.find(Filters.and(Filters.eq("username", nick), Filters.eq("password", password))).first();
        if(user == null) {
        	return false;
        }

        
        global.user=new user(nick,password,null,user.get("_id").toString(),user.getInteger("credits"),0,user.getInteger("privilege"));
        System.out.println(global.user._id);

        global.id_user=user.get("_id").toString();
        global.user=new user(nick,password,user.getString("region"),user.getString("mail"),user.getInteger("credits"),0,user.getInteger("liv_priv"));
        ArrayList<formation> formations= (ArrayList<formation>) user.get("formations");
        global.saved_formation_server=formations.get(global.curr_matchday);
        System.out.println(global.id_user);

        return true;
    }
    
    public static boolean register(String Nick,String Pass,String Email, String Region) throws JsonProcessingException {
    	
        MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME);
        
        Document user = usersCollection.find(Filters.eq("username", Nick)).first();
        if (user != null) {
        	System.out.println("This username is already in use!");
            return false;
        }

        
        usersCollection.insertOne(new Document().append("username", Nick).append("password", Pass).append("privilege",1).
        		append("region",Region).append("credits",100).append("points", 0).append("collection",0).append("email",Email));

        //INIZIALIZZARE LE VARIABILI GLOBAL

        /*user u=new user(Nick,Pass,Region,Email,100,0,1);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(u);
        System.out.println(json);
        Document doc = Document.parse( json );
        usersCollection.insertOne(doc);
*/
        return login(Nick,Pass);
    }
}
