package it.unipi.dii.ingin.lsmsd.fantamanager.trades;

import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.descending;

public class TradeMongoDriver {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static  MongoCollection<Document> collection;

    private static void openConnection(){
        mongoClient= MongoClients.create(global.MONGO_URI);
        database = mongoClient.getDatabase(global.DATABASE_NAME);
        collection = database.getCollection(global.TRADES_COLLECTION_NAME);
    }

    public static void closeConnection(){
            mongoClient.close();
    }

    public static void delete_my_trade(ObjectId trade_id){
        openConnection();

        //searching for the trades
        try {
            DeleteResult result = collection.deleteOne(Filters.eq("_id",trade_id));
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("An error has occured while deleting the trade!");
            return;
        }
    }
    public static MongoCursor<Document> retrieve_most_present(String offered_wanted){

        /*MongoClient mongoClient= MongoClients.create(global.MONGO_URI);
        MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);*/

        openConnection();

        if(offered_wanted.equals("offered")){
            //10 most frequent player offered in completed trades
            Bson match1=match(eq("status",1));
            Bson u=unwind("$player_from");
            Bson group=group("$player_from", Accumulators.sum("count",1));
            Bson order=sort(descending("count"));
            Bson limit=limit(20);

            try{
                return collection.aggregate(Arrays.asList(match1,u,group,order,limit)).iterator();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }else{

            //10 most frequent player wanted in completed trades
            Bson match1=match(eq("status",1));
            Bson u=unwind("$player_to");
            Bson group=group("$player_to",Accumulators.sum("count",1));
            Bson order=sort(descending("count"));
            Bson limit=limit(20);

            try{
                return collection.aggregate(Arrays.asList(match1,u,group,order,limit)).iterator();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


    }

    public static MongoCursor<Document> search_user(String my_user) {

        System.out.println("Searching trades made by: " + my_user);

        //connecting to mongoDB

        /*MongoClient myClient = MongoClients.create(global.MONGO_URI);
        MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);*/
        openConnection();
        MongoCursor<Document> resultDoc;

        //preparing bson
        Bson user_equal = Filters.eq("user_from", my_user);

        //searching for the trades
        try {
            resultDoc = collection.find(user_equal).iterator();

        } catch (Exception e) {
            System.out.println("An error has occured while viewing trades!");
            return null;
        }

        //myClient.close();
        //print
        return resultDoc;
        //show_trades(resultDoc);

    }

    public static MongoCursor<Document> search_trade(String from_input, String to_input) {

        System.out.println("Searching trades -> offered: "+ from_input + " // wanted: " + to_input);

        //connecting to mongoDB
        /*MongoClient myClient = MongoClients.create(global.MONGO_URI);
        MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);*/
        openConnection();
        MongoCursor<Document> resultDoc;

        //preparing bsons
        Pattern pattern0 = Pattern.compile(from_input, Pattern.CASE_INSENSITIVE);
        Bson card_from_equal = Filters.regex("player_from", pattern0);

        Pattern pattern1 = Pattern.compile(to_input, Pattern.CASE_INSENSITIVE);
        Bson card_to_equal = Filters.regex("player_to", pattern1);


        //searching for the trades
        try {
            if (!from_input.isEmpty() && !to_input.isEmpty()) { 	//both inputs
                resultDoc = collection.find(Filters.and(card_from_equal,card_to_equal)).iterator();
            }
            else if(!from_input.isEmpty()) { 						//only user input
                resultDoc = collection.find(card_from_equal).iterator();
            }
            else if(!to_input.isEmpty()) { 						//only card input
                resultDoc = collection.find(card_to_equal).iterator();
            }
            else {													//no inputs
                System.out.println("No elements to search....");
                return null;
            }

        } catch (Exception e) {
            System.out.println("An error has occured while viewing trades!");
            return null;
        }

        //print
        //myClient.close();
        return resultDoc;

    }

    public static MongoCursor<Document> trades_pending(){
        /*MongoClient myClient = MongoClients.create(global.MONGO_URI);
        MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);*/
        openConnection();
        MongoCursor<Document> resultDoc;

        //searching for the trades
        Bson filter = Filters.eq("status",0);
        try {
            resultDoc = collection.find(filter).iterator();
        } catch (Exception e) {
            System.out.println("An error has occured while viewing trades!");
            return null;
        }

        //myClient.close();
        return resultDoc;
    }

    public static Trade search_trade_byId(String id_trade) {

        openConnection();
        MongoCursor<Document> resultDoc;
        Trade trade = null;

        Bson filter = Filters.eq("_id",new ObjectId(id_trade));
        try {
            resultDoc = collection.find(filter).iterator();
        } catch (Exception e) {
            System.out.println("An error has occured while viewing trades!");
            return null;
        }
        while(resultDoc.hasNext()){
            Document trade_doc = resultDoc.next();
            String trade_id = trade_doc.get("_id").toString();
            ArrayList<String> player_from = (ArrayList<String>) trade_doc.get("player_from");
            ArrayList<String> player_to = (ArrayList<String>) trade_doc.get("player_to");
            Integer credits = (Integer) trade_doc.get("credits");
            String user_from = trade_doc.getString("user_from");
            trade=new Trade(trade_id,user_from,global.id_user,credits,player_from,player_to,1);
            //update_status_trade();  //TODO

        }
        return trade;
    }
}
