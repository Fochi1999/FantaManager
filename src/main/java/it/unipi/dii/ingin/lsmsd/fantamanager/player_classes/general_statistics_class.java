package it.unipi.dii.ingin.lsmsd.fantamanager.player_classes;

import it.unipi.dii.ingin.lsmsd.fantamanager.admin.general_statistics;

public class general_statistics_class {

	public long games_appearences;
	public long games_lineup;
	public long games_minutes;
 
	public long substitutes_in;
	public long substitutes_out;
	public long substitutes_bench;

	public long shots_total;
	public long shots_on;
 
	public long goals_total;
	public long goals_conceded;
	public long goals_assists;
	public long goals_saves;
 
	public long passes_total;
	public long passes_key;
	public long passes_accuracy;
	 
	public long tackles_total;
	public long tackles_blocks;
	public long tackles_interceptions;

	public long duels_total;
	public long duels_won;

	public long dribbles_attempts;
	public long dribbles_success;
	public long dribbles_past;

	public long fouls_drawn;
	public long fouls_committed;

	public long cards_yellow;
	public long cards_red;
	public long cards_yellowred;

	public long penalty_won;
	public long penalty_commutted;
	public long penalty_scored;
	public long penalty_missed;
	public long penalty_saved;
	
	//Constructor with values
	/*public static general_statistics_class add_info(general_statistics gen_stats, long games_appearences, long games_lineup, long games_minutes, long substitutes_in, long substitutes_out, long substitutes_bench, long shots_total, long shots_on, long goals_total, long goals_conceded, long goals_assists,
													long goals_saves, long passes_total, long passes_key, long passes_accuracy, long tackles_total, long tackles_blocks, long tackles_interceptions, long duels_total, long duels_won, long dribbles_attempts,
													long dribbles_success, long dribbles_past, long fouls_drawn, long fouls_committed, long cards_yellow, long cards_red, long cards_yellowred, long penalty_won, long penalty_commutted,
													long penalty_scored, long penalty_missed, long penalty_saved){

		gen_stats.games_appearences += games_appearences;
		gen_stats.games_lineup += games_lineup;
		gen_stats.games_minutes += games_minutes;

		gen_stats.substitutes_in += substitutes_in;
		gen_stats.substitutes_out += substitutes_out;
		gen_stats.substitutes_bench += substitutes_bench;

		gen_stats.shots_total += shots_total;
		gen_stats.shots_on += shots_on;

		gen_stats.goals_total += goals_total;
		gen_stats.goals_conceded += goals_conceded;
		gen_stats.goals_assists += goals_assists;
		gen_stats.goals_saves += goals_saves;

		gen_stats.passes_total += passes_total;
		gen_stats.passes_key += passes_key;
		gen_stats.passes_accuracy += passes_accuracy;

		gen_stats.tackles_total += tackles_total;
		gen_stats.tackles_blocks += tackles_blocks;
		gen_stats.tackles_interceptions += tackles_interceptions;

		gen_stats.duels_total += duels_total;
		gen_stats.duels_won += duels_won;

		gen_stats.dribbles_attempts += dribbles_attempts;
		gen_stats.dribbles_success += dribbles_success;
		gen_stats.dribbles_past += dribbles_past;

		gen_stats.fouls_drawn += fouls_drawn;
		gen_stats.fouls_committed += fouls_committed;

		gen_stats.cards_yellow += cards_yellow;
		gen_stats.cards_red += cards_red;
		gen_stats.cards_yellowred += cards_yellowred;

		gen_stats.penalty_won += penalty_won;
		gen_stats.penalty_commutted += penalty_commutted;
		gen_stats.penalty_scored += penalty_scored;
		gen_stats.penalty_missed += penalty_missed;
		gen_stats.penalty_saved += penalty_saved;

		return gen_stats;
	}*/
	//CONSTRUCTOR
	public general_statistics_class(){
		this.games_appearences = 0;
		this.games_lineup = 0;
		this.games_minutes = 0;

		this.substitutes_in = 0;
		this.substitutes_out = 0;
		this.substitutes_bench = 0;

		this.shots_total = 0;
		this.shots_on = 0;

		this.goals_total = 0;
		this.goals_conceded = 0;
		this.goals_assists = 0;
		this.goals_saves = 0;

		this.passes_total = 0;
		this.passes_key = 0;
		this.passes_accuracy = 0;

		this.tackles_total = 0;
		this.tackles_blocks = 0;
		this.tackles_interceptions = 0;

		this.duels_total = 0;
		this.duels_won = 0;

		this.dribbles_attempts = 0;
		this.dribbles_success = 0;
		this.dribbles_past = 0;

		this.fouls_drawn = 0;
		this.fouls_committed = 0;

		this.cards_yellow = 0;
		this.cards_red = 0;
		this.cards_yellowred = 0;

		this.penalty_won = 0;
		this.penalty_commutted = 0;
		this.penalty_scored = 0;
		this.penalty_missed = 0;
		this.penalty_saved = 0;
	}
	
}
	
	
	
	

