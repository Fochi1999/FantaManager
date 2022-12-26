package it.unipi.dii.ingin.lsmsd.fantamanager.collection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class collection {

            int id_user;

            public ArrayList<player_collection> player_card_collection;


            static JedisPool pool;

            public collection(int id_user,ArrayList<player_collection> player_card_collection){
                    this.id_user=id_user;
                    this.player_card_collection=player_card_collection;
            }

            public static void apertura_pool(){
                pool=new JedisPool("localhost",6379);
            }

            public static String crea_chiave_load(int user_id) {  //solo per fare il retrieve delle info dell-user in questione
                //return "user:"+this.userId+"*";   //dovrebbe essere cosi dopo la creazione di user
                return "user_id:"+user_id+"*";
            }

            /*public static JSONArray load_collection(int user_id){

                //List<Map<String, String>> values = new ArrayList<>();
                JSONArray players=new JSONArray();

                //Gson gson=new Gson();
                //ShoppingCart cart=null;
                String key_load=crea_chiave_load(user_id);  //qui dovremmo inserire this.user_id, oppure togliere il parametro e farlo come commentato sopra
                System.out.println(key_load);
                //JedisPool pool=new JedisPool("localhost",6379);
                try(Jedis jedis=pool.getResource()){

                    Set<String> set_keys=jedis.keys(key_load);

                    System.out.println(set_keys);

                    Iterator<String> it = set_keys.iterator();
                    while (it.hasNext()) {
                        String key= it.next();
                        String value=jedis.get(key);

                        System.out.println(key+" "+value);

                        int id=retrieve_id_player(key);

                        //JSONObject player=new JSONObject();
                        int present=0;
                        for(int i=0;i<players.size();i++) {
                            JSONObject player=(JSONObject) players.get(i);

                            if((Integer) player.get("id")==id){
                                present=1;
                                player.put(retrieve_key_info(key),value);
                                break;
                            }
                        }
                        if(present==0){
                            JSONObject player1=new JSONObject();
                            player1.put("id",id);
                            player1.put(retrieve_key_info(key),value);
                            players.add(player1);
                        }


                    }
                }
                System.out.println(players);
                return players;
            }*/

            public static ArrayList<player_collection> load_collection(int user_id) {

                //List<Map<String, String>> values = new ArrayList<>();
                //JSONArray players = new JSONArray();
                ArrayList<player_collection> players=new ArrayList<>();

                //Gson gson=new Gson();
                //ShoppingCart cart=null;
                String key_load = crea_chiave_load(user_id);  //qui dovremmo inserire this.user_id, oppure togliere il parametro e farlo come commentato sopra
                System.out.println(key_load);
                JedisPool pool=new JedisPool("localhost",6379);
                try (Jedis jedis = pool.getResource()) {

                    Set<String> set_keys = jedis.keys(key_load);

                    System.out.println(set_keys);

                    Iterator<String> it = set_keys.iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        String value = jedis.get(key);

                        System.out.println(key + " " + value);

                        int id = retrieve_id_player(key);

                        //JSONObject player=new JSONObject();
                        int present = 0;
                        for (int i = 0; i < players.size(); i++) {
                            //JSONObject player = (JSONObject) players.get(i);
                            player_collection player= players.get(i);

                            if (player.get_id() == id) {
                                present = 1;
                                //player.put(retrieve_key_info(key), value);
                                retrieve_key_info(key,player,value);
                                break;
                            }
                        }
                        if (present == 0) {
                            /*JSONObject player1 = new JSONObject();
                            player1.put("id", id);
                            player1.put(retrieve_key_info(key), value);
                            players.add(player1);*/
                            player_collection player=new player_collection(id,"",0,"","");
                            retrieve_key_info(key,player,value);
                            players.add(player);
                        }


                    }
                }
                System.out.println(players);
                return players;
            }

            private static void retrieve_key_info(String key, player_collection player,String value) {

                String[] words=key.split(":");
                String attribute=words[4];

                System.out.println(player);

                player.set_key_info(attribute,value);

                System.out.println(player);

            }

            private static int retrieve_id_player(String key) {

                String[] words=key.split(":");
                return Integer.parseInt(words[3]);
            }

            public static void closePool(){
                pool.close();
            }


}
