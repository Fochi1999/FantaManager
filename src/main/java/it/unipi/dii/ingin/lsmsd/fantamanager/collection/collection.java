package it.unipi.dii.ingin.lsmsd.fantamanager.collection;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import org.bson.Document;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.*;

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

            public static String crea_chiave_load(String user_id) {  //solo per fare il retrieve delle info dell-user in questione
                //return "user:"+this.userId+"*";   //dovrebbe essere cosi dopo la creazione di user
                return "user_id:"+global.id_user+"*";
            }


            public static ArrayList<player_collection> load_collection(String user_id) {

                //List<Map<String, String>> values = new ArrayList<>();
                //JSONArray players = new JSONArray();
                ArrayList<player_collection> players=new ArrayList<>();

                //Gson gson=new Gson();
                //ShoppingCart cart=null;
                String key_load = crea_chiave_load(user_id);  //qui dovremmo inserire this.user_id, oppure togliere il parametro e farlo come commentato sopra
                //System.out.println(key_load);
                JedisPool pool=new JedisPool("localhost",6379);
                try (Jedis jedis = pool.getResource()) {

                    Set<String> set_keys = jedis.keys(key_load);

                    System.out.println(set_keys);

                    Iterator<String> it = set_keys.iterator();
                    while (it.hasNext()) {
                        String key = it.next();
                        String value = jedis.get(key);

                        //System.out.println(key + " " + value);

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
                //System.out.println(players);
                return players;
            }

            private static void retrieve_key_info(String key, player_collection player,String value) {

                String[] words=key.split(":");
                String attribute=words[4];

                //System.out.println(player);

                player.set_key_info(attribute,value);

                //System.out.println(player);

            }

            private static int retrieve_id_player(String key) {

                String[] words=key.split(":");
                return Integer.parseInt(words[3]);
            }

            public static void closePool(){
                pool.close();
            }

            public static void delete_player_from_collection(int player_id){  //dalla  mia
                    apertura_pool();

                    System.out.println("player deleted:"+player_id);

                    int user_id=1;

                    try (Jedis jedis = pool.getResource()) {
                        jedis.expire("user_id:"+global.id_user+":player_id:"+player_id+":name",0);
                        jedis.expire("user_id:"+global.id_user+":player_id:"+player_id+":quantity",0);
                        jedis.expire("user_id:"+global.id_user+":player_id:"+player_id+":team",0);
                        jedis.expire("user_id:"+global.id_user+":player_id:"+player_id+":position",0);
                    }
                    closePool();
            }

    public static void add_player_to_collection(player_collection player, String retrieve_user) {
            apertura_pool();

            int user_id=1; //dovrebbe essere la variabile globale dell' id dell' utente
            System.out.println("player added to"+retrieve_user+":+player.name");
            try (Jedis jedis = pool.getResource()) {

                jedis.set("user_id:"+retrieve_user+":player_id:"+player.player_id+":name",player.name);
                jedis.set("user_id:"+retrieve_user+":player_id:"+player.player_id+":quantity", String.valueOf(player.quantity));
                jedis.set("user_id:"+retrieve_user+":player_id:"+player.player_id+":team",player.team);
                jedis.set("user_id:"+retrieve_user+":player_id:"+player.player_id+":position",player.position);


            }

            closePool();
    }

    public static boolean presence_player(player_collection player) {

                boolean result;
                apertura_pool();

                String key="user_id:"+global.id_user+":player_id:"+player.player_id+":name";
                try (Jedis jedis = pool.getResource()) {
                    String value=jedis.get(key);
                    if(value==null){
                            result=false;
                    }
                    else if(value.equals(player.name)){  //se non è nullo qui dovrebbe entrarci, quindi forse bastava un else
                        result=true;
                    }
                    else{
                        result=false;
                    }
                }

                closePool();
                return result;
    }

    public void change_team_of_player(int player_id){   //funzione che potrebbe servire all' admin per cambiare il team di un certo giocatore su tutto redis (tipo se va via a gennaio)

                    apertura_pool();
                    System.out.println("Which team does he play for?");
                    Scanner scanner = new Scanner(System.in);

                    String team=scanner.toString();

                    String key_team="user_id:*:player_id:"+player_id+":team";
                    try (Jedis jedis = pool.getResource()) {
                        Set<String> set_keys = jedis.keys(key_team);
                        Iterator<String> it = set_keys.iterator();
                        while (it.hasNext()) {
                            String key = it.next();

                            jedis.set(key,team);
                        }
                    }
                    closePool();
            }

            public void change_position_of_player(int player_id){ //funzione che potrebbe servire all' admin per cambiare il ruolo di un certo giocatore su tutto redis

                apertura_pool();

                System.out.println("Which position does he play?");
                Scanner scanner = new Scanner(System.in);

                String position=scanner.toString();

                String key_pos="user_id:*:player_id:"+player_id+":position";
                try (Jedis jedis = pool.getResource()) {
                    Set<String> set_keys = jedis.keys(key_pos);
                    Iterator<String> it = set_keys.iterator();
                    while (it.hasNext()) {
                        String key = it.next();

                        jedis.set(key,position);
                    }
                }
                closePool();
            }

            /*public static void add_player_to_collection(player_collection player){   //add to my collection

                    apertura_pool();

                    int user_id=1; //dovrebbe essere la variabile globale dell' id dell' utente
                    System.out.println("player accepted:"+player.name);
                    try (Jedis jedis = pool.getResource()) {

                        jedis.set("user_id:"+global.id_user+":player_id:"+player.player_id+":name",player.name);
                        jedis.set("user_id:"+global.id_user+":player_id:"+player.player_id+":quantity", String.valueOf(player.quantity));
                        jedis.set("user_id:"+global.id_user+":player_id:"+player.player_id+":team",player.team);
                        jedis.set("user_id:"+global.id_user+":player_id:"+player.player_id+":position",player.position);


                    }

                    closePool();
            }*/


            //prossime due funzioni solo per creazione collection casuale per utente user_id:1
            static String generate_string()
            {

                int leftLimit = 97; // letter 'a'
                int rightLimit = 122; // letter 'z'
                int targetStringLength = 10;
                Random random = new Random();

                String generatedString = random.ints(leftLimit, rightLimit + 1)
                        .limit(targetStringLength)
                        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                        .toString();

                return generatedString;

            }
                public static void create_collection(){
                    apertura_pool();
                    //String key_cart=this.crea_chiave();
                    JedisPool pool=new JedisPool("localhost",6379);
                    Random random=new Random();

                    String[] position={"P","D","M","A"};
                    for(int i=0;i<25;i++){

                        try(Jedis jedis=pool.getResource()){

                            int player_id=random.nextInt(50);

                            jedis.set("user_id:"+global.id_user+":player_id:"+player_id+":name",generate_string());
                            jedis.set("user_id:"+global.id_user+":player_id:"+player_id+":quantity", String.valueOf(random.nextInt(10)));
                            jedis.set("user_id:"+global.id_user+":player_id:"+player_id+":team",generate_string());
                            jedis.set("user_id:"+global.id_user+":player_id:"+player_id+":position",position[random.nextInt(4)]);

                        }
                    }

                    closePool();
                }


}
