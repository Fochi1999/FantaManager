package it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver;

import static com.mongodb.client.model.Accumulators.first;
import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.match;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.computed;
import static com.mongodb.client.model.Projections.excludeId;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Sorts.descending;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Aggregates.*;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;

public class RankingMongoDriver {

	public static ArrayList<Document> retrieve_user(Boolean search, String username) {
		
		ArrayList<Document> resultDoc = new ArrayList<>();

		UserMongoDriver.openConnection();
		//connecting to mongoDB 
		//MongoClient myClient = MongoClients.create(global.MONGO_URI);
		//MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		//MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
		MongoCursor<Document> cursor;
		   
		//searching users:
		if(!search) {		//global rank
			try {
				cursor = UserMongoDriver.collection.find().sort(descending("points")).limit(100).iterator();
				System.out.println("Viewing rank");
			}
			catch(Exception e) {
				System.out.println("Error on searching for users.");
				//myClient.close();
				UserMongoDriver.closeConnection();
				return null;
			}  
			//myClient.close();
			UserMongoDriver.closeConnection();
			
			//saving documents into an array
			while(cursor.hasNext()) {
				resultDoc.add(cursor.next());
			}
			return resultDoc;
		}
		else {			//search user by name
			//filters
			Pattern pattern = Pattern.compile(username, Pattern.CASE_INSENSITIVE);
			Bson filter = Filters.regex("username", pattern);	
		        	
			try {
				cursor = UserMongoDriver.collection.find(filter).sort(descending("points")).limit(100).iterator();
				System.out.println("Searching for user: " + username);
		    }
			catch(Exception e) {
				System.out.println("Error on searching for users.");
				//myClient.close();
				UserMongoDriver.closeConnection();
		    	return null;
		    }  	
			
			//saving documents into an array
			while(cursor.hasNext()) {
				resultDoc.add(cursor.next());
			}
			return resultDoc;
		}
	}
	
	public static ArrayList<Document> best_for_region() {
		
		ArrayList<Document> resultDoc = new ArrayList<>();
		System.out.println("Viewing best users for each region.");
		
		//connecting to mongoDB
		//MongoClient myClient = MongoClients.create(global.MONGO_URI);
		//MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		//MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
		UserMongoDriver.openConnection();
		
		//filters
		Bson group=group("$region", first("username","$username"), first("credits","$credits"), first("points","$points"), first("id","$_id"));
		Bson p1=project(fields(excludeId(),include("username"),include("points"),computed("_id","$id"),computed("region","$_id")));
		Bson order=sort(descending("points"));
		
		//searching
		try{
			MongoCursor<Document> cursor=UserMongoDriver.collection.aggregate(Arrays.asList(order,group,p1,order)).iterator();
			while(cursor.hasNext()){
				resultDoc.add(cursor.next());
			}
		}	
		catch(Exception e){
			System.out.println("Error on searching for users.");
			//myClient.close();
			UserMongoDriver.closeConnection();
	    	return null;
		}
		
		//myClient.close();
		UserMongoDriver.closeConnection();
		return resultDoc;
	}
	
	public static ArrayList<Document> search_users_by_region(String region) {
		
		ArrayList<Document> resultDoc = new ArrayList<>();
		System.out.println("Searching for region: " + region);
		
		//connecting to mongoDB
		//MongoClient myClient = MongoClients.create(global.MONGO_URI);
		//MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		//MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
		UserMongoDriver.openConnection();
		
		//filters
		Bson match1 = match(eq("region",region));
		Bson order = sort(descending("points"));
		Bson limit = limit(100);
		
		//searching
		try{
			MongoCursor<Document> cursor=UserMongoDriver.collection.aggregate(Arrays.asList(match1,order,limit)).iterator();
			while(cursor.hasNext()){
				resultDoc.add(cursor.next());
			}
		}	
		catch(Exception e){
			System.out.println("Error on searching for users.");
			//myClient.close();
			UserMongoDriver.closeConnection();
	    	return null;
		}
		
		//myClient.close();
		UserMongoDriver.closeConnection();
		return resultDoc;
	}
	
	
}
