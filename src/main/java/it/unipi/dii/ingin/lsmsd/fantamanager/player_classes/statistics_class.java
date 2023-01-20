package it.unipi.dii.ingin.lsmsd.fantamanager.player_classes;
//import it.unipi.lsmsd.player_classes.matchday_class;


import org.json.simple.JSONObject;

import java.util.ArrayList;

/*public class statistics_class {

	
	//Every matchday another element will be added here (up to 38 matchdays at the end of the football season)
	//public int last_updated_matchday;
	public JSONObject matchday;    //cambiato da matteo per far funzionar cambiamento sotto
	
	
	
	//CONSTRUCTOR
	public statistics_class(){
		//this.last_updated_matchday = 0;
		
		//this.matchday = new ArrayList<>();

		this.matchday = new JSONObject();  //matteo sostitution rispetto a riga sopra
		
	} 
	
}*/
public class statistics_class {


	//Every matchday another element will be added here (up to 38 matchdays at the end of the football season)
	//public int last_updated_matchday;
	public JSONObject matchday;    //cambiato da matteo per far funzionar cambiamento sotto


	//CONSTRUCTOR
	public statistics_class() {
		//this.last_updated_matchday = 0;

		//this.matchday = new ArrayList<>();

		//this.matchday = new JSONObject(); //matteo sostitution rispetto a riga sopra

		JSONObject match = new JSONObject();
		match.put("Big Chance Missed", "0");
		match.put("Goal/Min", "0");
		match.put("Cross no Corner", "0");
		match.put("Rec Ball", "0");
		match.put("YC", "0");
		match.put("Apps", "0");
		match.put("Was Fouled", "0");
		match.put("Pen Saves", "0");
		match.put("Passes", "0");
		match.put("Successful Dribbles", "0");
		match.put("Interception", "0");
		match.put("Plus", "0");
		match.put("Acc Pass", "0");
		match.put("Acc Pass %", "0");
		match.put("Clean Sheet", "0");
		match.put("On Tar. Shots", "0");
		match.put("Mins", "0");
		match.put("Aer. Duels Won", "0");
		match.put("RC", "0");
		match.put("Assists", "0");
		match.put("Woods", "0");
		match.put("Goals", "0");
		match.put("Fouls", "0");
		match.put("XGBuildup", "0");
		match.put("Aer. Duels Lost", "0");
		match.put("Pen Goals Conceded", "0");
		match.put("Key Pass", "0");
		match.put("Shot Con Rate", "0");
		match.put("Cross", "0");
		match.put("XGChain", "0");
		match.put("Err leading to Goals", "0");
		match.put("Saves", "0");
		match.put("xA", "0");
		match.put("Freekick Goals", "0");
		match.put("Starter", "0");
		match.put("2nd YC", "0");
		match.put("xG", "0");
		match.put("Won Duels", "0");
		match.put("OG", "0");
		match.put("Shots", "0");
		match.put("Acc Cross", "0");
		match.put("Headed Goals", "0");
		match.put("Ast", "0");
		match.put("Pen Goals", "0");
		match.put("Total Dribbles", "0");
		match.put("Big Chance Created", "0");
		match.put("CR", "0");
		match.put("Tackles", "0");
		match.put("Lost Duels", "0");
		match.put("Goals Conceded", "0");
		match.put("shotsInfo", new ArrayList<>());


		JSONObject matchday = new JSONObject();
		for (int i = 0; i < 38; i++) {
			JSONObject match_json = new JSONObject();
			JSONObject match_stats = new JSONObject();
			JSONObject match_score = new JSONObject();
			//matchday_class match=new matchday_class();
			match_json.put("stats", match);
			match_score.put("score", Double.valueOf(0));
			match_score.put("modif_value", 0);
			match_json.put("score-value", match_score);
			matchday.put("matchday" + (i + 1), match_json);
		}
		System.out.println(matchday);
		this.matchday = matchday;
	}

}
