package it.unipi.dii.ingin.lsmsd.fantamanager.user;

//TODO decidere insieme se lasciare la funzione in un file a parte o implementarlo in un altro

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.hash;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;

import java.util.concurrent.ThreadLocalRandom;

import java.util.Scanner;
import java.io.File;  
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class populate_user {
	
	public static void createUsers() throws NoSuchAlgorithmException{
		
		System.out.println("Creating 'User' collection...");
		ArrayList<Document> UserList = new ArrayList();
		
		//adding the admin first
		String admin_username = "admin";
    	String admin_password = hash.MD5(admin_username);	//the password is the same as the username
		int admin_credits = 0;
		int admin_points = 0;
		int admin_collection = 0;	
		String admin_email = generate_random_email();
		String admin_region = generate_random_region();
		Document admin = new Document();
		admin.append("username", admin_username);
		admin.append("password", admin_password);
		admin.append("credits", admin_credits);
		admin.append("points", admin_points);
		admin.append("collection", admin_collection);
		admin.append("email", admin_email);
		admin.append("region", admin_region);
		admin.append("privilege", 2);
		UserList.add(admin);
		
		//insert user from a file of 500k randomly generated usernames
		try {
			File myObj = new File("C:\\Users\\emman\\Desktop\\username_list.txt"); //TODO renderlo accessibile a tutti
		    Scanner myReader = new Scanner(myObj);
		    while (myReader.hasNextLine()) {
		    
		    	//creating attributes values	
				String user_username = myReader.nextLine();
		    	String user_password = hash.MD5(user_username);	//the password is the same as the username
				int user_credits = ThreadLocalRandom.current().nextInt(0, 501);
				int user_points = ThreadLocalRandom.current().nextInt(0, 351);
				int user_collection = 0;	//TODO will be increased in another function
				String user_email = generate_random_email();
				String user_region = generate_random_region();
		    	
				//creating document
				Document new_user = new Document();
				new_user.append("username", user_username);
				new_user.append("password", user_password);
				new_user.append("credits", user_credits);
				new_user.append("points", user_points);
				new_user.append("collection", user_collection);
				new_user.append("email", user_email);
				new_user.append("region", user_region);
				new_user.append("privilege", 1);
				
				//add
				UserList.add(new_user);
		    	//System.out.println(user_username);
		    }
		    
		    //connecting to mongoDB 
			MongoClient myClient = MongoClients.create(global.MONGO_URI);
			MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
			MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
							
			collection.insertMany(UserList);
			
			myClient.close();
		    myReader.close();
		}
		catch (FileNotFoundException e) {
		    System.out.println("An error occurred.");
		   	e.printStackTrace();
		}
		
		System.out.println("User collection created!");
	}
	
	
	private static String generate_random_email() throws NoSuchAlgorithmException{
		String emailAddress = "";
		String alphabet = "abcdefghijklmnopqrstuvwxyz";
		while (emailAddress.length() < 8) {
		    int character = (int) (Math.random() * 26);
		    emailAddress += alphabet.substring(character, character + 1);
		emailAddress += Integer.valueOf((int) (Math.random() * 99)).toString();
		emailAddress = hash.MD5(emailAddress);
		emailAddress += "@unipi.it";
		}
		return emailAddress;
	}
	
	private static String generate_random_region(){
		String regionList[] = {"Abruzzo","Basilicata","Calabria","Campania","Emilia-Romagna","Friuli-Venezia Giulia","Lazio","Liguria",
                "Lombardy","Marche","Molise","Piedmont","Apulia","Sardinia","Sicily","Trentino-South Tyrol","Tuscany",
                "Umbria","Aosta Valley","Veneto"};	
		int random = ThreadLocalRandom.current().nextInt(0, regionList.length);
		return regionList[random];
	}
}
