package it.unipi.dii.ingin.lsmsd.fantamanager.trades;

import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;

import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.UserMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import java.security.NoSuchAlgorithmException;
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

    public static boolean delete_my_trade(ObjectId trade_id){
        openConnection();

        //searching for the trades
        try {
            DeleteResult result = collection.deleteOne(Filters.eq("_id",trade_id));
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("An error has occured while deleting the trade!");
            return false;
        }
        return true;
    }
    public static MongoCursor<Document> retrieve_most_present(String offered_wanted){

    	openConnection();
    	
    	if(offered_wanted.equals("offered")){
            //10 most frequent player offered in completed trades
            Bson match1=match(eq("status",1));
            Bson u=unwind("$card_from");
            Bson group=group("$card_from", Accumulators.sum("count",1));
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
            Bson u=unwind("$card_to");
            Bson group=group("$card_to",Accumulators.sum("count",1));
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

        return resultDoc;
    }

    public static MongoCursor<Document> search_trade(String from_input, String to_input) {

        System.out.println("Searching trades -> offered: "+ from_input + " // wanted: " + to_input);

        openConnection();
        MongoCursor<Document> resultDoc;

        //preparing bsons
        Pattern pattern0 = Pattern.compile(from_input, Pattern.CASE_INSENSITIVE);
        Bson card_from_equal = Filters.regex("card_from", pattern0);

        Pattern pattern1 = Pattern.compile(to_input, Pattern.CASE_INSENSITIVE);
        Bson card_to_equal = Filters.regex("card_to", pattern1);


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

        return resultDoc;

    }

    
    public static MongoCursor<Document> trades_pending(){
        
        openConnection();
        MongoCursor<Document> resultDoc;

        //searching for the trades
        Bson filter = Filters.eq("status",0);
        try {
            resultDoc = collection.find(filter).limit(500).iterator();	//limit(500) because the load will be too much otherwise  //TODO, facciamo limit con un criterio, sennò non va bene
        } catch (Exception e) {
            System.out.println("An error has occured while viewing trades!");
            return null;
        }

        return resultDoc;
    }

    
    public static Trade search_trade_byId(String id_trade) {

        openConnection();
        Document trade_doc;
        Trade trade = null;

        Bson filter = Filters.eq("_id",new ObjectId(id_trade));
        try {
            trade_doc = collection.find(filter).first();
        } catch (Exception e) {
            System.out.println("An error has occured while viewing trades!");
            return null;
        }
        
        String trade_id = trade_doc.get("_id").toString();
        ArrayList<Document> card_from = (ArrayList<Document>) trade_doc.get("card_from");
        ArrayList<Document> card_to = (ArrayList<Document>) trade_doc.get("card_to");
        int credits = Integer.valueOf(trade_doc.get("credits").toString());
        String user_from = trade_doc.getString("user_from");
        trade=new Trade(trade_id,user_from,global.id_user,credits,card_from,card_to,1);

        return trade;
    }

    public static void update_trade(Trade chosen_trade, String field) { 
        openConnection();

        //searching for the trades
        try {
            UpdateResult result = collection.updateOne(eq("_id",new ObjectId(chosen_trade.trade_id)),Updates.set(field,1));
            System.out.println(result);
        } catch (Exception e) {
            System.out.println("An error has occured while deleting the trade!");

        }
    }

    public static void create_new_trade(Trade new_trade){  //TODO finire

            openConnection();

            Document doc = new Document();
            doc.append("user_from", new_trade.user_from);
            doc.append("user_to", new_trade.user_to);
            doc.append("card_from", new_trade.card_from);
            doc.append("card_to", new_trade.card_to);
            doc.append("credits", new_trade.credits);
            doc.append("status", 0);

            collection.insertOne(doc);

            closeConnection();
    }
    
    //deleting all trades involving that user
	public static void delete_all_trades(String username) { //used when deleting an user
    	openConnection();
    	try {
    		collection.deleteMany(Filters.eq("user_from", username));
    		//TradeMongoDriver.collection.deleteMany(Filters.eq("user_to", username));
            collection.deleteMany(Filters.eq("user_to", username));
    	}
    	catch(Exception e) {
    		System.out.println("Error! Cannot delete this user's trades now. Try later");
    		closeConnection();
    		return;
    	}
    	closeConnection();
    }
   
}
