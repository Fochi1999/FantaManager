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
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.hash;

public class profile_page {
	
	public static Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	
	
	
	public static boolean edit_attribute(String attribute_name, String new_value) throws NoSuchAlgorithmException{
		
		//validating inputs
		if(attribute_name.equals("email")) {	//email
			if(!validate_email(new_value)) {
				System.out.println("Invalid input field!");
				return false;
			}
		}
		
		//connecting to mongoDB 
    	MongoClient myClient = MongoClients.create(global.MONGO_URI);
    	MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
    	MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
    	
    	//updating attribute
    	Bson user = Filters.eq("username", global.user.username);
    	if(attribute_name.equals("password")) {	//case of password attribute: hashing the password
    		new_value = hash.MD5(new_value);
    	}
    	Bson update = Updates.set(attribute_name, new_value);
    	
    	
    	try {
    		collection.updateOne(user, update);
    	}
    	catch(Exception e) {
    		System.out.println("Error on updating " + attribute_name + "for user: " + global.user.username);
    		return false;
    	}
    	
    	myClient.close();
    	return true;
	}
	
	
	public static boolean validate_email(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
	
	
	
	public static boolean find_duplicate(String attribute_name, String new_value){
		
		//connecting to mongoDB 
    	MongoClient myClient = MongoClients.create(global.MONGO_URI);
    	MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
    	MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
    	Document attribute_value;
    	
    	//updating attribute
    	Bson filter = Filters.eq(attribute_name, new_value);
    	
    	//finding the attribute
    	try {
    		attribute_value = collection.find(filter).first();
    		myClient.close();
    		if(attribute_value == null) {	//attribute not in use
    			return false;
    		}
    	}
    	catch(Exception e) {
    		System.out.println("Error on finding " + attribute_name + "for user: " + global.user.username);
    		return true;
    	}
    	System.out.println("Duplicate found!");
    	return true; //attribute already in use  	
	}
	
}
