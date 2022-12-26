package it.unipi.dii.ingin.lsmsd.fantamanager.player_classes;
import java.util.ArrayList;

public class matchday_class {

	public float CR; 
    public float plus; 
    public int apps; 
    public int starter; 
    public int mins; 
    public int goals; 
    public int shots; 
    public int on_tar_shots; 
    public int pen_goals; 
    public int successful_dribbles; 
    public int ast;
    public int acc_pass; 
    public int key_pass; 
    public int fouls; 
    public int was_fouled; 
    public int YC; 
    public int RC; 
    public int rec_ball; 
    public int tackles; 
    public int clean_sheets;
    public float xG;
    public int assist;
    public float xG_chain;
    public float xG_buildup;
    public float xA;
	public ArrayList<shots_info_class> shots_info;


	
	
	//CONSTRUCTOR 
	public matchday_class(float CR_input, float plus_input, int apps_input, int starter_input, int mins_input, int goals_input,
                          int shots_input, int on_tar_shots_input, int pen_goals_input, int successful_dribbles_input, int ast_input,
                          int acc_pass_input, int key_pass_input, int fouls_input, int was_fouled_input, int YC_input, int RC_input,
                          int rec_ball_input, int tackles_input, int clean_sheets_input, float xG_input, int assist_input,
                          float xG_chain_input, float xG_buildup_input, float xA_input)//, shots_info_class shots_info_input)
	{
		this.CR = CR_input; 
		this.plus = plus_input; 
		this.apps = apps_input; 
		this.starter = starter_input; 
		this.mins = mins_input; 
		this.goals = goals_input; 
		this.shots = shots_input; 
		this.on_tar_shots = on_tar_shots_input;
	    this.pen_goals = pen_goals_input;
	    this.successful_dribbles = successful_dribbles_input; 
	    this.ast = ast_input; 
	    this.acc_pass = acc_pass_input; 
	    this.key_pass = key_pass_input; 
	    this.fouls = fouls_input;
	    this.was_fouled = was_fouled_input;
	    this.YC = YC_input; 
	    this.RC = RC_input; 
	    this.rec_ball = rec_ball_input; 
	    this.tackles = tackles_input; 
	    this.clean_sheets = clean_sheets_input; 
	    this.xG = xG_input; 
	    this.assist = assist_input;
	    this.xG_chain = xG_chain_input; 
	    this.xG_buildup = xG_buildup_input; 
	    this.xA = xA_input; 
	    this.shots_info = new ArrayList<>(); //shots_info_input;	
	}

	//CONSTRUCTOR (NO INPUT)
	public matchday_class()
	{
		this.CR = 0; 
		this.plus = 0; 
		this.apps = 0; 
		this.starter = 0; 
		this.mins = 0; 
		this.goals = 0; 
		this.shots = 0; 
		this.on_tar_shots = 0;
	    this.pen_goals = 0;
	    this.successful_dribbles = 0; 
	    this.ast = 0; 
	    this.acc_pass = 0; 
	    this.key_pass = 0; 
	    this.fouls = 0;
	    this.was_fouled = 0;
	    this.YC = 0; 
	    this.RC = 0; 
	    this.rec_ball = 0; 
	    this.tackles = 0; 
	    this.clean_sheets = 0; 
	    this.xG = 0; 
	    this.assist = 0;
	    this.xG_chain = 0; 
	    this.xG_buildup = 0; 
	    this.xA = 0; 
	    this.shots_info = new ArrayList<>(); //shots_info_input;	
	}
	
}






