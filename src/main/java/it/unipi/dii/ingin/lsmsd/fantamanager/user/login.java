package it.unipi.dii.ingin.lsmsd.fantamanager.user;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;

public class login {

    public static boolean login(String nick, String password){
        
    	MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME).withReadPreference(ReadPreference.primary());
        Document user_doc = usersCollection.find(Filters.and(Filters.eq("username", nick), Filters.eq("password", password))).first();

        if(user_doc == null) {
        	return false;
        }

        System.out.println(user_doc.get("formations"));
        Document formation = (Document) user_doc.get("formations");
        //Document formation_for_next_match=(Document) formation.get(global.next_matchday);
        //System.out.println(formation_for_next_match.toJson());


        global.id_user=user_doc.get("_id").toString();
        global.user=new user(nick,password,user_doc.getString("region"),user_doc.getString("email"),user_doc.getInteger("credits"),user_doc.getInteger("_privilege"), user_doc.getInteger("points"),formation.toJson());
        global.saved_formation_server=global.user.formations.get(global.next_matchday);
        global.saved_formation_local=global.saved_formation_server;
        System.out.println("User logged in. ID: " + global.id_user);
        return true;
    }
    
    public static boolean register(String Nick,String Pass,String Email, String Region)  {
    	
        MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME).withReadPreference(ReadPreference.primary());
        
        Document user = usersCollection.find(Filters.eq("username", Nick)).first();
        if (user != null) {
        	System.out.println("This username is already in use!");
            return false;
        }

        
        /*usersCollection.insertOne(new Document().append("username", Nick).append("password", Pass).append("privilege",1).
        		append("region",Region).append("credits",100).append("points", 0).append("collection",0).append("email",Email)); */

        //INIZIALIZZARE LE VARIABILI GLOBAL


        user u=new user(Nick,Pass,Region,Email,600,1,0);
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
    
    
    public static void logout(){
    	
    	//resetting global variables
    	global.user = null;
    	global.nick = null;
    	global.id_user = null;
    	global.saved_formation_local = null;
    	global.saved_formation_server = null;
    	global.owned_cards_list = null;
   
    	System.out.println("User logged out.");
    }
    
}
