package it.unipi.dii.ingin.lsmsd.fantamanager.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.user;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.formationMongoDriver;
import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;


public class populateDB {

	static int numero_player=0;

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
    			Document user = user_doc.next();
    			Document new_user = new Document();
    			new_user.append("username", user.getString("username"));
    			new_user.append("_id", user.get("_id"));
    			
    			user_list.add(new_user);
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
    	ArrayList<Document> cards_list = get_cards_collection_mongoDB();
    	System.out.println("Cards retrieved..");
    	ArrayList<Document> user_list = get_users_collection_mongoDB();
    	System.out.println("Users retrieved..");
    	
    	//connecting to redis
    	JedisPool pool = new JedisPool(global.REDIS_URI, global.REDIS_PORT);
    	
        //insert
        try(Jedis jedis=pool.getResource()){
        	
        	for(int i=0;i<user_list.size();i++){
        	
        		Transaction transaction = jedis.multi();
        		/*
        		if(user_list.get(i).getString("username").equals("admin")) {	//admin doesn't play
        			i++;
        		}*/
        		
        		String user_id = user_list.get(i).get("_id").toString();
        		int random_total_cards = ThreadLocalRandom.current().nextInt(30, 40);	//user's cards collection size
        		
        		for(int j=0; j<5; j++) {	//at least 5 attacker
        			
        			int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
        			String card_position = cards_list.get(random_card).getString("position");
        			if(!card_position.equals("Attacker")){
        				j--;
        				continue;
        			}		
        			
        			int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);	
        			String random_quantity = Integer.toString(rndm_quantity);	//card quantity
        			
        			String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
        			String card_team = cards_list.get(random_card).getString("team");			//card team
        			
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":name",card_name);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":quantity", random_quantity);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":team",card_team);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":position",card_position);
        		}
        		random_total_cards -= 5;
        		
        		for(int j=0; j<4; j++) {	//at least 4 goalkeepers
        			
        			int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
        			String card_position = cards_list.get(random_card).getString("position");
        			if(!card_position.equals("Goalkeeper")){
        				j--;
        				continue;
        			}		
        			
        			int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);	
        			String random_quantity = Integer.toString(rndm_quantity);	//card quantity
        			
        			String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
        			String card_team = cards_list.get(random_card).getString("team");			//card team
        			
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":name",card_name);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":quantity", random_quantity);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":team",card_team);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":position",card_position);
        		}
        		random_total_cards -= 4;
        		
        		for(int j=0; j<7; j++) {	//at least 7 midfielders
        			
        			int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
        			String card_position = cards_list.get(random_card).getString("position");
        			if(!card_position.equals("Midfielder")){
        				j--;
        				continue;
        			}		
        			
        			int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);	
        			String random_quantity = Integer.toString(rndm_quantity);	//card quantity
        			
        			String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
        			String card_team = cards_list.get(random_card).getString("team");			//card team
        			
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":name",card_name);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":quantity", random_quantity);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":team",card_team);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":position",card_position);
        		}
        		random_total_cards -= 7;
        		
        		for(int j=0; j<7; j++) {	//at least 7 defenders
        			
        			int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
        			String card_position = cards_list.get(random_card).getString("position");
        			if(!card_position.equals("Defender")){
        				j--;
        				continue;
        			}		
        			
        			int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);	
        			String random_quantity = Integer.toString(rndm_quantity);	//card quantity
        			
        			String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
        			String card_team = cards_list.get(random_card).getString("team");			//card team
        			
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":name",card_name);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":quantity", random_quantity);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":team",card_team);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":position",card_position);
        		}
        		random_total_cards -= 7;
        		
        		for(int j=0; j<random_total_cards; j++) {
                	
        			int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
        			int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);	
        			String random_quantity = Integer.toString(rndm_quantity);	//card quantity
        			String card_position = cards_list.get(random_card).getString("position");		//card position
        			String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
        			String card_team = cards_list.get(random_card).getString("team");			//card team
        			
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":name",card_name);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":quantity", random_quantity);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":team",card_team);
        			transaction.set("user_id:"+user_id+":card_id:"+random_card+":position",card_position);
        		}
        		System.out.println(i+"/"+user_list.size()+ " " + transaction.exec());
        	}	
        }
        pool.close();
        System.out.println("Users' card collection created on redis");
    }
	
    public static void create_trade_collection_mongoDB(int total_trades){
		
		System.out.println("Creating 'Trades' collection...");
		ArrayList<Document> TradeList = new ArrayList();
		
		ArrayList<Document> card_list = get_cards_collection_mongoDB();
    	System.out.println("Cards retrieved..");
		ArrayList<Document> user_list = get_users_collection_mongoDB();
		System.out.println("Users retrieved..");
		
		//creating trade classes
		for(int i=0; i < total_trades; i++) {
			
			//creating random trade's elements
			int random0 = ThreadLocalRandom.current().nextInt(0, user_list.size()-1);
			String user_from_input = user_list.get(random0).getString("username");
			if(user_list.get(random0).getString("username").equals("admin")) {	//admin doesn't play
				user_from_input = user_list.get(random0+1).getString("username");
        	}
			int credits_input = ThreadLocalRandom.current().nextInt(-20, 20);
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
			
			ArrayList<JSONObject> card_from_input = new ArrayList<>();
			for(int j = 0; j < random_array_lenght1; j++) {
				JSONObject new_card = new JSONObject();
				int random = ThreadLocalRandom.current().nextInt(0, card_list.size()-1);
				new_card.put("card_name", card_list.get(random).get("fullname"));
				new_card.put("card_id", card_list.get(random).get("player_id"));
				new_card.put("card_team", card_list.get(random).get("team"));
				new_card.put("card_position", card_list.get(random).get("position"));
				card_from_input.add(new_card);
			}
			
			ArrayList<JSONObject> card_to_input = new ArrayList<>();
			for(int j = 0; j < random_array_lenght2; j++) {
				JSONObject new_card = new JSONObject();
				int random = ThreadLocalRandom.current().nextInt(0, card_list.size()-1);
				new_card.put("card_name", card_list.get(random).get("fullname"));
				new_card.put("card_id", card_list.get(random).get("player_id"));
				new_card.put("card_team", card_list.get(random).get("team"));
				new_card.put("card_position", card_list.get(random).get("position"));
				card_to_input.add(new_card);
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
			System.out.println("Trades created: "+ (i+1) + "/" + total_trades);
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

	public static void create_users_collection_mongoDB_edo(int total_users){
		System.out.println("Creating 'User' collection...");
		ArrayList<user> user_list = new ArrayList();
		ArrayList<Document> user_list_doc=new ArrayList<>();
		try {
			File myObj = new File("src/main/resources/temp/user2.txt");
			Scanner myReader = new Scanner(myObj);
			int tot=0;

			while(tot < total_users) {

				//creating attributes values
				String user_username = myReader.nextLine();
				String user_password = hash.MD5(user_username);    //the password is the same as the username
				int user_credits = ThreadLocalRandom.current().nextInt(50, 351);
				int user_points = 0;
				String user_email = generate_random_email(user_username);
				int random1 = ThreadLocalRandom.current().nextInt(0, utilities.regionList.length);
				String user_region = utilities.regionList[random1];
				int user_privilege=1;
				user u=new user(user_username,user_password,user_region,user_email,user_credits,user_privilege,user_points);
				//creating document
				Document new_user_doc = new Document();
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				String json = null;
				try {
					json = ow.writeValueAsString(u);
				} catch (JsonProcessingException e) {
					throw new RuntimeException(e);
				}
				JSONParser parser = new JSONParser();
				JSONObject jsonUser = (JSONObject) parser.parse(json);
				//System.out.println(jsonUser.toString());
				//add
				user_list_doc.add(Document.parse(jsonUser.toString()));
				System.out.println("Users created: "+(tot+1) + "/" + total_users);
				tot=tot+1;
			}
			myReader.close();

			//connecting to mongoDB
			MongoClient myClient = MongoClients.create(global.MONGO_URI);
			MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
			MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
			collection.insertMany(user_list_doc);
			myClient.close();

		}
		catch (FileNotFoundException | NoSuchAlgorithmException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		System.out.println("User collection created!");
	}

    public static void create_users_collection_mongoDB(int total_users) throws NoSuchAlgorithmException{
		
		System.out.println("Creating 'User' collection...");
		ArrayList<user> user_list = new ArrayList();
		
		//creating formation
		
		//player
		JSONObject player = new JSONObject();
		player.put("name", "");
		player.put("id", 0);
		player.put("team", "");
		player.put("vote", Double.valueOf(0));
		//ArrayList<JSONObject> players = new ArrayList<>();
		JSONObject players =new JSONObject();
		for(int i=0; i<=18;i++) {
			players.put(String.valueOf(i),player);
		}
		//module
		ArrayList<Integer> module = new ArrayList<>();
		module.add(0, 0);
		module.add(1, 0);
		module.add(2, 0);
		module.add(3, 0);


		//formation
		//ArrayList<JSONObject> formations = new ArrayList<>();
		JSONObject formations=new JSONObject();
		JSONObject formation = new JSONObject();
		formation.put("tot",0);
		formation.put("valid",false);
		formation.put("module", module);
		formation.put("players", players);
		for(int i=1; i<39; i++) {
			formations.put(String.valueOf(i), formation);
		}
		
		
		//adding the admin first
		String admin_username = "admin";
    	String admin_password = hash.MD5(admin_username);	//the password is the same as the username
		int admin_credits = ThreadLocalRandom.current().nextInt(50, 351);;
		int admin_points = 0;
		int admin_privilege=2;
		String admin_email = generate_random_email("admin");
		int random0 = ThreadLocalRandom.current().nextInt(0,utilities.regionList.length);

		String admin_region = utilities.regionList[random0];

		//user admin=new user(admin_username,admin_password,admin_region,admin_email,admin_credits,2,admin_points);
		ArrayList<Document> user_list_doc=new ArrayList<>();
		//adding admin
		Document admin = new Document();
		admin.append("username", admin_username);
		admin.append("password", admin_password);
		admin.append("credits", admin_credits);
		admin.append("points", admin_points);
		admin.append("email", admin_email);
		admin.append("region", admin_region);
		admin.append("_privilege",admin_privilege);
		admin.append("formations", formations);
		user_list_doc.add(admin);
		
		//insert user from a file of 500k randomly generated usernames
		try {
			File myObj = new File("src/main/resources/temp/user2.txt");
		    Scanner myReader = new Scanner(myObj);
		    int tot=0;

			while(tot < total_users) {
				
				//creating attributes values
				String user_username = myReader.nextLine();
				String user_password = hash.MD5(user_username);    //the password is the same as the username
				int user_credits = ThreadLocalRandom.current().nextInt(50, 351);
				int user_points = 0;
				String user_email = generate_random_email(user_username);
				int random1 = ThreadLocalRandom.current().nextInt(0, utilities.regionList.length);
				String user_region = utilities.regionList[random1];
				int user_privilege=1;

				//creating document
				Document new_user_doc = new Document();
				new_user_doc.append("username", user_username);
				new_user_doc.append("password", user_password);
				new_user_doc.append("credits", user_credits);
				new_user_doc.append("points", user_points);
				new_user_doc.append("email", user_email);
				new_user_doc.append("region", user_region);
				new_user_doc.append("_privilege",user_privilege);
				new_user_doc.append("formations", formations);
					
				//add	
				user_list_doc.add(new_user_doc);
				System.out.println("Users created: "+(tot+1) + "/" + total_users);
				tot=tot+1;
			}
			myReader.close();
			
			//connecting to mongoDB
			MongoClient myClient = MongoClients.create(global.MONGO_URI);
			MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
			MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
			collection.insertMany(user_list_doc);
			myClient.close();

		}
		catch (FileNotFoundException e) {
		    System.out.println("An error occurred.");
		   	e.printStackTrace();
		}
		
		System.out.println("User collection created!");
	}
	
	
	private static String generate_random_email(String s) throws NoSuchAlgorithmException{
		String emailAddress = s;
		emailAddress += "@unipi.it";
		return emailAddress;
	}
/*
	public static void create_cards_collection_mongoDB(){

		//connecting to mongoDB
		MongoClient myClient = MongoClients.create(global.MONGO_URI);
		MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		MongoCollection<Document> collection = database.getCollection(global.CARDS_COLLECTION_NAME);
		System.out.print("Connected to mongoDB...\n");

		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader("json_stats/matchday_stats/kickest_stats.json")) {    //C:\Users\matte\DataMiningJupyter\fantacalcio_LSKickest_completo_portiere.json
			//Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray playerList = (JSONArray) obj;
			//System.out.println(playerList);

			//Iterate over player array
			playerList.forEach(player -> findPlayerAPI((JSONObject) player,collection));

		} //catch (FileNotFoundException e) {
		//e.printStackTrace();
		//}
		catch (IOException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}

		myClient.close();
		System.out.print("'Player' DB created...\n");

	}
	*/
/*
	private static void findPlayerAPI(JSONObject player_kickest, MongoCollection<Document> collection) {
		JSONParser jsonParser = new JSONParser();

		try (FileReader reader = new FileReader("json_stats/APIFootball.json")) {   //C:\Users\matte\OneDrive\Desktop\LargeScale\jsonEdo\json_finali\APIFootball.json
			//Read JSON file
			Object obj = jsonParser.parse(reader);

			JSONArray playerList = (JSONArray) obj;
			//System.out.println(playerList);

			for(int i=0; i<playerList.size(); i++) {

				JSONObject player_API = (JSONObject) playerList.get(i);             //get the player json
				JSONObject player_info = (JSONObject) player_API.get("player");    //getting the player info object 'player'

				//getting player's information
				String player_API_name = (String) player_info.get("name");  //ricezione name from API
				//unicode for strange characters
				//player_API_name=StringEscapeUtils.unescapeJava(player_API_name);


				String player_kickest_name=(String)player_kickest.get("Player");  //ricezione from kickest

				JSONArray player_info_stats=(JSONArray) player_API.get("statistics");
				JSONObject player_info_statistica=(JSONObject) player_info_stats.get(0);
				String player_API_team=(String) player_info_statistica.get("team");
				String player_kickest_team=(String)player_kickest.get("Team");

				if(player_API_name.equals(player_kickest_name) && player_API_team.equals(convert_team(player_kickest_team))){  //per risolvere problema G.Pezzella
					System.out.println(player_API_name);
					createPlayerDocuments(player_kickest,player_API,numero_player++,collection);
				}
				else{
					//potrebbe non esserci o avere quelle stringhe strane
					//player_API_name=StringEscapeUtils.unescapeJava(player_API_name);
					player_API_name=trasformation(player_API_name);

					if(player_API_name.equals(player_kickest_name) && player_API_team.equals(convert_team(player_kickest_team))){  //per risolvere problema G.Pezzella
						System.out.println(player_API_name);
						createPlayerDocuments(player_kickest,player_API,numero_player++,collection);
					}
				}


			}

		} //catch (FileNotFoundException e) {
		//e.printStackTrace();
		//}
		catch (IOException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
	}
	*/
/*
	public static void createPlayerDocuments(JSONObject player_kickest, JSONObject player_API, int id, MongoCollection<Document> collection){


		//JSONObject player = (JSONObject) playerList.get(i);			 //get the player json
		JSONObject player_info = (JSONObject) player_API.get("player"); 	//getting the player info object 'player'

		//getting player's information
		String playerFullname = (String)player_kickest.get("Player");  //sostituito ricezione name from APIFootball con ricezione name from kickest (matteo)
		//System.out.println(playerFullname);
		String playerFirstname = (String)player_info.get("firstname");
		String playerLastname = (String)player_info.get("lastname");
		Long playerAge = (Long) player_info.get("age");
		if(playerAge==null){
			//continue; //cosi ci leviamo i giocatori senza eta (in quanto probabilmente non hanno informazioni adeguate e non saranno presenti su kickest)
		}
		String playerNationality = (String)player_info.get("nationality");
		String playerHeight = (String)player_info.get("height");
		String playerWeight = (String)player_info.get("weight");

		JSONObject player_birth = (JSONObject) player_info.get("birth");
		String playerBirthDate = (String)player_birth.get("date");
		String playerBirthPlace = (String)player_birth.get("place");
		String playerBirthCountry =(String)player_birth.get("country");

		JSONArray statistics = (JSONArray)player_API.get("statistics"); 	//getting the player's statistics array 'statistics'
		JSONObject stats = (JSONObject)statistics.get(0); 			//retrieving the only element in the array

		//JSONObject team = (JSONObject)stats.get("team");
		String playerTeam = (String)stats.get("team");

		JSONObject games = (JSONObject)stats.get("games");
		String playerPosition =(String)games.get("position");

		//retrieve career if exists
		String player_career;
		try{
			//player.get("carrier").equals(null);
			player_career = (String) player_API.get("carrier");
		}
		catch(Exception e){
			player_career = "missing";
		}

		//creating the new player json file with only the needed information
		player_class new_player = new player_class(playerFullname, playerFirstname, playerLastname, id, Math.toIntExact(playerAge), playerBirthDate, playerBirthPlace, playerBirthCountry, playerNationality,
				playerHeight, playerWeight, playerPosition, playerTeam, player_career,30, 30);
		general_statistics_class new_gen_stats = new general_statistics_class();
		statistics_class new_stats = new statistics_class();
		new_player.general_statistics = new_gen_stats;
		new_player.statistics = new_stats;


		//turning the new created player class into a json file
		Gson gson = new Gson();
		String json = gson.toJson(new_player);
		Document doc = Document.parse(json); //converting the json to document

		//passing the json into mongoDB
		try {
			collection.insertOne(doc);
		}
		catch(Exception e) {
			System.out.print("MongoDB error, player " + playerFullname + " not inserted \n");
		}


	}
*/
	private static String trasformation(String player) {

		String[] words = player.split(" ");
		//System.out.println(words[0]);
		if (words.length > 1) {
			if(words.length==2)
				player = words[0].charAt(0) + ". " + words[1];
			else
				player=words[0].charAt(0) + ". " + words[1]+" "+words[2];
		} else {
			player = words[0];
		}
		//System.out.println("PLAYER TRASFORM:" + player);
		return player;
	}
	public static void create_random_formations(int matchday) throws ParseException {
		ArrayList<Document> user_list = get_users_collection_mongoDB();
		System.out.println("users retrieved..");
		ArrayList<Document> cards_list = get_cards_collection_mongoDB();
		System.out.println("cards retrieved..");
		for(int i=0;i<user_list.size();i++){
			//ArrayList<card_collection>Cards= collectionRedisDriver.load_collection(user_list.get(i).get("_id").toString());
			ArrayList<card_collection>Cards=create_random_team(cards_list);
			formation f=formation.getRandomFormation(Cards);
			//formations.put(matchday,f);
			formationMongoDriver.insert_formation(user_list.get(i).getString("username"),f,matchday);
			System.out.println("Formation created: "+(i+1)+"/"+user_list.size());
		}
	}

	private static ArrayList<card_collection> create_random_team(ArrayList<Document> cards_list) {

		ArrayList<card_collection> random_team=new ArrayList<>();


				int random_total_cards = ThreadLocalRandom.current().nextInt(30, 40);	//user's cards collection size

				for(int j=0; j<5; j++) {	//at least 5 attacker

					int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
					String card_position = cards_list.get(random_card).getString("position");
					if(!card_position.equals("Attacker")){
						j--;
						continue;
					}

					int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);
					String random_quantity = Integer.toString(rndm_quantity);	//card quantity

					String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
					String card_team = cards_list.get(random_card).getString("team");			//card team

					card_collection card=new card_collection(random_card,card_name,rndm_quantity,card_team,card_position);
					random_team.add(card);
				}
				random_total_cards -= 5;

				for(int j=0; j<4; j++) {	//at least 4 goalkeepers

					int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
					String card_position = cards_list.get(random_card).getString("position");
					if(!card_position.equals("Goalkeeper")){
						j--;
						continue;
					}

					int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);
					String random_quantity = Integer.toString(rndm_quantity);	//card quantity

					String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
					String card_team = cards_list.get(random_card).getString("team");			//card team

					card_collection card=new card_collection(random_card,card_name,rndm_quantity,card_team,card_position);
					random_team.add(card);
				}
				random_total_cards -= 4;

				for(int j=0; j<7; j++) {	//at least 7 midfielders

					int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
					String card_position = cards_list.get(random_card).getString("position");
					if(!card_position.equals("Midfielder")){
						j--;
						continue;
					}

					int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);
					String random_quantity = Integer.toString(rndm_quantity);	//card quantity

					String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
					String card_team = cards_list.get(random_card).getString("team");			//card team

					card_collection card=new card_collection(random_card,card_name,rndm_quantity,card_team,card_position);
					random_team.add(card);
				}
				random_total_cards -= 7;

				for(int j=0; j<7; j++) {	//at least 7 defenders

					int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
					String card_position = cards_list.get(random_card).getString("position");
					if(!card_position.equals("Defender")){
						j--;
						continue;
					}

					int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);
					String random_quantity = Integer.toString(rndm_quantity);	//card quantity

					String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
					String card_team = cards_list.get(random_card).getString("team");			//card team

					card_collection card=new card_collection(random_card,card_name,rndm_quantity,card_team,card_position);
					random_team.add(card);
				}
				random_total_cards -= 7;

				for(int j=0; j<random_total_cards; j++) {

					int random_card = ThreadLocalRandom.current().nextInt(0, cards_list.size()-1);	//card id
					int rndm_quantity = ThreadLocalRandom.current().nextInt(1, 4);
					String random_quantity = Integer.toString(rndm_quantity);	//card quantity
					String card_position = cards_list.get(random_card).getString("position");		//card position
					String card_name = cards_list.get(random_card).getString("fullname");			//card fullname
					String card_team = cards_list.get(random_card).getString("team");			//card team

					card_collection card=new card_collection(random_card,card_name,rndm_quantity,card_team,card_position);
					random_team.add(card);
				}
				return random_team;
	}

	private static String convert_team(String team) {

		switch(team){
			case "ATA":
				team="Atalanta";
				break;
			case "BOL":
				team="Bologna";
				break;
			case "CAG":
				team="Cagliari";
				break;
			case "EMP":
				team="Empoli";
				break;
			case "FIO":
				team="Fiorentina";
				break;
			case "GEN":
				team="Genoa";
				break;
			case "INT":
				team="Inter";
				break;
			case "JUV":
				team="Juventus";
				break;
			case "LAZ":
				team="Lazio";
				break;
			case "MIL":
				team="AC Milan";
				break;
			case "NAP":
				team="Napoli";
				break;
			case "ROM":
				team="AS Roma";
				break;
			case "SAL":
				team="Salernitana";
				break;
			case "SAM":
				team="Sampdoria";
				break;
			case "SAS":
				team="Sassuolo";
				break;
			case "SPE":
				team="Spezia";
				break;
			case "TOR":
				team="Torino";
				break;
			case "UDI":
				team="Udinese";
				break;
			case "VEN":
				team="Venezia";
				break;
			case "VER":
				team="Verona";
				break;


		}
		return team;
	}


}
