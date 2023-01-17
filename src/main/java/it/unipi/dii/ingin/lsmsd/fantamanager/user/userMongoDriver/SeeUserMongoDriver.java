package it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver;

import org.bson.Document;
import com.mongodb.client.model.Filters;
import java.util.ArrayList;

import it.unipi.dii.ingin.lsmsd.fantamanager.trades.TradeMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collectionRedisDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;

public class SeeUserMongoDriver {

	public static Document search_user(String username) {
		
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
	
	
	public static boolean delete_user(String username, String _id) {
		
		ArrayList<card_collection> user_card_list = collectionRedisDriver.load_collection(_id);
		//REDIS
		try {
			collectionRedisDriver.delete_user_card_collection(_id);
			System.out.print("Redis..OK\t");
		}
		catch(Exception e) {
			System.out.println("Error! Cannot delete this user now. Try later");
			return false;
		}
		
		//MONGODB
		try {
			UserMongoDriver.openConnection();
			TradeMongoDriver.delete_all_trades(username);
			System.out.println(UserMongoDriver.collection.deleteOne(Filters.eq("username", username)));
			System.out.print("MongoDB..OK\t\n");
		}
		catch(Exception e) {	//handling exception
			int i=0;
			while(i<user_card_list.size()) {
				collectionRedisDriver.add_card_to_collection(user_card_list.get(i), _id);
				i++;
			}
			
			System.out.println("Error! Cannot delete this user now. Try later...card collection restored on Redis");
			return false;
		}
		UserMongoDriver.closeConnection();
		
		System.out.println("User '"+ username + "' successfully deleted from databases.");
		return true;
	}
	
}
