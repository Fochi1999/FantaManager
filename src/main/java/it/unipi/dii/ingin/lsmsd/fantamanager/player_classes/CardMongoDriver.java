package it.unipi.dii.ingin.lsmsd.fantamanager.player_classes;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.Trade;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.descending;
import static com.mongodb.client.model.Projections.*;

public class CardMongoDriver {

    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static MongoCollection<Document> collection;

    private static void openConnection(){
        mongoClient= MongoClients.create(global.MONGO_URI);
        database = mongoClient.getDatabase(global.DATABASE_NAME);
        collection = database.getCollection(global.CARDS_COLLECTION_NAME);
    }

    public static void closeConnection(){
        mongoClient.close();
    }

    public static MongoCursor<Document> retrieve_cards(String cards_input){
        //connecting to mongoDB
        openConnection();

        MongoCursor<Document> resultDoc;

        //blank search field
        if(cards_input.equals("")) {

            try {
                resultDoc = collection.find().iterator();
            }
            catch(Exception e) {
                System.out.println("Error on search.");
                return null;
            }
        }
        else {
            //filter
            Pattern pattern = Pattern.compile(cards_input, Pattern.CASE_INSENSITIVE);
            Bson filter = Filters.regex("fullname", pattern);

            try {
                resultDoc = collection.find(filter).iterator();
            }
            catch(Exception e) {
                System.out.println("Error on search.");
                return null;
            }
        }
        return resultDoc;
    }

    public static MongoCursor<Document> search_card_by(String skill, String team, String role){
        //connecting to mongoDB
        openConnection();

        if(skill==null && role==null && team==null) {
            //non faccio nulla
            System.out.println("Inserisci valori");
        } else if(skill!=null && role!=null && team==null){



            Bson groupMultiple= new Document("$group",new Document("_id",new Document("position","$position").append("team","$team")).append("fullname",new Document("$first","$fullname"))
                    .append("credits",new Document("$first","$credits")).append("id",new Document("$first","$_id")).append("position",new Document("$first","$position")).append("team",new Document("$first","$team")));
            Bson match=match(eq("_id.position",role));
            Bson order=sort(descending((String) skill));
            //Bson first_five=limit(5);



                try{
                    return collection.aggregate(Arrays.asList(order,groupMultiple,match)).iterator();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }



        } else if (skill!=null && team!=null && role==null) {


            Bson groupMultiple= new Document("$group",new Document("_id",new Document("position","$position").append("team","$team")).append("fullname",new Document("$first","$fullname"))
                    .append("credits",new Document("$first","$credits")).append("id",new Document("$first","$_id")).append("position",new Document("$first","$position")).append("team",new Document("$first","$team")));
            Bson match=match(eq("_id.team",team));
            Bson order=sort(descending((String) skill));
            //Bson first_five=limit(5);

            try{
                return collection.aggregate(Arrays.asList(order,groupMultiple,match)).iterator();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (skill!=null && team==null && role==null) {


            //search_by_skill.setDisable(false);


            //miglior giocatore per quella skill, per ogni team, per ogni ruolo
            Bson groupMultiple= new Document("$group",new Document("_id",new Document("position","$position").append("team","$team")).append("fullname",new Document("$first","$fullname"))
                    .append("credits",new Document("$first","$credits")).append("id",new Document("$first","$_id")).append("position",new Document("$first","$position")).append("team",new Document("$first","$team")));
            //Bson match=match(eq("_id.position",role));
            Bson order=sort(descending((String) skill));
            //Bson first_five=limit(5);

            try{
                return collection.aggregate(Arrays.asList(order,groupMultiple)).iterator();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else if (skill!=null && team!=null && role!=null) {

            //prende primi 5 di una stessa squadra e position in base ad una certa skill
            Bson match1=match(eq("team",team));
            Bson match2=match(eq("position",role));
            Bson order=sort(descending((String) skill));
            Bson first_five=limit(2);


                try{
                    return collection.aggregate(Arrays.asList(match1,match2,order,first_five)).iterator();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

        }
        else{
            //dovrebbe essere il caso con skill non selezionata e gli altri due si
            Bson match1=match(eq("team",team));
            Bson match2=match(eq("position",role));

            try{
                return collection.aggregate(Arrays.asList(match1,match2)).iterator();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }


    public static card_collection search_player_by_name(String name) {

        openConnection();
        MongoCursor<Document> resultDoc;
        card_collection player=null;

        Bson filter = Filters.eq("fullname",name);
        try {
            resultDoc = collection.find(filter).iterator();
        } catch (Exception e) {
            System.out.println("An error has occured while viewing trades!");
            return null;
        }
        while(resultDoc.hasNext()){
            Document player_doc = resultDoc.next();
            Integer player_id = (Integer) player_doc.get("player_id");
            String fullname=(String) player_doc.get("fullname");
            String team = player_doc.getString("team");
            String position = player_doc.getString("position");
            player=new card_collection(player_id,fullname,1,team,position);
            //update_status_trade();  //TODO

        }
        return player;
    }

    public static MongoCursor<Document> retrieve_player_for_trade(){
        openConnection();

        MongoCursor<Document> resultDoc;

        //blank search field


            try {
                resultDoc = collection.find().projection(fields(include("player_id","fullname","team","position"))).iterator();
            }
            catch(Exception e) {
                System.out.println("Error on search.");
                return null;
            }

            return resultDoc;
    }

}
