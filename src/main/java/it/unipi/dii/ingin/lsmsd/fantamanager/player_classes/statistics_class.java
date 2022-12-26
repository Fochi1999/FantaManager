package it.unipi.dii.ingin.lsmsd.fantamanager.player_classes;
//import it.unipi.lsmsd.player_classes.matchday_class;


import org.json.simple.JSONObject;

public class statistics_class {

	
	//Every matchday another element will be added here (up to 38 matchdays at the end of the football season)
	public int last_updated_matchday;
	public JSONObject matchday;    //cambiato da matteo per far funzionar cambiamento sotto
	
	
	
	//CONSTRUCTOR
	public statistics_class(){
		this.last_updated_matchday = 0;
		
		//this.matchday = new ArrayList<>();

		this.matchday = new JSONObject();  //matteo sostitution rispetto a riga sopra
		
	} 
	
}
