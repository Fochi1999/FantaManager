package it.unipi.dii.ingin.lsmsd.fantamanager.user;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;

public class SeeUserMongoDriver {

	public static Document search_user(String username) {
		
		//connecting to mongoDB 
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
    	
		//searching user
		Document user_doc;
    	try {
    		user_doc = collection.find(Filters.eq("username", username)).first();
    	}catch (Exception e) {
    		System.out.println("Error on searching user");
    		myClient.close();
    		return null;
    	}
    	
    	myClient.close();
    	return user_doc;
	}	
	
	
	public static void delete_user(String username) {
		
		//connecting to mongoDB 
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
			
		//delete user
		try {
			collection.deleteOne(Filters.eq("username", username));
		}
		catch(Exception e) {
			System.out.println("Error! Cannot delete this user now. Try later");
			myClient.close();
			return;
		}
		
		myClient.close();
		System.out.println("User '"+ username + "' successfully deleted.");
		return;
	}
	
}
