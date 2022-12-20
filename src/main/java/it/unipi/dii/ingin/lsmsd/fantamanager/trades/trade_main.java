package it.unipi.dii.ingin.lsmsd.fantamanager.trades;


import org.bson.Document;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;
import java.util.Scanner;

public class trade_main{

	public static void createTrade() {
		
		System.out.println("How many trades do you want to insert?");
		int ins_trade = 0;
		Scanner scanner=new Scanner(System.in);
		try{
			ins_trade = scanner.nextInt();
		}
		catch(Exception e) {
			System.out.println("Insert a valid integer value!");
		}
		scanner.close();
		
		if(ins_trade <= 0) {
			System.out.println("The inserted value must be greater than 0!");
		}
		else {
			createTradeDocuments(ins_trade);		
		}
	}	
	

	public static void createTradeDocuments(int total_trades){
		
		//connecting to mongoDB 
		String uri = "mongodb://localhost:27017";
		MongoClient myClient = MongoClients.create(uri);
		MongoDatabase database = myClient.getDatabase("FantaManager");
		MongoCollection<Document> collection = database.getCollection("Trades");
		System.out.print("Connected to MongoDB...\n");	
		
		//retieving cards name
		ArrayList<String> card_names = new ArrayList<>();
		try {
			MongoCollection<Document> cards_collection = database.getCollection("Player_Java_Final");
			MongoCursor<Document> cards_doc = cards_collection.find().iterator();
			while(cards_doc.hasNext()) {
				card_names.add(cards_doc.next().getString("fullname"));
			}
		}
		catch(Exception e){
			System.out.println("Error retrieving cards list.");
		}
		System.out.println("Cards names retrieved.");

		//creating trade classes
		for(int i=0; i < total_trades; i++) {
			
			//creating random trade's elements
			String user_from_input = "user" + ThreadLocalRandom.current().nextInt(0, 501); //assuming 500 is the max user'sid
			int credits_input = ThreadLocalRandom.current().nextInt(-500, 501);
			int status_input = ThreadLocalRandom.current().nextInt(0, 2);
			String user_to_input = "";
			
			//if the trade is complete add the user that accepted it
			if(status_input > 0) {
				user_to_input = "user" + ThreadLocalRandom.current().nextInt(1, 501);
				//Checking that the user from and to are not the same
				while(user_from_input.equals(user_to_input)) {
					user_to_input = "user" + ThreadLocalRandom.current().nextInt(1, 501);
				}
			}
			
			//creating the players array to trade
			int random_array_lenght1 =  ThreadLocalRandom.current().nextInt(1, 3 + 1);
			int random_array_lenght2 =  ThreadLocalRandom.current().nextInt(0, 3 + 1); //even 0 cards could be received
			
			ArrayList<String> player_from_input = new ArrayList<>();
			for(int j = 0; j < random_array_lenght1; j++) {
				int random = ThreadLocalRandom.current().nextInt(0, card_names.size()-1);
				player_from_input.add(card_names.get(random));
			}
			
			ArrayList<String> player_to_input = new ArrayList<>();
			for(int j = 0; j < random_array_lenght2; j++) {
				int random = ThreadLocalRandom.current().nextInt(0, card_names.size()-1);
				player_to_input.add(card_names.get(random));
			}
			
			Document doc = new Document();
			doc.append("trade_id", i);
			doc.append("user_from", user_from_input);
			doc.append("user_to", user_to_input);
			doc.append("player_from", player_from_input);
			doc.append("player_to", player_to_input);
			doc.append("credits", credits_input);
			doc.append("status", status_input);
			
			//passing the json into mongoDB
			try {
				collection.insertOne(doc);
			}
			catch(Exception e) {
				System.out.print("MongoDB error, trades not found. \n");
			}  
		}
		
		System.out.println("Trade collection created!");
		myClient.close();
	}
}	
