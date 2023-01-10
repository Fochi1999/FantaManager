package it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;

public class UserMongoDriver {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    static MongoCollection<Document> collection;

    public static void openConnection(){
        mongoClient= MongoClients.create(global.MONGO_URI);
        database = mongoClient.getDatabase(global.DATABASE_NAME);
        collection = database.getCollection(global.USERS_COLLECTION_NAME);
    }

    public static void closeConnection(){
        mongoClient.close();
    }
}
