package it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver;

import com.mongodb.ReadPreference;
import com.mongodb.client.*;
import com.mongodb.client.model.Updates;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class UserMongoDriver {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    static MongoCollection<Document> collection;

    public static void openConnection(){
        mongoClient= MongoClients.create(global.MONGO_URI);
        database = mongoClient.getDatabase(global.DATABASE_NAME);
        collection = database.getCollection(global.USERS_COLLECTION_NAME).withReadPreference(ReadPreference.primary());
    }

    public static void closeConnection(){
        mongoClient.close();
    }

    public static String retrieve_user_attribute(String username,String attribute_name){
                openConnection();
                String attribute_value = "";
                try(MongoCursor<Document> result=collection.find(eq("username",username)).iterator()){
                            while(result.hasNext()){
                                if(attribute_name.equals("credits") || attribute_name.equals("collection") || attribute_name.equals("points")) {
                                    //int nv=Integer.parseInt(new_value);
                                    int value=(Integer) result.next().get(attribute_name);
                                    attribute_value=Integer.toString(value);
                                }
                                else {
                                    attribute_value= (String) result.next().get(attribute_name);
                                }
                            }
                }
                closeConnection();
                return attribute_value;
    }
}
