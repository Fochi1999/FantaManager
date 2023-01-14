package it.unipi.dii.ingin.lsmsd.fantamanager.trades;
import com.mongodb.client.*;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import org.json.simple.JSONObject;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.descending;

public class Trade{

	String trade_id;
	String user_from;
	String user_to;
	ArrayList<JSONObject> card_from;
	ArrayList<JSONObject> card_to;
	int credits;	// negative value: offered credits - positive value: wanted credits
	int status; 	// 0: pending - 1:completed
	

	
	//constructor
	public Trade(String trade_id_input, String user_from_input, String user_to_input, int credits_input,
				 ArrayList<JSONObject> card_from_input, ArrayList<JSONObject> card_to_input, int status_input) {
		
		//error prevention condition
		if(status_input > 0)
			status_input = 1;
		
		//initializing the values
		this.trade_id = trade_id_input;
		this.user_from = user_from_input;
		this.credits = credits_input;
		this.status = status_input; 	//setting the trade's status to 'pending'
		this.user_to = user_to_input; 	
		this.card_from = card_from_input;
		this.card_to = card_to_input;
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

	public ArrayList<JSONObject> get_card_from(){return this.card_from;}

	public ArrayList<JSONObject> get_card_to(){return this.card_to;}


	
	//update a trade's status from pending to complete
	public void complete_trade(String user_to_input) {
		this.user_to = user_to_input;
		this.status = 1; //setting the trade's status to 'complete'
	}

}


