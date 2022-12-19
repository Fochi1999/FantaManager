package it.unipi.dii.ingin.lsmsd.fantamanager.trades;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.bson.Document;
import com.google.gson.Gson;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.*;

import org.bson.conversions.Bson;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Scanner;

public class trade_main{

	public static void main(String[] args) {
		
		trade_main trade = new trade_main();
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
			trade.createTradeDocuments(ins_trade);		
		}
	}	
	
	
	public void createTradeDocuments(int total_trades){
		
		//connecting to mongoDB 
		String uri = "mongodb://localhost:27017";
		MongoClient myClient = MongoClients.create(uri);
		MongoDatabase database = myClient.getDatabase("FantaManager");
		MongoCollection<Document> collection = database.getCollection("Trades");
		System.out.print("Connected to mongoDB...\n");	
		

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
			
			ArrayList<Integer> player_from_input = new ArrayList<>();
			for(int j = 0; j < random_array_lenght1; j++) {
				int value = ThreadLocalRandom.current().nextInt(1, 501); //assuming 500 is the max player id
				player_from_input.add(value);
			}
			
			ArrayList<Integer> player_to_input = new ArrayList<>();
			for(int j = 0; j < random_array_lenght2; j++) {
				int value = ThreadLocalRandom.current().nextInt(1, 501); //assuming 500 is the max player id
				player_to_input.add(value);
			}
			
			//creating the class
			trade_class new_trade = new trade_class(i, user_from_input, user_to_input, 
					credits_input, player_from_input, player_to_input, status_input);
			
			//turning the new created trade class into a json file
			Gson gson = new Gson();
			String json = gson.toJson(new_trade);
			Document doc = Document.parse(json); //converting the json to document
				    
			//passing the json into mongoDB
			try {
				collection.insertOne(doc);
			}
			catch(Exception e) {
				System.out.print("MongoDB error, trade not inserted \n");
			}  
		}
		
		System.out.println("Trade collection created!");
	}
	
}
