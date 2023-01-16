package it.unipi.dii.ingin.lsmsd.fantamanager.admin;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
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

import static com.mongodb.client.model.Filters.eq;

public class retrieve_matchday {
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
        matchday_ins.put("xG", "0");
        matchday_ins.put("xA", "0");
        matchday_ins.put("xGChain", "0");
        matchday_ins.put("xGBuildup", "0");

        JSONArray shots=new JSONArray();


        //System.out.println(matchday_ins);

        //take info from understat
        take_from_understat(matchday, matchday_ins, name_puntato);

        //take info from undershots
        take_from_undershots(matchday, shots, name_puntato);

        matchday_ins.put("shotsInfo", shots);

        //System.out.println("AFTER UNDERSTAT" + matchday_ins);
        //update collection

        Bson filter= Filters.and(eq("fullname", name),eq("team", team));  //AND per risolvere problema L.Pellegrini
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
