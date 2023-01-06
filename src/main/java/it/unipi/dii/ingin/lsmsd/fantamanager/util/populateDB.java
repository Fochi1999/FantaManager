package it.unipi.dii.ingin.lsmsd.fantamanager.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.user;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;


public class populateDB {

	 //prossime due funzioni solo per creazione collection casuale per utente user_id:1
    static ArrayList<Document> get_cards_collection_mongoDB(){

    	//connecting to mongoDB 
    	MongoClient myClient = MongoClients.create(global.MONGO_URI);
    	MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
    	MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);
    			
    	//retieving cards name
    	ArrayList<Document> card_list = new ArrayList<>();
    	try {
    		MongoCollection<Document> cards_collection = database.getCollection(global.CARDS_COLLECTION_NAME);
    		MongoCursor<Document> cards_doc = cards_collection.find().iterator();
    		while(cards_doc.hasNext()) {
    			card_list.add(cards_doc.next());
    		}
    	}
    	catch(Exception e){
    		System.out.println("Error retrieving cards list.");
    		myClient.close();
    		return null;
    	}
    	
    	myClient.close();
    	return card_list;
    }

    
    static ArrayList<Document> get_users_collection_mongoDB(){

    	//connecting to mongoDB 
    	MongoClient myClient = MongoClients.create(global.MONGO_URI);
    	MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
    	MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
    			
    	//retieving cards name
    	ArrayList<Document> user_list = new ArrayList<>();
    	try {
    		MongoCursor<Document> user_doc = collection.find().iterator();
    		while(user_doc.hasNext()) {
    			user_list.add(user_doc.next());
    		}
    	}
    	catch(Exception e){
    		System.out.println("Error retrieving users list.");
    		myClient.close();
    		return null;
    	}
    	
    	myClient.close();
    	return user_list;
    }
    
    
    public static void create_user_card_collection_redis(){
    	
    	System.out.println("Creating user's collection on redis...");
    	
    	//retrieve documents from mongo
    	ArrayList<Document> user_list = get_users_collection_mongoDB();
    	ArrayList<Document> cards_list = get_cards_collection_mongoDB();
    	
    	//connecting to mongoDB 
    	MongoClient myClient = MongoClients.create(global.MONGO_URI);
    	MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
    	MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
    	
        //connecting to redis
        JedisPool pool=new JedisPool("localhost",6379);
        //insert
        try(Jedis jedis=pool.getResource()){
        	
        	for(int i=0;i<user_list.size()-1;i++){
        		Pipeline pipe = jedis.pipelined();
        		int total_cards=0;
        		if(user_list.get(i).getString("username").equals("admin")) {	//admin doesn't play
        			continue;
        		}
        		String user_id = user_list.get(i).get("_id").toString();
        		int random_total_cards = ThreadLocalRandom.current().nextInt(0, cards_list.size());	//user's cards collection size
        		for(int j=0; j<random_total_cards; j++) {
                	
        			int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size());	//card id
        			int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);
        			total_cards+=rndm_quantity;
        			String random_quantity = Integer.toString(rndm_quantity);	//card quantity
        			String card_position = cards_list.get(random_card).getString("position");		//card position
        			String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
        			String card_team = cards_list.get(random_card).getString("team");			//card team
        			pipe.set("user_id:"+user_id+":card_id:"+random_card+":name",card_name);
        			pipe.set("user_id:"+user_id+":card_id:"+random_card+":quantity", random_quantity);
        			pipe.set("user_id:"+user_id+":card_id:"+random_card+":team",card_team);
        			pipe.set("user_id:"+user_id+":card_id:"+random_card+":position",card_position);
        		}
        		System.out.println(pipe.syncAndReturnAll());
        		
        		//updating user collection attribute on mongo
        		collection.updateOne(Filters.eq("_id",user_id), Updates.set("collection", total_cards));
        	}
        }
        pool.close();
        myClient.close();
        System.out.println("Users' card collection created on redis");
    }
	
    public static void create_trade_collection_mongoDB(int total_trades){
		
		System.out.println("Creating 'Trades' collection...");
		ArrayList<Document> TradeList = new ArrayList();
		
		ArrayList<Document> card_list = get_cards_collection_mongoDB();
		ArrayList<Document> user_list = get_users_collection_mongoDB();
		
		//creating trade classes
		for(int i=0; i < total_trades; i++) {
			
			//creating random trade's elements
			int random0 = ThreadLocalRandom.current().nextInt(0, user_list.size()-1);
			String user_from_input = user_list.get(random0).getString("username");
			if(user_list.get(random0).getString("username").equals("admin")) {	//admin doesn't play
				user_from_input = user_list.get(random0+1).getString("username");
        	}
			int credits_input = ThreadLocalRandom.current().nextInt(-500, 501);
			int status_input = ThreadLocalRandom.current().nextInt(0, 2);
			
			//if the trade is created as 'completed' then add the user that accepted it
			String user_to_input = "";
			if(status_input > 0) {
				int random1 = ThreadLocalRandom.current().nextInt(0, user_list.size()-1);
				user_to_input = user_list.get(random1).getString("username");
				//Checking that the user 'owner' and 'accepted' are not the same
				while(user_from_input.equals(user_to_input)) {
					user_to_input = user_list.get(random1 +1).getString("username");
				}
			}
			
			//creating the players array to trade
			int random_array_lenght1 =  ThreadLocalRandom.current().nextInt(1, 3 + 1);
			int random_array_lenght2 =  ThreadLocalRandom.current().nextInt(0, 3 + 1); //even 0 cards could be received
			
			ArrayList<String> card_from_input = new ArrayList<>();
			for(int j = 0; j < random_array_lenght1; j++) {
				int random = ThreadLocalRandom.current().nextInt(0, card_list.size()-1);
				card_from_input.add(card_list.get(random).getString("fullname"));
			}
			
			ArrayList<String> card_to_input = new ArrayList<>();
			for(int j = 0; j < random_array_lenght2; j++) {
				int random = ThreadLocalRandom.current().nextInt(0, card_list.size()-1);
				card_to_input.add(card_list.get(random).getString("fullname"));
			}
			
			//creating the document
			Document doc = new Document();
			doc.append("user_from", user_from_input);
			doc.append("user_to", user_to_input);
			doc.append("card_from", card_from_input);
			doc.append("card_to", card_to_input);
			doc.append("credits", credits_input);
			doc.append("status", status_input);
			
			//adding to list
			TradeList.add(doc);			
		}
		
		//connecting to mongoDB 
    	MongoClient myClient = MongoClients.create(global.MONGO_URI);
    	MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
    	MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);
    	
		//passing the json into mongoDB
		try {
			collection.insertMany(TradeList);
		}
		catch(Exception e) {
			System.out.println("Error while creating Trades collection!");
		}  
		
		System.out.println("Trades collection created!");
		myClient.close();
	}
    
    
    public static void create_users_collection_mongoDB(int total_users) throws NoSuchAlgorithmException{
		
		System.out.println("Creating 'User' collection...");
		ArrayList<user> user_list = new ArrayList();
		
		//adding the admin first
		String admin_username = "admin";
    	String admin_password = hash.MD5(admin_username);	//the password is the same as the username
		int admin_credits = 0;
		int admin_points = 0;
		int admin_collection = 0;	
		String admin_email = generate_random_email();
		int random0 = ThreadLocalRandom.current().nextInt(0,utilities.regionList.length);
		String admin_region = utilities.regionList[random0];

		user admin=new user(admin_username,admin_password,admin_region,admin_email,admin_credits,admin_collection,2,admin_points);
		user_list.add(admin);
		//insert user from a file of 500k randomly generated usernames
		try {
			File myObj = new File("src/main/resources/temp/user.txt");
		    Scanner myReader = new Scanner(myObj);
		    int num=0;
		    while(num < total_users) {
		    	
		    	//creating attributes values	
				String user_username = myReader.nextLine();
		    	String user_password = hash.MD5(user_username);	//the password is the same as the username
				int user_credits = ThreadLocalRandom.current().nextInt(0, 501);
				int user_points = ThreadLocalRandom.current().nextInt(0, 351);
				int user_collection = 0;	//TODO will be increased in another function
				String user_email = generate_random_email();
				int random1 = ThreadLocalRandom.current().nextInt(0,utilities.regionList.length);
				String user_region = utilities.regionList[random1];

				//creating document
				user new_user=new user(user_username,user_password,user_region,user_email,user_credits,user_collection,1,user_points);
				//add
				user_list.add(new_user);
		    	//System.out.println(user_username);
				num=num+1;
		    }
		    
		    //connecting to mongoDB 
			MongoClient myClient = MongoClients.create(global.MONGO_URI);
			MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
			MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
			ArrayList<Document> user_list_doc=new ArrayList<>();
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			String json = null;
			for(int i=0;i<user_list.size();i++) {
				try {
					json = ow.writeValueAsString(user_list.get(i));
					user_list_doc.add(Document.parse( json ));
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
			}

			collection.insertMany(user_list_doc);
			
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
	
}
