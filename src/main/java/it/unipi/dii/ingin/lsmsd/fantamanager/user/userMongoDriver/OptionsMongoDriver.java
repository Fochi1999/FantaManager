package it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import java.util.regex.Pattern;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.hash;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.TradeMongoDriver;

public class OptionsMongoDriver {
	
	public static Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	
	
	
	public static boolean edit_attribute(String username, String attribute_name, String new_value) throws NoSuchAlgorithmException{
		
		//validating inputs
		if(attribute_name.equals("email")) {	//email
			if(!validate_email(new_value)) {
				System.out.println("Invalid input field!");
				return false;
			}
		}
		
		//connecting to mongoDB 
		UserMongoDriver.openConnection();
    	
    	//updating attribute
    	Bson user = Filters.eq("username", username);
    	if(attribute_name.equals("password")) {	//case of password attribute: hashing the password
    		new_value = hash.MD5(new_value);
    	}
    	
    	//changing value to integer for some attributes
    	Bson update;
    	if(attribute_name.equals("credits") || attribute_name.equals("collection") || attribute_name.equals("points")) {
			//int nv=Integer.parseInt(new_value);
    		update = Updates.set(attribute_name, Integer.parseInt(new_value));
    	}
    	else {
    		update = Updates.set(attribute_name, new_value);
    	}
    	
    	try {
    		UserMongoDriver.collection.updateOne(user, update);
    	}
    	catch(Exception e) {
    		System.out.println("Error on updating " + attribute_name + "for user: " + global.user.username);
			UserMongoDriver.closeConnection();
    		return false;
    	}
    	
    	if(attribute_name.equals("username")) { //if the username has been changed, this change has to be done on trades entities 
    		TradeMongoDriver.change_username_ontrades(username, new_value, "user_from");
    		TradeMongoDriver.change_username_ontrades(username, new_value, "user_to");
    		System.out.println("Username changed on all user's trades");
    	}

		UserMongoDriver.closeConnection();
    	return true;
	}
	
	
	public static boolean validate_email(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.find();
	}
	
	
	
	public static boolean find_duplicate(String attribute_name, String new_value){
		
		//connecting to mongoDB 

		UserMongoDriver.openConnection();

    	Document attribute_value;
    	
    	//updating attribute
    	Bson filter = Filters.eq(attribute_name, new_value);
    	
    	//finding the attribute
    	try {
    		attribute_value = UserMongoDriver.collection.find(filter).first();

			UserMongoDriver.closeConnection();
    		if(attribute_value == null) {	//attribute not in use
    			return false;
    		}
    	}
    	catch(Exception e) {
    		System.out.println("Error on finding " + attribute_name + "for user: " + global.user.username);
			UserMongoDriver.closeConnection();
    		return true;
    	}
    	System.out.println("Duplicate found!");
    	return true; //attribute already in use  	
	}

	public static void update_user_credits(Boolean add, String username, int new_credits) throws NoSuchAlgorithmException{

		int user_credits=0;

		if(add){
			if(global.user.getUsername().equals(username)) {
				user_credits = global.user.getCredits();
				global.user.setCredits(user_credits + new_credits);
			}
			else{
					user_credits=Integer.parseInt(UserMongoDriver.retrieve_user_attribute(username,"credits"));
			}

			edit_attribute(username, "credits", Integer.toString(user_credits+new_credits));
		}
		else {
			if(global.user.getUsername().equals(username)) {
				user_credits = global.user.getCredits();
				global.user.setCredits(user_credits - new_credits);
			}
			else{
				user_credits=Integer.parseInt(UserMongoDriver.retrieve_user_attribute(username,"credits"));
			}

			edit_attribute(username, "credits", Integer.toString(user_credits-new_credits));
		}
		//System.out.println("Credits updated for: " + username);
	}


	public static void update_user_points(String username, int old_tot, int new_points) throws NoSuchAlgorithmException {

		int user_points;
		if(global.user.getUsername().equals(username)) {
			user_points = global.user.getPoints();
			global.user.setCredits(user_points + new_points);
		}
		else{
			user_points=Integer.parseInt(UserMongoDriver.retrieve_user_attribute(username,"points"));
		}

		edit_attribute(username, "points", Integer.toString(user_points+new_points-old_tot));  //prima leggo punti che ci sono, se sono 0, qui aggiungo solo il totale nuovo fatto in quella partita, altrimenti
																											// se sto calcolando una partita già calcolata, il punteggio vecchio lo toglie e aggiunge quello nuovo, altrimenti si sommerebbero due volte
	}
}
