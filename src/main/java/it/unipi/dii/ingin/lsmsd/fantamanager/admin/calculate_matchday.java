package it.unipi.dii.ingin.lsmsd.fantamanager.admin;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;

public class calculate_matchday {



    public static void calulate_user_team_score(int matchday,Map<String,Float> player_score){

        Float total_score = Float.valueOf(0);

        //String url = "mongodb://localhost:27017";
        MongoClient mongoClient2 = MongoClients.create(global.MONGO_URI);

        // Access a Database
        MongoDatabase database2 = mongoClient2.getDatabase(global.DATABASE_NAME);

        // Access a Collection
        MongoCollection<Document> coll = database2.getCollection(global.USERS_COLLECTION_NAME);

        try(MongoCursor<Document> cursor=coll.find().projection(fields(include("formations"), include("username"))).iterator()){
            while(cursor.hasNext()) {
                String doc = cursor.next().toJson();
                System.out.println(doc);
                JSONParser parser = new JSONParser();
                JSONObject json_formation = (JSONObject) parser.parse(doc);

                JSONObject formation= (JSONObject) json_formation.get("formations");
                JSONObject match = (JSONObject) formation.get(String.valueOf(matchday));
                JSONArray modulo=(JSONArray) match.get("modulo");

                JSONObject cards = (JSONObject) match.get("players");
                System.out.println(cards);

                String username= (String) json_formation.get("username");

                riazzera_vote_card_of_team(cards);

                for (int i = 0; i < 11; i++) { //TODO farla a modo, qui prende solo i primi 11
                    JSONObject card=(JSONObject) cards.get(String.valueOf(i));
                    float score=player_score.get(card.get("name"));
                    //inserisce il voto di ogni giocatore in formation   //TODO ragionare se sia corretto o meno avere questa ridondanza di avere il voto sia qui che sulle cards
                    if(score==-5000){
                        //se invece non gioca, l' inserimento viene fatto in take_card_from_bench
                        score=take_card_from_bench(i,player_score,cards,modulo,username,matchday);

                        //e in quello che non ha giocato ma era schierato titolare metto null
                        if(score!=0) {
                            System.out.println("titolare sostituito:" + card.get("name"));
                            Bson filter = Filters.and(eq("username", username));
                            Bson update1 = Updates.set("formations." + matchday + ".players." + i + ".vote", null);
                            UpdateOptions options = new UpdateOptions().upsert(true);
                            System.out.println(coll.updateOne(filter, update1, options));

                            //setto voto anche nella variabile cards
                            card.put("vote", null);
                        }
                        else{
                            //altrimenti significa che nemmeno quelli della panchina erano validi o erano gia presi in altre posizioni, quindi il voto resta zero per quello che era titolare e non ha avuto rimpiazzo dalla panchina
                            System.out.println(card.get("name")+" non sara rimpiazzato");
                        }
                    }
                    else {

                        System.out.println("titolare"+card.get("name"));
                        Bson filter = Filters.and(eq("username", username));
                        Bson update1 = Updates.set("formations." + matchday + ".players." + i + ".vote", score);
                        UpdateOptions options = new UpdateOptions().upsert(true);
                        System.out.println(coll.updateOne(filter, update1, options));

                        //setto voto anche nella variabile cards
                        card.put("vote",score);
                    }
                    total_score+=score;
                }

                //TODO controllo di questa query su tutti gli users
                Bson filter = Filters.and(eq("username", username));
                Bson update1 = Updates.set("formations." + matchday + ".tot",total_score);
                UpdateOptions options = new UpdateOptions().upsert(true);
                System.out.println(coll.updateOne(filter, update1, options));
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


    }

    private static float take_card_from_bench(int i, Map<String, Float> player_score, JSONObject cards, JSONArray modulo, String username, int matchday) {

        MongoClient mongoClient2 = MongoClients.create(global.MONGO_URI);

        // Access a Database
        MongoDatabase database2 = mongoClient2.getDatabase(global.DATABASE_NAME);

        // Access a Collection
        MongoCollection<Document> coll = database2.getCollection(global.USERS_COLLECTION_NAME);

        float score = 0;
        int place_of_bench=0;

                Long num_dif= (Long) modulo.get(1);
                Long num_mid= (Long) modulo.get(2);
                Long num_att= (Long) modulo.get(3);

                if(i==0){
                    //portiere
                    JSONObject card=(JSONObject) cards.get(String.valueOf(11)); //primo in panchina
                    score=player_score.get(card.get("name"));
                    if(score==-5000 || (Double)card.get("vote")!=0){
                        card=(JSONObject) cards.get(String.valueOf(12));  //secondo in panchina
                        score=player_score.get(card.get("name"));
                        if(score==-5000 || (Double)card.get("vote")!=0){
                            return 0; //non avra rimpiazzo dalla panchina
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
                    score=player_score.get(card.get("name"));
                    if(score==-5000 || (Float)card.get("vote")!=0){
                        card=(JSONObject) cards.get(String.valueOf(14));  //secondo in panchina
                        score=player_score.get(card.get("name"));
                        if(score==-5000 || (Float)card.get("vote")!=0){
                            return 0;//non avra rimpiazzo dalla panchina
                        }
                        place_of_bench=14;
                    }
                    else{
                        place_of_bench=13;
                    }

                } else if (i<=num_dif+num_mid) {
                    //centrocampista
                    JSONObject card=(JSONObject) cards.get(String.valueOf(15)); //primo in panchina
                    score=player_score.get(card.get("name"));
                    if(score==-5000 || (Float)card.get("vote")!=0){
                        card=(JSONObject) cards.get(String.valueOf(16));  //secondo in panchina
                        score=player_score.get(card.get("name"));
                        if(score==-5000 || (Float)card.get("vote")!=0){
                            return 0;//non avra rimpiazzo dalla panchina
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
                    score=player_score.get(card.get("name"));
                    if(score==-5000 || (Double)card.get("vote")!=0){
                        card=(JSONObject) cards.get(String.valueOf(18));  //secondo in panchina
                        score=player_score.get(card.get("name"));
                        if(score==-5000 || (Double)card.get("vote")!=0){
                            return 0;//non avra rimpiazzo dalla panchina
                        }
                        place_of_bench=18;
                    }
                    else{
                        place_of_bench=17;
                    }
                }

        JSONObject card=(JSONObject) cards.get(String.valueOf(place_of_bench));
        System.out.println("dalla panchina:"+card.get("name"));
        System.out.println("score dalla panchina:"+player_score.get(card.get("name")));

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
           card.put("vote", Float.valueOf(0));
       }

    }

    public static void calculate_card_score(int matchday) {

        Map<String,Float> player_score=new HashMap<String,Float>();  //per calcolo score di un team di un player

        //String url = "mongodb://localhost:27017";
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
                //System.out.println(player_name);

                Long credits= (Long) json_player.get("credits");  //prende credits player per poi modificarlo

                String team= (String) json_player.get("team");

                JSONObject playerObject = (JSONObject) json_player.get("statistics");
                JSONObject playermatch = (JSONObject) playerObject.get("matchday");
                JSONObject matchday_K = (JSONObject) playermatch.get("matchday" + matchday);
                JSONObject matchday_stat = (JSONObject) matchday_K.get("stats");

                //take all statistics for matchday of that player
                String cr= (String) matchday_stat.get("CR");
                String plus= (String) matchday_stat.get("Plus");
                String apps= (String) matchday_stat.get("Apps");
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

                float score;

                if(Integer.parseInt(mins)>14) {
                    score =   Float.parseFloat(plus) + Float.parseFloat(apps) + Float.parseFloat(starter)/2 + 3*Float.parseFloat(goals) + Float.parseFloat(shots)/2 + Float.parseFloat(on_tar_shots)
                            + Float.parseFloat(pen_goals)/3 + Float.parseFloat(successful_dribbles) + Float.parseFloat(assists) + Float.parseFloat(acc_pass)/100 + Float.parseFloat(key_pass)/2 - Float.parseFloat(fouls)/2
                            + Float.parseFloat(was_fouled)/2 + Float.parseFloat(yc) + Float.parseFloat(rc) + Float.parseFloat(rec_ball)/3 + Float.parseFloat(tackles)/5 + Float.parseFloat(clean_sheets)
                            + Float.parseFloat(woods) + Float.parseFloat(headed_goals)/10 + Float.parseFloat(freekick_goals)/3 - Float.parseFloat(big_chance_missed)/4 + Float.parseFloat(goal_min)/100 + Float.parseFloat(shot_con_rate)/100
                            + Float.parseFloat(total_dribbles)/4 +  Float.parseFloat(passes)/100 + Float.parseFloat(acc_pass_per)/100
                            + Float.parseFloat(big_chance_created)/4 + Float.parseFloat(cross)/4 + Float.parseFloat(acc_cross)
                            + Float.parseFloat(cross_no_corner) + Float.parseFloat(second_yc) - Float.parseFloat(err_leading_to_goal) + Float.parseFloat(og) + Float.parseFloat(interception)/2 + Float.parseFloat(won_duels)/3
                            - Float.parseFloat(lost_duels)/3 + Float.parseFloat(aer_duels_won)/3 - Float.parseFloat(aer_duels_lost)/3 + Float.parseFloat(saves)/4 - Float.parseFloat(goals_conceded)/3 + Float.parseFloat(pen_saves)*3
                            - Float.parseFloat(pen_goals_conceded)/2;
                }
                else{
                    score=-5000;  //per riconoscere che non ha giocato
                }

                //System.out.println(score);
                float mod_value=calculate_mod_value(score);
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

                player_score.put(player_name,score);



            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        //formation.calculate_team_score(player_score);  //TODO per calcolo score di un team di un player
        calulate_user_team_score(matchday,player_score);
    }

    private static float calculate_mod_value(float score) {  //in base allo score della giornata calcola di quanto deve essere modificato il valore del giocatore

        if(score<=0){
            return -1;
        }
        else{
            if(score<7){
                return 0;
            }
            else{
                return 1;
            }
        }
    }

    public static void retrieve_info_matchday(Integer matchday) {

        MongoClient mongoClient2 = MongoClients.create(global.MONGO_URI);

        // Access a Database
        MongoDatabase database2 = mongoClient2.getDatabase(global.DATABASE_NAME);

        // Access a Collection
        MongoCollection<Document> coll = database2.getCollection(global.CARDS_COLLECTION_NAME);

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("json_stats/matchday_stats/kickest_stats.json")) {   //C:\Users\matte\OneDrive\Desktop\LargeScale\jsonEdo\json_stats\matchday_stats\kickest_stats.json  //C:\Users\matte\DataMiningJupyter\fantacalcio_LSKickest_completo_portiere.json
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray playerList = (JSONArray) obj;
            //System.out.println(playerList);

            //Iterate over player array
            playerList.forEach(player -> parsePlayerObject((JSONObject) player, matchday, coll));

        } //catch (FileNotFoundException e) {
        //e.printStackTrace();
        //}
        catch (IOException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
    }

    private static void parsePlayerObject(JSONObject player, Integer matchday, MongoCollection<Document> coll) {

        //JSONObject playerObj = (JSONObject) player.get("Player");
        String name = (String) player.get("Player");
        //System.out.println(name);

        String team=(String) player.get("Team");

        team=convert_team(team);

        String name_puntato;

        if(name.charAt(1)!='.'){
            String[] words = name.split(" ");
            if(words.length>1){
                name_puntato=trasformation(name); //lo fa diventare puntato anche se si chiama Mario Rui (M. Rui)
            }
            else{
                name_puntato=name;
            }
        }
        else{
            name_puntato=name;
        }

        //Get player object within list
        JSONObject playerObject = (JSONObject) player.get("statistiche"); //qui prende da file kickest

        //Get player first name
        JSONObject matchday_ins = (JSONObject) playerObject.get("matchday" + matchday);
        matchday_ins.put("xG", 0);
        matchday_ins.put("xA", 0);
        matchday_ins.put("xGChain", 0);
        matchday_ins.put("xGBuildup", 0);

        JSONArray shots=new JSONArray();


        //System.out.println(matchday_ins);

        //take info from understat
        take_from_understat(matchday, matchday_ins, name_puntato);

        //take info from undershots
        take_from_undershots(matchday, shots, name_puntato);

        matchday_ins.put("shotsInfo", shots);

        //System.out.println("AFTER UNDERSTAT" + matchday_ins);
        //update collection

        Bson filter=Filters.and(eq("fullname", name),eq("team", team));  //AND per risolvere problema L.Pellegrini
        Bson update = Updates.set("statistics.matchday.matchday" + matchday+".stats", matchday_ins);
        UpdateOptions options = new UpdateOptions().upsert(true);
        System.out.println(coll.updateOne(filter, update, options));

    }



    public static void take_from_understat(Integer matchday, JSONObject matchday_ins, String name_player) {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("json_stats/matchday_stats/understat_file/statsUnderstatMatchday" + matchday + ".json")) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray playerList = (JSONArray) obj;

            JSONObject playerObj = (JSONObject) playerList.get(0); //perchè questi file sono array di un elemento, al cui interno poi ci sono altri array, infatti ha una quadra in cima e una in fondo

            JSONArray pla = (JSONArray) playerObj.get("stats"); //ok, prende tutte e 10 le partite


            //Iterate over player array
            pla.forEach(match_understat -> parseUnderstatObject((JSONObject) match_understat, matchday, matchday_ins, name_player));


        } //catch (FileNotFoundException e) {
        //e.printStackTrace();
        //}
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private static void parseUnderstatObject(JSONObject match_understat, Integer matchday, JSONObject matchday_ins, String name) {

        int match=0; //se ha fatto match deve smettere di iterare

        //System.out.println(player_understat);

        //Get player object within list
        JSONObject playerObject1 = (JSONObject) match_understat.get("a");
        JSONObject playerObject2 = (JSONObject) match_understat.get("h");

        //System.out.println(playerObject);

        //Get player first name

        String key = playerObject1.keySet().toString();
        //System.out.println(key);

        key = key.substring(1, key.length() - 1);

        String key2 = playerObject2.keySet().toString();
        //System.out.println(key2);

        key2 = key2.substring(1, key2.length() - 1);


        String[] keys1 = key.split(", ");
        String[] keys2= key2.split(", ");


        for (int i = 0; i < keys1.length; i++) {

            //System.out.println(keys[i]);

            JSONObject elem = (JSONObject) playerObject1.get(keys1[i]);
            //System.out.println(elem);

            String player = (String) elem.get("player");  //DEVI TRASFORMARE QUESTO Lorenzo De Silvestri, in L.De Silvestri
            //System.out.println(player);

            String[] words = name.split(" ");

            if(words.length>1) {
                player = trasformation(player);
            }
            else{
                //System.out.println(name);
                String[] words2 = player.split(" ");
                if(words2.length>1) {
                    if (name.equals(words2[0])) {
                        System.out.println(words2[0]);
                        System.out.println(words2[1]);
                        player = words2[0];
                    } else{
                        //System.out.println(words2[1]);
                        player = words2[1];
                    }
                }
            }
            //System.out.println(player);

            // System.out.println(player.length());
            //System.out.println(name.length());

            if (name.equals(player)) {

                //System.out.println("MATCHHHHHHH----------------------------------------------------");

                String xG = (String) elem.get("xG");
                //System.out.println(xG);

                String xA = (String) elem.get("xA");
                //System.out.println(xA);

                String xGChain = (String) elem.get("xGChain");
                //System.out.println(xGChain);

                String xGBuildup = (String) elem.get("xGBuildup");
                //System.out.println(xGBuildup);

                matchday_ins.put("xG", xG);
                matchday_ins.put("xA", xA);
                matchday_ins.put("xGChain", xGChain);
                matchday_ins.put("xGBuildup", xGBuildup);

                match=1;  //DOVREI FARE IN MODO CHE SMETTA DI ITERARE SE L' HA TROVATO

                //System.out.println(matchday_ins);

                //break;
            }
            if(match==1){
                break;
            }

        }
        //adesso per quelli che hanno etichetta h
        if(match==0) {
            for (int i = 0; i < keys2.length; i++) {

                //System.out.println(keys[i]);

                JSONObject elem = (JSONObject) playerObject2.get(keys2[i]);
                //System.out.println(elem);

                String player = (String) elem.get("player");  //DEVI TRASFORMARE QUESTO Lorenzo De Silvestri, in L.De Silvestri
                //System.out.println(player);

                String[] words = name.split(" ");

                if(words.length>1) {
                    player = trasformation(player);
                }
                else{
                    //System.out.println(name);
                    String[] words2 = player.split(" ");
                    if(words2.length>1) {
                        if (name.equals(words2[0])) {
                            //System.out.println(words2[0]);
                            //System.out.println(words2[1]);
                            player = words2[0];
                        } else{
                            //System.out.println(words2[1]);
                            player = words2[1];
                        }
                    }
                }

                if (name.equals(player)) {

                    //System.out.println("MATCHHHHHHH----------------------------------------------------");

                    String xG = (String) elem.get("xG");
                    //System.out.println(xG);

                    String xA = (String) elem.get("xA");
                    //System.out.println(xA);

                    String xGChain = (String) elem.get("xGChain");
                    //System.out.println(xGChain);

                    String xGBuildup = (String) elem.get("xGBuildup");
                    //System.out.println(xGBuildup);

                    matchday_ins.put("xG", xG);
                    matchday_ins.put("xA", xA);
                    matchday_ins.put("xGChain", xGChain);
                    matchday_ins.put("xGBuildup", xGBuildup);

                    //match=1;  //DOVREI FARE IN MODO CHE SMETTA DI ITERARE SE L' HA TROVATO

                    //System.out.println(matchday_ins);

                    //break;
                }
                if(match==1){
                    break;
                }
            }
        }



        //JSONObject elem=(JSONObject) playerObject.get(key);

        //System.out.println(elem);

    }

    private static String trasformation(String player) {

        String[] words = player.split(" ");
        //System.out.println(words[0]);
        if (words.length > 1) {
            if(words.length==2)
                player = words[0].charAt(0) + ". " + words[1];
            else
                player=words[0].charAt(0) + ". " + words[1]+" "+words[2];
        } else {
            player = words[0];
        }
        //System.out.println("PLAYER TRASFORM:" + player);
        return player;
    }


    private static String convert_team(String team) {

        switch(team){
            case "ATA":
                team="Atalanta";
                break;
            case "BOL":
                team="Bologna";
                break;
            case "CAG":
                team="Cagliari";
                break;
            case "EMP":
                team="Empoli";
                break;
            case "FIO":
                team="Fiorentina";
                break;
            case "GEN":
                team="Genoa";
                break;
            case "INT":
                team="Inter";
                break;
            case "JUV":
                team="Juventus";
                break;
            case "LAZ":
                team="Lazio";
                break;
            case "MIL":
                team="AC Milan";
                break;
            case "NAP":
                team="Napoli";
                break;
            case "ROM":
                team="AS Roma";
                break;
            case "SAL":
                team="Salernitana";
                break;
            case "SAM":
                team="Sampdoria";
                break;
            case "SAS":
                team="Sassuolo";
                break;
            case "SPE":
                team="Spezia";
                break;
            case "TOR":
                team="Torino";
                break;
            case "UDI":
                team="Udinese";
                break;
            case "VEN":
                team="Venezia";
                break;
            case "VER":
                team="Verona";
                break;


        }
        return team;
    }



    private static void take_from_undershots(Integer matchday, JSONArray shots, String name_player) {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("json_stats/matchday_stats/shots_file/shotsUnderstatMatchday" + matchday + ".json")) {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray playerList = (JSONArray) obj;

            JSONObject playerObj = (JSONObject) playerList.get(0);

            JSONArray pla = (JSONArray) playerObj.get("shots");
            //System.out.println(playerObj);

            //Iterate over player array
            pla.forEach(player_undershot -> parseUndershotObject((JSONObject) player_undershot, matchday, shots, name_player));


        } //catch (FileNotFoundException e) {
        //e.printStackTrace();
        //}
        catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private static void parseUndershotObject(JSONObject player_understat, Integer matchday, JSONArray shots, String name) {


        //Get player object within list
        JSONArray playerObject = (JSONArray) player_understat.get("a");
        JSONArray playerObject2 = (JSONArray) player_understat.get("h");

        playerObject.forEach(it ->{
            JSONObject play=(JSONObject) it;
            //System.out.println(play);
            String player= (String) play.get("player");
            //System.out.println(player);

            player = trasformation(player);
            //System.out.println(name);

            if (name.equals(player)) {

                //System.out.println("MATCHHHHHHHSHOTS----------------------------------------------------");


                String X = (String) play.get("X");
                //System.out.println(X);

                String Y = (String) play.get("Y");
                //System.out.println(Y);

                String xG = (String) play.get("xG");
                //System.out.println(xG);

                String min = (String) play.get("minute");
                //System.out.println(min);

                String result = (String) play.get("result");
                //System.out.println(result);

                String situation = (String) play.get("situation");
                //System.out.println(situation);

                String shotType = (String) play.get("shotType");
                //System.out.println(shotType);

                String player_assist = (String) play.get("player_assisted");
                //System.out.println(player_assist);

                String Action = (String) play.get("lastAction");
                //System.out.println(Action);

                /*

                 */
                //JSONArray matchday_shots=new JSONArray();

                JSONObject matchday_ins_shots = new JSONObject();

                matchday_ins_shots.put("X", X);
                matchday_ins_shots.put("Y", Y);
                matchday_ins_shots.put("xG", xG);
                matchday_ins_shots.put("min", min);
                matchday_ins_shots.put("results",result);
                matchday_ins_shots.put("situation", situation);
                matchday_ins_shots.put("shotType", shotType);

                JSONObject matchday_ins_shots_assist = new JSONObject();
                matchday_ins_shots_assist.put("player", player_assist);
                matchday_ins_shots_assist.put("action", Action);
                matchday_ins_shots.put("assist", matchday_ins_shots_assist);

                shots.add(matchday_ins_shots);

                //System.out.println(shots);

                //matchday_ins.put("shotsInfo", matchday_shots);

                //System.out.println(matchday_ins);

            }
        });

        playerObject2.forEach(it ->{
            JSONObject play=(JSONObject) it;
            //System.out.println(play);
            String player= (String) play.get("player");
            //System.out.println(player);

            player = trasformation(player);
            /*if(name.equals("J. Veretout")) {
                System.out.println(name);
                System.out.println(player);
            }*/

            if (name.equals(player)) {

                //System.out.println("MATCHHHHHHHSHOTS----------------------------------------------------");


                String X = (String) play.get("X");
                //System.out.println(X);

                String Y = (String) play.get("Y");
                //System.out.println(Y);

                String xG = (String) play.get("xG");
                //System.out.println(xG);

                String min = (String) play.get("minute");
                //System.out.println(min);

                String result = (String) play.get("result");
                //System.out.println(result);

                String situation = (String) play.get("situation");
                //System.out.println(situation);

                String shotType = (String) play.get("shotType");
                //System.out.println(shotType);

                String player_assist = (String) play.get("player_assisted");
                //System.out.println(player_assist);

                String Action = (String) play.get("lastAction");
                //System.out.println(Action);

                /*

                 */
                //JSONArray matchday_shots=new JSONArray();

                JSONObject matchday_ins_shots = new JSONObject();

                matchday_ins_shots.put("X", X);
                matchday_ins_shots.put("Y", Y);
                matchday_ins_shots.put("xG", xG);
                matchday_ins_shots.put("min", min);
                matchday_ins_shots.put("results",result);
                matchday_ins_shots.put("situation", situation);
                matchday_ins_shots.put("shotType", shotType);

                JSONObject matchday_ins_shots_assist = new JSONObject();
                matchday_ins_shots_assist.put("player", player_assist);
                matchday_ins_shots_assist.put("action", Action);
                matchday_ins_shots.put("assist", matchday_ins_shots_assist);

                shots.add(matchday_ins_shots);

                //System.out.println(shots);

                //matchday_ins.put("shotsInfo", matchday_shots);

                //System.out.println(matchday_ins);

            }
        });

    }
}
