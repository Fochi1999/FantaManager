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
        /*  MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME);
        Document user = usersCollection.find(Filters.and(Filters.eq("username", nick), Filters.eq("password", password))).first();
        ret= user!=null;*/
        //INIZIALIZZARE LE VARIABILI GLOBAL
        global.liv_priv=2;
        global.nick=nick;
        return ret;
    }
    public static boolean register(String Nick,String Pass){
        /*MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> usersCollection = database.getCollection(global.USERS_COLLECTION_NAME);
        Document user = usersCollection.find(Filters.eq("username", Nick)).first();
        if (user != null) {
            return false;
        }
        usersCollection.insertOne(new Document().append("username", Nick).append("password", Pass));*/
        //INIZIALIZZARE LE VARIABILI GLOBAL
        global.liv_priv=2;
        global.nick=Nick;
        return true;
    }
}
