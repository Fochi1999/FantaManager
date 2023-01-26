package it.unipi.dii.ingin.lsmsd.fantamanager.player_classes;

import com.mongodb.ReadPreference;
import org.bson.Document;
import java.util.Iterator;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.json.JSONArray;
import org.json.JSONObject;
import org.bson.types.ObjectId;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;

public class see_card {

	
	public static Document search_card(ObjectId card_id) {
		
		Document card_doc;
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.CARDS_COLLECTION_NAME).withReadPreference(ReadPreference.nearest());
		
		try {
			card_doc = collection.find(Filters.eq("_id", card_id)).first();
		}
		catch (Exception e) {
			System.out.println("Error on searching card");
			myClient.close();
			return null;
		}
		
		myClient.close();
		return card_doc;	
	}
	
	
	public static String get_general_info(Document card_doc) {
		String output = "";
		
		//retrieving info
		String firstname = card_doc.getString("firstname");
		String lastname = card_doc.getString("lastname");
		String age = card_doc.get("age").toString();
		String birthdate = card_doc.get("birth_date").toString();
		String birthplace = card_doc.getString("birth_place");
		String birthcountry = card_doc.getString("birth_country");
		String height = card_doc.getString("height");
		String weight = card_doc.getString("weight");
				
		output = "First name: " + firstname + "\nLast name: " + lastname + "\nAge: " + age +
				 "\nHeight: " + height + "\nWeight: " + weight + "\nBirth date: " + birthdate +
				 "\nBirth place: " + birthplace + "\nBirth country: " + birthcountry;    	
		
		//handling the career attribute (some cards doesn't have it)
		Object career = card_doc.get("career");
		if(career != null) {
			output = output + "\nCareer: " + career.toString();
		}
		
		return output;
	}
	
	public static String get_general_stats(Document card_doc){
		
		String output = "";
		JSONObject card = new JSONObject(card_doc.toJson());
	    JSONObject stats = card.getJSONObject("general_statistics");
	    
	    int passes_accuracy = 0;
	    if(!stats.get("games_appearences").toString().equals("0")) {
	    	passes_accuracy = ((Integer.parseInt(stats.get("passes_accuracy").toString()))/(Integer.parseInt(stats.get("games_appearences").toString())));
	    }
	    
	    output = "Games Appearences: "+stats.get("games_appearences").toString() + "\nGames Lineup: " + stats.get("games_lineup").toString() +
	    		"\nGames Minutes: "+ stats.get("games_minutes").toString() + "\nSubstitutes In: " + stats.get("substitutes_in").toString() +
	    		"\nSubstitutes Out: " + stats.get("substitutes_out").toString() +/* "\nSubstitutes Bench: " + stats.get("substitutes_bench").toString() +*/
	    		"\nShots Total: " + stats.get("shots_total").toString() + "\nShots On: " + stats.get("shots_on").toString() +
	    		"\nGoals Total: "+ stats.get("goals_total").toString() + "\nGoals Conceded: " + stats.get("goals_conceded").toString() +
	    		"\nGoals Assists: " + stats.get("goals_assists").toString() + "\nGoals Saves: "+stats.get("goals_saves").toString() +
	    		"\nPasses Total: "+stats.get("passes_total").toString() + "\nPasses Key: "+ stats.get("passes_key").toString() + 
	    		"\nPasses Accuracy: "+passes_accuracy+ "\nTackles Total: "+stats.get("tackles_total").toString() +
	    		/*"\nTackles Blocks: "+stats.get("tackles_blocks").toString() +*/ "\nTackles Interceptions: "+stats.get("tackles_interceptions").toString()+
	    		"\nDuels Total: "+stats.get("duels_total").toString() +"\nDuels Won: "+stats.get("duels_won").toString() +
	    		"\nDribbles Attempts: "+stats.get("dribbles_attempts").toString()+"\nDribbles Success: "+stats.get("dribbles_success").toString()+
	    		/*"\nDribbles Past: "+stats.get("dribbles_past").toString()+*/"\nFouls Drawn: "+stats.get("fouls_drawn").toString()+
	    		"\nFouls Committed: "+stats.get("fouls_committed").toString()+"\nYellow Cards: "+stats.get("cards_yellow").toString()+
	    		"\nRed Cards: "+stats.get("cards_red").toString()+"\nYellow/Red Cards: "+stats.get("cards_yellowred").toString()+
	    		/*"\nPenalty Won: "+stats.get("penalty_won").toString()+"\nPenalty Commutted: "+stats.get("penalty_commutted").toString()+*/
	    		"\nPenalty Scored: "+stats.get("penalty_scored").toString()+/*"\nPenalty Missed: "+stats.get("penalty_missed").toString()+*/
	    		"\nPenalty Saved: "+stats.get("penalty_saved").toString();
		return output;
	}
	
	public static String get_matchday_info(Document card_doc, int num_matchday){
		
		String output = "";
		JSONObject card = new JSONObject(card_doc.toJson());
	    JSONObject matchday = card.getJSONObject("statistics").getJSONObject("matchday").getJSONObject("matchday" + num_matchday);
	    
	    String score = matchday.getJSONObject("score-value").get("score").toString();
	    if(score.equals("-5000.0")) {
	    	output = "Matchday not played \n";
	    	return output;
	    }
	    
	    output = "Matchday score: " + score + "\n";
	    
	    JSONObject stats = matchday.getJSONObject("stats");
	    Iterator<String> keys = stats.keys();
	    while(keys.hasNext()) {
	        String key = keys.next();
	        if(key.equals("On Tar")) {
	        	JSONObject ontar = stats.getJSONObject("On Tar");
	        	output = output + "On Tar Shots: " + ontar.get(" Shots").toString() + "\n";
	        }
	        
	        else if(key.equals("Aer")) {
	        	JSONObject aer = stats.getJSONObject("Aer");
	        	output = output + "AER Duels Won: " + aer.get(" Duels Won").toString() +
	        			"\nAER Duels Lost: " + aer.get(" Duels Lost").toString() +"\n";
	        }
	        else if(key.equals("shotsInfo")) {
	        	JSONArray shots_info = stats.getJSONArray("shotsInfo");
	        	if(shots_info != null) {
	        		int i=0;
	        		while(i<shots_info.length()) {
	        			JSONObject shot = shots_info.getJSONObject(i);
	        			i=i+1;
	        			output = output + "Shot " + i +":" + "\n\tMinute: "+ shot.get("min").toString() + "\n\tShot Type: " + shot.get("shotType").toString() +
	        					"\n\tPosition X: " + shot.get("X").toString() + "\n\tPosition Y: " + shot.get("Y").toString() +
	        					"\n\tResults: " + shot.get("results").toString() + "\n\tSituation: " + shot.get("situation").toString()+
	        					"\n\txG: " + shot.get("xG").toString() + "\n";
	        			JSONObject assist = shot.getJSONObject("assist");
	        			if(!assist.get("player").toString().equals("null")) {
	        				output = output + "\tAssist Player: " + assist.get("player").toString() + "\n\tAssist Action: " + assist.get("action").toString() + "\n";
	        			}
	 
	        		}
	        	}
	        		
	        }
	        else {
	        	output = output + key+ ": " + stats.get(key)+ "\n";
	        }
	    }
	    
	    return output;
	}
}
