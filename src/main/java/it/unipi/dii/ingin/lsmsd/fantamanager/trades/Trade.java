package it.unipi.dii.ingin.lsmsd.fantamanager.trades;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Filters;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Aggregates.limit;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.descending;

public class Trade{

	int trade_id;
	String user_from;
	String user_to;
	ArrayList<String> player_from;
	ArrayList<String> player_to;
	int credits;	// negative value: offered credits - positive value: wanted credits
	int status; 	// 0: pending - 1:completed
	

	
	//constructor
	public Trade(int trade_id_input, String user_from_input, String user_to_input, int credits_input,
			ArrayList<String> player_from_input, ArrayList<String> player_to_input, int status_input) {
		
		//error prevention condition
		if(status_input > 0)
			status_input = 1;
		
		//initializing the values
		this.trade_id = trade_id_input;
		this.user_from = user_from_input;
		this.credits = credits_input;
		this.status = status_input; 	//setting the trade's status to 'pending'
		this.user_to = user_to_input; 	
		this.player_from = player_from_input;
		this.player_to = player_to_input;
	}
	
	//update a trade's status from pending to complete
	public void complete_trade(String user_to_input) {
		this.user_to = user_to_input;
		this.status = 1; //setting the trade's status to 'complete'
	}


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


		System.out.println("Creating 'Trades' collection...");
		ArrayList<Document> TradeList = new ArrayList();

		//connecting to mongoDB
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);

		//retieving cards name
		ArrayList<String> card_names = new ArrayList<>();
		try {
			MongoCollection<Document> cards_collection = database.getCollection(global.CARDS_COLLECTION_NAME);
			MongoCursor<Document> cards_doc = cards_collection.find().iterator();
			while(cards_doc.hasNext()) {
				card_names.add(cards_doc.next().getString("fullname"));
			}
		}
		catch(Exception e){
			System.out.println("Error retrieving cards list.");
		}
		System.out.println("Cards names retrieved.");

		//retieving users name
		ArrayList<String> usernames = new ArrayList<>();
		try {
			MongoCollection<Document> users_collection = database.getCollection(global.USERS_COLLECTION_NAME);
			MongoCursor<Document> users_doc = users_collection.find().iterator();
			while(users_doc.hasNext()) {
				usernames.add(users_doc.next().getString("username"));
			}
		}
		catch(Exception e){
			System.out.println("Error retrieving usernames list.");
		}
		System.out.println("Usernames retrieved.");

		//creating trade classes
		for(int i=0; i < total_trades; i++) {

			//creating random trade's elements
			int random0 = ThreadLocalRandom.current().nextInt(0, usernames.size()-1);
			String user_from_input = usernames.get(random0);

			int credits_input = ThreadLocalRandom.current().nextInt(-500, 501);
			int status_input = ThreadLocalRandom.current().nextInt(0, 2);

			//if the trade is created as 'completed' then add the user that accepted it
			String user_to_input = "";
			if(status_input > 0) {
				int random1 = ThreadLocalRandom.current().nextInt(0, usernames.size()-1);
				user_to_input = usernames.get(random1);
				//Checking that the user 'owner' and 'accepted' are not the same
				while(user_from_input.equals(user_to_input)) {
					user_to_input = usernames.get(random1 +1);
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

			//creating the document
			Document doc = new Document();
			doc.append("user_from", user_from_input);
			doc.append("user_to", user_to_input);
			doc.append("player_from", player_from_input);
			doc.append("player_to", player_to_input);
			doc.append("credits", credits_input);
			doc.append("status", status_input);

			//adding to list
			TradeList.add(doc);
		}


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


	public static MongoCursor<Document> retrieve_most_present(String offered_wanted){

		MongoClient mongoClient=MongoClients.create(global.MONGO_URI);
		MongoDatabase database = mongoClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);

		if(offered_wanted.equals("offered")){
			//10 most frequent player offered in completed trades
			Bson match1=match(eq("status",1));
			Bson u=unwind("$player_from");
			Bson group=group("$player_from", Accumulators.sum("count",1));
			Bson order=sort(descending("count"));
			Bson limit=limit(20);

			try{
				return collection.aggregate(Arrays.asList(match1,u,group,order,limit)).iterator();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}

		}else{

			//10 most frequent player wanted in completed trades
			Bson match1=match(eq("status",1));
			Bson u=unwind("$player_to");
			Bson group=group("$player_to",Accumulators.sum("count",1));
			Bson order=sort(descending("count"));
			Bson limit=limit(20);

			try{
				return collection.aggregate(Arrays.asList(match1,u,group,order,limit)).iterator();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}


	}

	public static MongoCursor<Document> search_user(String my_user) {

		System.out.println("Searching trades made by: " + my_user);

		//connecting to mongoDB

		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);
		MongoCursor<Document> resultDoc;

		//preparing bson
		Bson user_equal = Filters.eq("user_from", my_user);

		//searching for the trades
		try {
			resultDoc = collection.find(user_equal).iterator();

		} catch (Exception e) {
			System.out.println("An error has occured while viewing trades!");
			return null;
		}

		//myClient.close();
		//print
		return resultDoc;
		//show_trades(resultDoc);

	}

	public static MongoCursor<Document> search_trade(String from_input, String to_input) {

		System.out.println("Searching trades -> offered: "+ from_input + " // wanted: " + to_input);

		//connecting to mongoDB
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);
		MongoCursor<Document> resultDoc;

		//preparing bsons
		Pattern pattern0 = Pattern.compile(from_input, Pattern.CASE_INSENSITIVE);
		Bson card_from_equal = Filters.regex("player_from", pattern0);

		Pattern pattern1 = Pattern.compile(to_input, Pattern.CASE_INSENSITIVE);
		Bson card_to_equal = Filters.regex("player_to", pattern1);


		//searching for the trades
		try {
			if (!from_input.isEmpty() && !to_input.isEmpty()) { 	//both inputs
				resultDoc = collection.find(Filters.and(card_from_equal,card_to_equal)).iterator();
			}
			else if(!from_input.isEmpty()) { 						//only user input
				resultDoc = collection.find(card_from_equal).iterator();
			}
			else if(!to_input.isEmpty()) { 						//only card input
				resultDoc = collection.find(card_to_equal).iterator();
			}
			else {													//no inputs
				System.out.println("No elements to search....");
				return null;
			}

		} catch (Exception e) {
			System.out.println("An error has occured while viewing trades!");
			return null;
		}

		//print
		//myClient.close();
		return resultDoc;

	}

	public static MongoCursor<Document> trades_pending(){
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.TRADES_COLLECTION_NAME);
		MongoCursor<Document> resultDoc;

		//searching for the trades
		Bson filter = Filters.eq("status",0);
		try {
			resultDoc = collection.find(filter).iterator();
		} catch (Exception e) {
			System.out.println("An error has occured while viewing trades!");
			return null;
		}

		//myClient.close();
		return resultDoc;
	}
}


