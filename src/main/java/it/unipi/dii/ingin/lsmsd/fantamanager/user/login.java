package it.unipi.dii.ingin.lsmsd.fantamanager.user;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
public class login {

    public static boolean login(String nick, String password){
        boolean ret=true;
        MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME);
        Document user = usersCollection.find(Filters.and(Filters.eq("username", nick), Filters.eq("password", password))).first();
        ret= user!=null;
        global.user=new user(nick,password,null,user.get("_id").toString(),user.getInteger("credits"),0,user.getInteger("liv_priv"));
        System.out.println(global.user._id);
        return ret;
    }
    public static boolean register(String Nick,String Pass){
        MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME);
        Document user = usersCollection.find(Filters.eq("username", Nick)).first();
        if (user != null) {
            return false;
        }
        usersCollection.insertOne(new Document().append("username", Nick).append("password", Pass).append("liv_priv",1).append("region",null).append("credits",100));
        //INIZIALIZZARE LE VARIABILI GLOBAL
        return login(Nick,Pass);
    }
}
