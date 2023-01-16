package it.unipi.dii.ingin.lsmsd.fantamanager.admin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.CardMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.general_statistics_class;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class general_statistics {

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

    //CONSTRUCTOR
    public general_statistics(){
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

    public static general_statistics add_info(general_statistics gen_stats, long games_appearences, long games_lineup, long games_minutes, long substitutes_in, long substitutes_out, long substitutes_bench, long shots_total, long shots_on, long goals_total, long goals_conceded, long goals_assists,
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
    }


    public static void handle_general_statistics(Long player_id, JSONObject gen_stats, String mins, String starter, String shots, String on_tar_shots, String goals, String goals_conceded, String assists, String saves, String passes, String key_pass, String acc_pass, String tackles, String interception, String lost_duels, String won_duels, String total_dribbles, String successful_dribbles, String was_fouled, String fouls, String yc, String rc, String second_yc, String pen_saves, String pen_goals, String pen_goals_conceded) throws ParseException {
        //prendi oggetto general_statistics esistente su mongo
        //crea oggetto general_statistics nuovo
        //e su mongo carichi uno sommato tra i due

        //MongoClient mongoClient2 = MongoClients.create(global.MONGO_URI);

        // Access a Database
        //MongoDatabase database2 = mongoClient2.getDatabase(global.DATABASE_NAME);

        // Access a Collection
        //MongoCollection<Document> coll = database2.getCollection(global.CARDS_COLLECTION_NAME);


        long games_appearence=0;
        if(Integer.parseInt(mins)>0){
            games_appearence= Long.valueOf(1);
        }

        long min=Long.valueOf(mins);
        long start=Long.valueOf(starter);
        long shot=Long.valueOf(shots);
        long on_tar_shot=Long.valueOf(on_tar_shots);
        long goal=Long.valueOf(goals);
        long goal_con=Long.valueOf(goals_conceded);
        long assist=Long.valueOf(assists);
        long save=Long.valueOf(saves);
        long pass=Long.valueOf(passes);
        long key_p=Long.valueOf(key_pass);
        long acc_p=Long.valueOf(acc_pass);
        long tackle=Long.valueOf(tackles);
        long tackle_block=Long.valueOf(interception);
        long tackle_inter=Long.valueOf(interception);
        long total_duel=Long.valueOf(lost_duels+won_duels);
        long duel_won=Long.valueOf(won_duels);
        long dribbles=Long.valueOf(total_dribbles);
        long succ_dribbles=Long.valueOf(successful_dribbles);
        long dribbles_past=Long.valueOf(0);  //dribbles past
        long was_foul=Long.valueOf(was_fouled);
        long foul=Long.valueOf(fouls);
        long yellow_card=Long.valueOf(yc);
        long red_card=Long.valueOf(rc);
        long second_yellow=Long.valueOf(second_yc);
        long penalty_won=Long.valueOf(0); //penalty won
        long penalty_commuted=Long.valueOf(0); //penalty commuted
        long pen_goal=Long.valueOf(pen_goals);
        long pen_missed=Long.valueOf(0);  //penalty_missed
        long pen_save=Long.valueOf(pen_saves);
        long substitutes_in=0;
        long substitutes_out = 0;
        long substitutes_bench=0;  //che differenza con quello sopra?

        if(start==1 && min<90){
            substitutes_out=1;
        }
        if(start==0 && min>0){
            substitutes_in=1;
            substitutes_bench=1;
        }



        general_statistics old_gen_stats=new general_statistics();

        System.out.println(gen_stats.toString());

        try {
            ObjectMapper mapper = new ObjectMapper();
            TypeReference<general_statistics> typeRef = new TypeReference<general_statistics>() {};
            old_gen_stats = mapper.readValue(gen_stats.toString(), typeRef);
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("gen_stats non trovate");
        }

        general_statistics general_stats_match =add_info(old_gen_stats,games_appearence,start,min,substitutes_in,substitutes_out,substitutes_bench,shot,on_tar_shot,goal,goal_con,assist,save,pass,key_p,acc_p,tackle,tackle_block,tackle_inter,
                total_duel,duel_won,dribbles,succ_dribbles,dribbles_past,was_foul,foul,yellow_card,red_card,second_yellow,penalty_won,penalty_commuted,pen_goal,pen_missed,pen_save);

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String gen_string = null;
        try {
            gen_string = ow.writeValueAsString(general_stats_match);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        //Gson gson = new Gson();
        //String gen_string = gson.toJson(general_stats_match);
        JSONParser parser = new JSONParser();
        JSONObject gen_json = (JSONObject) parser.parse(gen_string);

        //Bson filter= Filters.eq("player_id", player_id);
        //Bson update = Updates.set("general_statistics",gen_json);
        //UpdateOptions options = new UpdateOptions().upsert(true);
        //System.out.println(coll.updateOne(filter, update, options));

        CardMongoDriver.update_gen_stats(player_id,gen_json);

    }
}
