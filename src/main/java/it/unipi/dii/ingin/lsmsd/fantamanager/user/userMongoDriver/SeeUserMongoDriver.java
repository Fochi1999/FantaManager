package it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver;

import org.bson.Document;
import com.mongodb.client.model.Filters;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.trades.TradeMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;

public class SeeUserMongoDriver {

	public static Document search_user(String username) {
		
		//connecting to mongoDB 
		//MongoClient myClient = MongoClients.create(global.MONGO_URI);
		//MongoDatabase database = myClient.getDatabase(global.DATABASE_NAME);
		//MongoCollection<Document> collection = database.getCollection(global.USERS_COLLECTION_NAME);
		UserMongoDriver.openConnection();
    	
		//searching user
		Document user_doc;
    	try {
    		user_doc = UserMongoDriver.collection.find(Filters.eq("username", username)).first();
    	}catch (Exception e) {
    		System.out.println("Error on searching user");
    		//myClient.close();
			UserMongoDriver.closeConnection();
    		return null;
    	}
    	
    	//myClient.close();
		UserMongoDriver.closeConnection();
    	return user_doc;
	}	
	
	
	public static void delete_user(String username) {
		
		//TODO remove all trades from db and all keyvalues on redis
		UserMongoDriver.openConnection();
			
		//deleting all trades
		TradeMongoDriver.delete_all_trades(username);
		
		//deleting all keys on redis
		collection.delete_user_card_collection(global.id_user);
		
		//delete user
		try {
			System.out.println(UserMongoDriver.collection.deleteOne(Filters.eq("username", username)));
		}
		catch(Exception e) {
			System.out.println("Error! Cannot delete this user now. Try later");
			UserMongoDriver.closeConnection();
			return;
		}
		UserMongoDriver.closeConnection();
		
		System.out.println("User '"+ username + "' successfully deleted.");
		return;
	}
	
}
