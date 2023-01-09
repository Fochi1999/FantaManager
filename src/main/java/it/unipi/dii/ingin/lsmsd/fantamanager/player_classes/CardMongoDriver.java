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

import static com.mongodb.client.model.Accumulators.first;
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

    public static ArrayList<Document> retrieve_cards(String cards_input){
        //connecting to mongoDB
        openConnection();
        ArrayList<Document> resultList = new ArrayList<>();
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
        
        //add to arraylist
        while(resultDoc.hasNext()) {
        	resultList.add(resultDoc.next());
        }
        
        return resultList;
    }

    public static ArrayList<Document> search_card_by(String skill, String team, String role){
        //connecting to mongoDB
        openConnection();
        ArrayList<Document> result = new ArrayList<>();
        MongoCursor<Document> resultDoc = null;
        
        if(skill==null && role==null && team==null) {
            //non faccio nulla
            System.out.println("Inserisci valori");
        } else if(skill!=null && role!=null && team==null){

            System.out.println("skill and role");

            //primo per ogni squadra per quel ruolo per quella skill

            //Bson group= new Document("$group",new Document("team","$team").append("fullname",new Document("$first","$fullname"))
              //      .append("credits",new Document("$first","$credits")).append("_id",new Document("$first","$_id")).append("position",new Document("$first","$position")).append("team",new Document("$first","$team")));
            Bson group=group("$team", first("fullname","$fullname"), first("credits","$credits"), first("position","$position"),
                    first("team","$team"), first("id","$_id"));
            Bson match=match(eq("position",role));
            Bson order=sort(descending((String) skill));
            Bson p1=project(fields(excludeId(),include("fullname"),include("credits"),computed("_id","$id"),include("team"),include("position")));
            //Bson first_five=limit(5);



                try{
                    resultDoc = collection.aggregate(Arrays.asList(match,order,group,p1)).iterator();
                    //while (resultDoc.hasNext()) {
                      //  System.out.println(resultDoc.next().toJson());
                    //}
                } catch (Exception e) {
                    return null;
                }

                

        } else if (skill!=null && team!=null && role==null) {

            //primo per ogni ruolo di una determinata squadra con un determinato skill

            //Bson groupMultiple= new Document("$group",new Document("_id",new Document("position","$position").append("team","$team")).append("fullname",new Document("$first","$fullname"))
                    //.append("credits",new Document("$first","$credits")).append("id",new Document("$first","$_id")).append("position",new Document("$first","$position")).append("team",new Document("$first","$team")));
            Bson group=group("$position", first("fullname","$fullname"), first("credits","$credits"), first("position","$position"),
                    first("team","$team"), first("id","$_id"));
            Bson match=match(eq("team",team));
            Bson order=sort(descending((String) skill));
            Bson p1=project(fields(excludeId(),include("fullname"),include("credits"),computed("_id","$id"),include("team"),include("position")));
            //Bson first_five=limit(5);

            try{
            	resultDoc = collection.aggregate(Arrays.asList(match,order,group,p1)).iterator();
            } catch (Exception e) {
                return null;
            }

        } else if (skill!=null && team==null && role==null) {


            //search_by_skill.setDisable(false);


            //miglior giocatore per quella skill, per ogni team, per ogni ruolo

            Bson groupMultiple= new Document("$group",new Document("_id",new Document("position","$position").append("team","$team")).append("fullname",new Document("$first","$fullname"))
                    .append("credits",new Document("$first","$credits")).append("id",new Document("$first","$_id")).append("position",new Document("$first","$position")).append("team",new Document("$first","$team")));
            //Bson match=match(eq("_id.position",role));
            Bson order=sort(descending((String) skill));
            Bson p1=project(fields(excludeId(),include("fullname"),include("credits"),computed("_id","$id"),include("team"),include("position")));
            //Bson first_five=limit(5);

            try{
            	resultDoc = collection.aggregate(Arrays.asList(order,groupMultiple,p1)).iterator();
            } catch (Exception e) {
                return null;
            }

        } else if (skill!=null && team!=null && role!=null) {

            //prende primi 2 di una stessa squadra e position in base ad una certa skill
            Bson match1=match(eq("team",team));
            Bson match2=match(eq("position",role));
            Bson order=sort(descending((String) skill));
            Bson first_five=limit(2);


                try{
                	resultDoc = collection.aggregate(Arrays.asList(match1,match2,order,first_five)).iterator();
                } catch (Exception e) {
                    return null;
                }

        }
        else if(skill==null && team!=null && role!=null){

            Bson match1=match(eq("team",team));
            Bson match2=match(eq("position",role));

            try{
            	resultDoc = collection.aggregate(Arrays.asList(match1,match2)).iterator();
            } catch (Exception e) {
                return null;
            }
        } else if (skill==null && team==null && role!=null) {

            Bson match2=match(eq("position",role));

            try{
                resultDoc = collection.aggregate(Arrays.asList(match2)).iterator();
            } catch (Exception e) {
                return null;
            }
        }
        else{
                //dovrebbe essere caso in cui ho selezionato solo team
            Bson match2=match(eq("team",team));

            try{
                resultDoc = collection.aggregate(Arrays.asList(match2)).iterator();
            } catch (Exception e) {
                return null;
            }
        }

        while(resultDoc.hasNext()) {
        	result.add(resultDoc.next());
        }
        return result;
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
