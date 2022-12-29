package it.unipi.dii.ingin.lsmsd.fantamanager.user;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;

public class profile_page {
	
	public static Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	
	
	
	public static boolean edit_attribute(String attribute_name, String new_value){
		
		//validating inputs
		if(attribute_name.equals("email")) {	//email
			if(!validate_email(new_value)) {
				System.out.println("Invalid input field!");
				return false;
			}
		}
		
		if(attribute_name.equals("username")) {	//username
			if(!validate_username(new_value)) {
				System.out.println("The username '"+ new_value+"' has already been taken!");
				return false;
			}
		}
		
		
		//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Users");
    	
    	//updating attribute
    	Bson user = Filters.eq("username", global.nick);
    	Bson update = Updates.set(attribute_name, new_value);
    	try {
    		collection.updateOne(user, update);
    	}
    	catch(Exception e) {
    		System.out.println("Error on updating " + attribute_name + "for user: " + global.nick);
    		return false;
    	}
    	
    	myClient.close();
    	return true;
	}
	
	
	public static boolean validate_email(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
	
	public static boolean validate_username(String new_username) {
		
		//connecting to mongoDB 
    	String uri = "mongodb://localhost:27017";
    	MongoClient myClient = MongoClients.create(uri);
    	MongoDatabase database = myClient.getDatabase("FantaManager");
    	MongoCollection<Document> collection = database.getCollection("Users");
    	
    	//searching username
    	Bson filter = Filters.eq("username", new_username);
    	Document user = collection.find(filter).first();
    	myClient.close();
    	
    	
    	if(user == null) {	//user not found
    		return true;
    	}
    	
    	return false;			//user found
    	
	}
	
	
}
