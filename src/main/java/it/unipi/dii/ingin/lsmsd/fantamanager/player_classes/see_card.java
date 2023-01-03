package it.unipi.dii.ingin.lsmsd.fantamanager.player_classes;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.types.ObjectId;

public class see_card {

	
	public static Document search_card(ObjectId card_id) {
		
		Document card_doc;
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.CARDS_COLLECTION_NAME);
		
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
		
		//retrieving infos
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
}
