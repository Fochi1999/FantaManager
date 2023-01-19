package it.unipi.dii.ingin.lsmsd.fantamanager.admin;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.utilities;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;

public class calculate_matchday {



    public static void calulate_user_team_score(int matchday, Map<Long, Double> player_score){



        
        MongoClient mongoClient2 = MongoClients.create(global.MONGO_URI);

        // Access a Database
        MongoDatabase database2 = mongoClient2.getDatabase(global.DATABASE_NAME);

        // Access a Collection
        MongoCollection<Document> coll = database2.getCollection(global.USERS_COLLECTION_NAME);

        try(MongoCursor<Document> cursor=coll.find().projection(fields(include("formations"), include("username"))).iterator()){
            while(cursor.hasNext()) {
            //for(int j=0;j<10;j++){
                //Double total_score = Double.valueOf(0);

                String doc = cursor.next().toJson();
                //System.out.println(doc);
                JSONParser parser = new JSONParser();
                JSONObject json_formation = (JSONObject) parser.parse(doc);

                JSONObject formation = (JSONObject) json_formation.get("formations");
                String username = (String) json_formation.get("username");
                JSONObject match = (JSONObject) formation.get(String.valueOf(matchday));
                //System.out.println(match);

                if (match != null && (boolean)match.get("valid")) {
                    JSONArray modulo = (JSONArray) match.get("module");

                    JSONObject cards = (JSONObject) match.get("players");
                    //System.out.println(cards);
                    Double total_score = Double.valueOf(0);


                    riazzera_vote_card_of_team(cards);

                    for (int i = 0; i < 11; i++) {
                        JSONObject card = (JSONObject) cards.get(String.valueOf(i));
                        Double score = player_score.get(card.get("id"));
                        //inserisce il voto di ogni giocatore in formation
                        if (score == -5000) {
                            //se invece non gioca, l' inserimento viene fatto in take_card_from_bench
                            score = take_card_from_bench(i, player_score, cards, modulo, username, matchday);

                            //e in quello che non ha giocato ma era schierato titolare metto null
                            if (score != 0) {
                                //System.out.println("titolare sostituito:" + card.get("name"));
                                Bson filter = Filters.and(eq("username", username));
                                Bson update1 = Updates.set("formations." + matchday + ".players." + i + ".vote", null);
                                UpdateOptions options = new UpdateOptions().upsert(true);
                                System.out.println(coll.updateOne(filter, update1, options));

                                //setto voto anche nella variabile cards
                                card.put("vote", null);
                            } else {
                                //altrimenti significa che nemmeno quelli della panchina erano validi o erano gia presi in altre posizioni, quindi il voto resta zero per quello che era titolare e non ha avuto rimpiazzo dalla panchina
                                System.out.println(card.get("name") + " non sara rimpiazzato");
                            }
                        } else {

                            System.out.println("titolare" + card.get("name"));
                            Bson filter = Filters.and(eq("username", username));
                            Bson update1 = Updates.set("formations." + matchday + ".players." + i + ".vote", score);
                            UpdateOptions options = new UpdateOptions().upsert(true);
                            System.out.println(coll.updateOne(filter, update1, options));

                            //setto voto anche nella variabile cards
                            card.put("vote", score);
                        }
                        total_score += score;
                    }

                    //TODO controllo di questa query su tutti gli users
                    Bson filter = Filters.and(eq("username", username));
                    Bson update1 = Updates.set("formations." + matchday + ".tot", total_score);
                    UpdateOptions options = new UpdateOptions().upsert(true);
                    System.out.println(coll.updateOne(filter, update1, options));

                    //poi assegniamo il punteggio ottenuto (arrotondato per difetto) come crediti all' utente
                    System.out.println("punteggio:"+(int) Math.floor(total_score));
                    OptionsMongoDriver.update_user_credits(true,username, (int) Math.floor(total_score));
                }
                else{
                    //l-utente non ha inserito la formazione
                    System.out.println("formazione non inserita");

                    JSONObject formation_null=new JSONObject();
                    formation_null.put("valid",false);
                    formation_null.put("players",new JSONObject());
                    formation_null.put("tot",0.0);
                    formation_null.put("modulo",new ArrayList<>());
                    Bson filter = Filters.and(eq("username", username));
                    Bson update1 = Updates.set("formations." + matchday,formation_null);
                    UpdateOptions options = new UpdateOptions().upsert(true);
                    System.out.println(coll.updateOne(filter, update1, options));
                }
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }


    }

    private static Double take_card_from_bench(int i, Map<Long, Double> player_score, JSONObject cards, JSONArray modulo, String username, int matchday) {

        MongoClient mongoClient2 = MongoClients.create(global.MONGO_URI);

        // Access a Database
        MongoDatabase database2 = mongoClient2.getDatabase(global.DATABASE_NAME);

        // Access a Collection
        MongoCollection<Document> coll = database2.getCollection(global.USERS_COLLECTION_NAME);

        Double score = Double.valueOf(0);
        int place_of_bench=0;

                Long num_dif= (Long) modulo.get(1);
                Long num_mid= (Long) modulo.get(2);
                Long num_att= (Long) modulo.get(3);

                if(i==0){
                    //portiere
                    JSONObject card=(JSONObject) cards.get(String.valueOf(11)); //primo in panchina
                    score=player_score.get(card.get("id"));
                    if(score==-5000 || (Double)card.get("vote")!=0){
                        card=(JSONObject) cards.get(String.valueOf(12));  //secondo in panchina
                        score=player_score.get(card.get("id"));
                        if(score==-5000 || (Double)card.get("vote")!=0){
                            return Double.valueOf(0); //non avra rimpiazzo dalla panchina
                        }
                        place_of_bench=12;
                    }
                    else{
                        place_of_bench=11;
                    }
                }
                else if(i<=num_dif){
                        //difensore
                    JSONObject card=(JSONObject) cards.get(String.valueOf(13)); //primo in panchina
                    score=player_score.get(card.get("id"));
                    if(score==-5000 || (Double)card.get("vote")!=0){
                        card=(JSONObject) cards.get(String.valueOf(14));  //secondo in panchina
                        score=player_score.get(card.get("id"));
                        if(score==-5000 || (Double)card.get("vote")!=0){
                            return Double.valueOf(0);//non avra rimpiazzo dalla panchina
                        }
                        place_of_bench=14;
                    }
                    else{
                        place_of_bench=13;
                    }

                } else if (i<=num_dif+num_mid) {
                    //centrocampista
                    JSONObject card=(JSONObject) cards.get(String.valueOf(15)); //primo in panchina
                    score=player_score.get(card.get("id"));
                    if(score==-5000 || (Double)card.get("vote")!=0){
                        card=(JSONObject) cards.get(String.valueOf(16));  //secondo in panchina
                        score=player_score.get(card.get("id"));
                        if(score==-5000 || (Double)card.get("vote")!=0){
                            return Double.valueOf(0);//non avra rimpiazzo dalla panchina
                        }
                        place_of_bench=16;
                    }
                    else{
                        place_of_bench=15;
                    }
                }
                else{
                    //attaccante
                    JSONObject card=(JSONObject) cards.get(String.valueOf(17)); //primo in panchina
                    score=player_score.get(card.get("id"));
                    if(score==-5000 || (Double)card.get("vote")!=0){
                        card=(JSONObject) cards.get(String.valueOf(18));  //secondo in panchina
                        score=player_score.get(card.get("id"));
                        if(score==-5000 || (Double)card.get("vote")!=0){
                            return Double.valueOf(0);//non avra rimpiazzo dalla panchina
                        }
                        place_of_bench=18;
                    }
                    else{
                        place_of_bench=17;
                    }
                }

        JSONObject card=(JSONObject) cards.get(String.valueOf(place_of_bench));
        //System.out.println("dalla panchina:"+card.get("name"));
        //System.out.println("score dalla panchina:"+player_score.get(card.get("id")));

        Bson filter = Filters.and(eq("username", username));
        Bson update1 = Updates.set("formations." + matchday + ".players."+place_of_bench+".vote",score);
        UpdateOptions options = new UpdateOptions().upsert(true);
        System.out.println(coll.updateOne(filter, update1, options));

        //setto voto anche nella variabile cards
        card.put("vote",score);


        return score;
    }

    private static void riazzera_vote_card_of_team(JSONObject cards) {

       for(int i=0;i<18;i++){
           JSONObject card=(JSONObject) cards.get(String.valueOf(i));
           card.put("vote", Double.valueOf(0));
       }

    }

    public static void calculate_card_score(int matchday) {

        Map<Long,Double> player_score=new HashMap<Long,Double>();  //per calcolo score di un team di un player

       
        MongoClient mongoClient2 = MongoClients.create(global.MONGO_URI);

        // Access a Database
        MongoDatabase database2 = mongoClient2.getDatabase(global.DATABASE_NAME);

        // Access a Collection
        MongoCollection<Document> coll = database2.getCollection(global.CARDS_COLLECTION_NAME);

        //scorre tutti gli elementi del Document
        try(MongoCursor<Document> cursor=coll.find().iterator()){
            while(cursor.hasNext()){
                String player=cursor.next().toJson();
                //System.out.println(player);
                JSONParser parser = new JSONParser();
                JSONObject json_player = (JSONObject) parser.parse(player);

                String player_name= (String) json_player.get("fullname");
                Long player_id= (Long) json_player.get("player_id");
                String role=(String) json_player.get("position");
                //System.out.println(player_name);

                int credits= ((Long) json_player.get("credits")).intValue();  //prende credits player per poi modificarlo

                String team= (String) json_player.get("team");
                JSONObject gen_stats= (JSONObject) json_player.get("general_statistics");

                JSONObject playerObject = (JSONObject) json_player.get("statistics");
                JSONObject playermatch = (JSONObject) playerObject.get("matchday");
                JSONObject matchday_K = (JSONObject) playermatch.get("matchday" + matchday);
                JSONObject matchday_stat = (JSONObject) matchday_K.get("stats");

                //take all statistics for matchday of that player
                //String cr= (String) matchday_stat.get("CR");
                String plus= (String) matchday_stat.get("Plus");  //TODO usare
                //String apps= (String) matchday_stat.get("Apps");
                String starter= (String) matchday_stat.get("Starter");
                String mins= (String) matchday_stat.get("Mins");
                String goals= (String) matchday_stat.get("Goals");
                String shots= (String) matchday_stat.get("Shots");
                String on_tar_shots= (String) matchday_stat.get("On Tar. Shots");
                String pen_goals= (String) matchday_stat.get("Pen Goals");
                String successful_dribbles= (String) matchday_stat.get("Successful Dribbles");
                //String ast= (String) matchday_stat.get("Ast");  //gia presente assist
                String acc_pass= (String) matchday_stat.get("Acc Pass");
                String key_pass= (String) matchday_stat.get("Key Pass");
                String fouls= (String) matchday_stat.get("Fouls");
                String was_fouled= (String) matchday_stat.get("Was Fouled");
                String yc= (String) matchday_stat.get("YC");
                String rc= (String) matchday_stat.get("RC");
                String rec_ball= (String) matchday_stat.get("Rec Ball");
                String tackles= (String) matchday_stat.get("Tackles");
                String clean_sheets= (String) matchday_stat.get("Clean Sheets");
                String woods= (String) matchday_stat.get("Woods");
                String headed_goals= (String) matchday_stat.get("Headed Goals");
                String freekick_goals= (String) matchday_stat.get("Freekick Goals");
                String big_chance_missed= (String) matchday_stat.get("Big Chance Missed");
                String goal_min= (String) matchday_stat.get("Goal/Min");
                String shot_con_rate= (String) matchday_stat.get("Shot Con Rate");
                String total_dribbles= (String) matchday_stat.get("Total Dribbles");
                String assists= (String) matchday_stat.get("Assists");
                String passes= (String) matchday_stat.get("Passes");
                String acc_pass_per= (String) matchday_stat.get("Acc Pass %");
                String big_chance_created= (String) matchday_stat.get("Big Chance Created");
                String cross= (String) matchday_stat.get("Cross");
                String acc_cross= (String) matchday_stat.get("Acc Cross");
                String cross_no_corner= (String) matchday_stat.get("Cross no Corner");
                String second_yc= (String) matchday_stat.get("2nd YC");
                String err_leading_to_goal= (String) matchday_stat.get("Err leading to Goal");
                String og= (String) matchday_stat.get("OG");
                String interception= (String) matchday_stat.get("Interception");
                String won_duels= (String) matchday_stat.get("Won Duels");
                String lost_duels= (String) matchday_stat.get("Lost Duels");
                String aer_duels_won= (String) matchday_stat.get("Aer. Duels Won");
                String aer_duels_lost= (String) matchday_stat.get("Aer. Duels Lost");
                String saves= (String) matchday_stat.get("Saves");
                String goals_conceded= (String) matchday_stat.get("Goals Conceded");
                String pen_saves= (String) matchday_stat.get("Pen Saves");
                String pen_goals_conceded= (String) matchday_stat.get("Pen Goals Conceded");
                String xG=(String) matchday_stat.get("xG");
                String xGBuildup=(String) matchday_stat.get("xGBuildup");
                String xGChain=(String) matchday_stat.get("xGChain");
                String xA=(String) matchday_stat.get("xA");

                float score;

                if(Integer.parseInt(mins)>14) {

                    score= (float) (6+3*Float.parseFloat(goals)+Float.parseFloat(headed_goals)/2+Float.parseFloat(assists)+Float.parseFloat(was_fouled)/10-Float.parseFloat(yc)/2-Float.parseFloat(rc)+Float.parseFloat(on_tar_shots)/5
                                                +(Float.parseFloat(goals)-Float.parseFloat(xG))-Float.parseFloat(big_chance_missed)+Float.parseFloat(rec_ball)/10+Float.parseFloat(interception)/10+(Float.parseFloat(assists)-Float.parseFloat(xA))
                                                +(Float.parseFloat(aer_duels_won)-Float.parseFloat(aer_duels_lost))/5-Float.parseFloat(fouls)/10+Float.parseFloat(successful_dribbles)/10+(Float.parseFloat(on_tar_shots)*2-Float.parseFloat(shots))/10
                                                +Float.parseFloat(cross_no_corner)/10+(1.2*Float.parseFloat(acc_pass)-Float.parseFloat(passes))+Float.parseFloat(woods)/10+Float.parseFloat(xGBuildup)+Float.parseFloat(xGChain)-Float.parseFloat(err_leading_to_goal)
                                                +Float.parseFloat(saves)/2+Float.parseFloat(starter)/10-2*Float.parseFloat(og)-0.3*Float.parseFloat(lost_duels)
                                                +Float.parseFloat(big_chance_created)/2+Float.parseFloat(tackles)/10+Float.parseFloat(won_duels)/10-0.7*Float.parseFloat(second_yc)+Float.parseFloat(freekick_goals)/2
                                                +Float.parseFloat(cross)/10+Float.parseFloat(key_pass)/5+Float.parseFloat(total_dribbles)/10+ Float.parseFloat(acc_cross)/100);


                    if(Float.parseFloat(acc_pass_per)>90){
                            score+=0.5;
                    }

                    if(role.equals("Goalkeeper")){
                            score+=3*Float.parseFloat(pen_saves);
                            score+=Float.parseFloat(clean_sheets);
                            score-=Float.parseFloat(goals_conceded)/5;
                    }
                    else{
                            score+=Float.parseFloat(clean_sheets)*0.3;
                    }

                    if(Float.parseFloat(shot_con_rate)<30){
                            score-=1;
                    }

                }
                else{
                    score=-5000;  //per riconoscere che non ha giocato
                }

                //System.out.println(score);
                int mod_value=calculate_mod_value(score,plus);
                //matchday_stat.put("score",score);  //inserisce score nel matchday
                //System.out.println(matchday_stat.get("score"));

                JSONObject score_value=new JSONObject();
                score_value.put("score",score);
                score_value.put("modif_value",mod_value);

                //Bson filter = Filters.eq("fullname", player_name);   //change 1045
                Bson filter= Filters.and(eq("fullname", player_name),eq("team", team));  //AND per risolvere problema L.Pellegrini
                Bson update1 = Updates.set("statistics.matchday.matchday" + matchday+".score-value", score_value);
                Bson update2= Updates.set("credits", credits+mod_value);
                Bson update=Updates.combine(update1,update2);
                UpdateOptions options = new UpdateOptions().upsert(true);
                System.out.println(coll.updateOne(filter, update, options));

                player_score.put(player_id,Double.valueOf(score));

                general_statistics.handle_general_statistics(player_id,gen_stats,mins,starter,shots,on_tar_shots,goals,goals_conceded,assists,saves,passes,key_pass,acc_pass,tackles,interception,lost_duels,won_duels,total_dribbles,successful_dribbles,was_fouled,fouls,yc,rc,second_yc,pen_saves,pen_goals,pen_goals_conceded);

            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        System.out.println("calculate matchday completed");
        //formation.calculate_team_score(player_score);
        calulate_user_team_score(matchday,player_score);   //per calcolo score di un team di un user

        utilities.update_matchday(matchday); //da qui cambia vettore e anche la variabile della prossima giornata su redis
    }


    private static int calculate_mod_value(float score, String plus) {  //in base allo score della giornata calcola di quanto deve essere modificato il valore del giocatore

        int mod=0;
        if(score<=0){
            mod=-1;
        }
        else{
            if(score<7){
                mod=0;
            }
            else{
                if(score>=15) {
                    mod=2;
                }
                else{
                    mod=1;
                }
            }
        }
        int pluss= (int) Math.floor(Float.parseFloat(plus));
        mod=mod+pluss;
        return mod;
    }

}
