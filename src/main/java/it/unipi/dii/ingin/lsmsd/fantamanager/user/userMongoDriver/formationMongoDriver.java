package it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.ReadPreference;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class formationMongoDriver {



    public static boolean change_formation() {
        MongoClient myClient = MongoClients.create(global.MONGO_URI);
        MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME).withReadPreference(ReadPreference.primary());
        Bson user = Filters.eq("username", global.user.username);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(global.user.formations);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        System.out.println(json);
        Bson update = Updates.set("formations", json);
        try {
            collection.updateOne(user, update);
        }
        catch(Exception e) {
            System.out.println("Error on updating formation for user: " + global.user.username);
            return false;
        }

        myClient.close();
        return true;
    }

    public static boolean insert_formation(String username, formation formation, int matchday) throws ParseException {

        UserMongoDriver.openConnection();
        Bson user = Filters.eq("username", username);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = null;
        try {
            json = ow.writeValueAsString(formation);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //System.out.println(json);
        JSONObject JsonFormation=create_object_formation(json);
        //Document formations=(Document) formation;
        //System.out.println(JsonFormation);
        //JSONObject form=(JSONObject) json;

        Bson update = Updates.set("formations."+matchday, JsonFormation);
        try {
            UserMongoDriver.collection.updateOne(user, update);
        }
        catch(Exception e) {
            System.out.println("Error on updating formation for user: " + username);
            return false;
        }

        //myClient.close();
        UserMongoDriver.closeConnection();
        return true;


    }

    private static JSONObject create_object_formation(String form) throws ParseException {

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(form);

        return json;
    }
}
