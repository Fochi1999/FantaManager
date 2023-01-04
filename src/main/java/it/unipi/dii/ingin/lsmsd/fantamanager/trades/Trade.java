package it.unipi.dii.ingin.lsmsd.fantamanager.trades;
import com.mongodb.client.*;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.descending;

public class Trade{

	String trade_id;
	String user_from;
	String user_to;
	ArrayList<String> player_from;
	ArrayList<String> player_to;
	int credits;	// negative value: offered credits - positive value: wanted credits
	int status; 	// 0: pending - 1:completed
	

	
	//constructor
	public Trade(String trade_id_input, String user_from_input, String user_to_input, int credits_input,
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



	public String get_id() {
		return this.trade_id;
	}

	public String get_user_from(){
		return this.user_from;
	}

	public String get_user_to(){
		return this.user_to;
	}

	public int get_credits(){
		return this.credits;
	}

	public int get_user_status(){
		return this.status;
	}

	public ArrayList<String> get_player_from(){return this.player_from;}

	public ArrayList<String> get_player_to(){return this.player_to;}


	
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



}


