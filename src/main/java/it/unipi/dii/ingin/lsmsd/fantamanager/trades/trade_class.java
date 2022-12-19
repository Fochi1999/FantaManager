package it.unipi.dii.ingin.lsmsd.fantamanager.trades;
import java.util.ArrayList;

public class trade_class{

	int trade_id;
	String user_from;
	String user_to;
	ArrayList<Integer> player_from;
	ArrayList<Integer> player_to;
	int credits;	// negative value: offered credits - positive value: wanted credits
	int status; 	// 0: pending - 1:completed
	

	
	//constructor
	public trade_class(int trade_id_input, String user_from_input, String user_to_input, int credits_input, 
			ArrayList<Integer> player_from_input, ArrayList<Integer> player_to_input, int status_input) {
		
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
	
}


