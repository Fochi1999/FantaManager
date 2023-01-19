package it.unipi.dii.ingin.lsmsd.fantamanager.collection;

import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;

public class collectionRedisDriver {

            int id_user;

            public ArrayList<card_collection> player_card_collection;


            static JedisPool pool;

            public collectionRedisDriver(int id_user, ArrayList<card_collection> player_card_collection){
                    this.id_user=id_user;
                    this.player_card_collection=player_card_collection;
            }

            public static void apertura_pool(){
                pool=new JedisPool(global.REDIS_URI,global.REDIS_PORT);
            }

            public static String crea_chiave_load(String user_id) {  //solo per fare il retrieve delle info dell-user in questione
                //return "user:"+this.userId+"*";   //dovrebbe essere cosi dopo la creazione di user
                return "user_id:"+user_id+":card_id:";
            }


            public static ArrayList<card_collection> load_collection(String user_id) {

                ArrayList<card_collection> cards=new ArrayList<>();

                //String key_load = crea_chiave_load(user_id);
                String key_load="user_id:"+user_id+":card_id:";
                
            	apertura_pool();
                try (Jedis jedis = pool.getResource()) {
                	
                    int i = 0;
                	while (i<=global.max_card_id) {	
                        String key_name = key_load+i+":name";
                        String value_name = jedis.get(key_name);
                        
                        if (value_name != null) {
                        	String key_pos = key_load+i+":position";
                            String value_pos = jedis.get(key_pos);
                            
                            
                            String key_qnt = key_load+i+":quantity";
                            String value_qnt = jedis.get(key_qnt);
                            
                            String key_team = key_load+i+":team";
                            String value_team = jedis.get(key_team);
                            
                        	card_collection card=new card_collection(i,value_name,Integer.parseInt(value_qnt),value_team,value_pos);
                            cards.add(card);
                        }
                        i++;
                    }
                }
                closePool();
                return cards;
            }
            
/* 
            private static void retrieve_key_info(String key, card_collection card,String value) { UNUSED

                String[] words=key.split(":");
                String attribute=words[4];

                //System.out.println(player);

                card.set_key_info(attribute,value);

                //System.out.println(player);

            }

            private static int retrieve_id_card(String key) { UNUSED

                String[] words=key.split(":");
                return Integer.parseInt(words[3]);
            }
*/
            
            public static void closePool(){
                pool.close();
            }

            public static void delete_card_from_collection(card_collection card){  //dalla  mia, viene sempre eliminato dall apropria, quindi non l' ho generalizzata


                    System.out.println("Card deleted: "+ card);

                //if(presence_card(card,global.id_user)) {
                    apertura_pool();
                    //devo aumentare solo la quantity
                    String key = "user_id:" + global.id_user + ":card_id:" + card.card_id + ":quantity";
                    try (Jedis jedis = pool.getResource()) {
                        String value = jedis.get(key);
                        Integer quantity = Integer.parseInt(value);
                        if (quantity > 1) {
                            jedis.set("user_id:" + global.id_user + ":card_id:" + card.card_id + ":quantity", String.valueOf(quantity - 1)); 
                        }
                        else{
                            jedis.del("user_id:" + global.id_user + ":card_id:" + card.card_id + ":name");
                            jedis.del("user_id:" + global.id_user + ":card_id:" + card.card_id + ":quantity");
                            jedis.del("user_id:" + global.id_user + ":card_id:" + card.card_id + ":team");
                            jedis.del("user_id:" + global.id_user + ":card_id:" + card.card_id + ":position");
                        }
                    }
                //}
                    closePool();
            }

            
            public static void delete_card_from_collection(String card_id){  //Emmanuel

            	//if(presence_card(card,global.id_user)) {
                apertura_pool();
                //devo aumentare solo la quantity
                String key = "user_id:" + global.id_user + ":card_id:" + card_id + ":quantity";
                try (Jedis jedis = pool.getResource()) {
                    String value = jedis.get(key);
                    Integer quantity = Integer.parseInt(value);
                    if (quantity > 1) {
                        jedis.set("user_id:" + global.id_user + ":card_id:" + card_id + ":quantity", String.valueOf(quantity - 1)); 
                    }
                    else{
                        jedis.del("user_id:" + global.id_user + ":card_id:" + card_id + ":name");
                        jedis.del("user_id:" + global.id_user + ":card_id:" + card_id + ":quantity");
                        jedis.del("user_id:" + global.id_user + ":card_id:" + card_id + ":team");
                        jedis.del("user_id:" + global.id_user + ":card_id:" + card_id + ":position");
                    }
                }
          //  }
                closePool();
        }
            
            public static void add_card_to_collection(card_collection card, String retrieve_user) {
                    
            	if(presence_card(card,retrieve_user)){
                        apertura_pool();
                        //devo aumentare solo la quantity
                        String key="user_id:"+retrieve_user+":card_id:"+card.card_id+":quantity";
                        try (Jedis jedis = pool.getResource()) {
                            String value=jedis.get(key);
                            Integer quantity=Integer.parseInt(value);
                            jedis.set("user_id:" + retrieve_user + ":card_id:" + card.card_id + ":quantity", String.valueOf(quantity+1)); 
                        }
                    }
                    else {
                        apertura_pool();
                        //devo aggiungerlo per intero
                        try (Jedis jedis = pool.getResource()) {

                            jedis.set("user_id:" + retrieve_user + ":card_id:" + card.card_id + ":name", card.name);
                            jedis.set("user_id:" + retrieve_user + ":card_id:" + card.card_id + ":quantity", String.valueOf(card.quantity));
                            jedis.set("user_id:" + retrieve_user + ":card_id:" + card.card_id + ":team", card.team);
                            jedis.set("user_id:" + retrieve_user + ":card_id:" + card.card_id + ":position", card.position);


                        }
                    }
                    closePool();
                    System.out.println("Card added to "+retrieve_user+":"+card.name);
            }

            public static boolean presence_card(card_collection card, String retrieve_user) {

                        boolean result;
                        apertura_pool();

                        String key="user_id:"+retrieve_user+":card_id:"+card.card_id+":name";
                        try (Jedis jedis = pool.getResource()) {
                            String value=jedis.get(key);
                            if(value==null){
                                    result=false;
                            }
                            else if(value.equals(card.name)){  //se non è nullo qui dovrebbe entrarci, quindi forse bastava un else
                                result=true;
                            }
                            else{
                                result=false;
                            }
                        }

                        closePool();
                        return result;
            }

            public void change_team_of_card(int card_id){   //funzione che potrebbe servire all' admin per cambiare il team di un certo giocatore su tutto redis (tipo se va via a gennaio)

                    apertura_pool();
                    System.out.println("Which team does he play for?");
                    Scanner scanner = new Scanner(System.in);

                    String team=scanner.toString();

                    String key_team="user_id:*:card_id:"+card_id+":team";
                    try (Jedis jedis = pool.getResource()) {
                        Set<String> set_keys = jedis.keys(key_team);
                        Iterator<String> it = set_keys.iterator();
                        while (it.hasNext()) {
                            String key = it.next();

                            jedis.set(key,team);
                        }
                    }
                    closePool();
                    scanner.close();
            }

            public void change_position_of_card(int card_id){ //funzione che potrebbe servire all' admin per cambiare il ruolo di un certo giocatore su tutto redis

                apertura_pool();

                System.out.println("Which position does he play?");
                Scanner scanner = new Scanner(System.in);

                String position=scanner.toString();

                String key_pos="user_id:*:card_id:"+card_id+":position";
                try (Jedis jedis = pool.getResource()) {
                    Set<String> set_keys = jedis.keys(key_pos);
                    Iterator<String> it = set_keys.iterator();
                    while (it.hasNext()) {
                        String key = it.next();

                        jedis.set(key,position);
                    }
                }
                closePool();
                scanner.close();
            }
            
        public static void delete_user_card_collection(String user_id) {
        	
        	String key_load = crea_chiave_load(user_id);  
            apertura_pool();
            
            try (Jedis jedis = pool.getResource()) {
            
                int i = 0;
            	while (i<=global.max_card_id) {	
                    String key_name = key_load+i+":name";
                    System.out.println("Deleting "+key_name);
                    jedis.del(key_name);
                    
                    String key_pos = key_load+i+":position";
                    jedis.del(key_pos);
                        
                    String key_qnt = key_load+i+":quantity";
                    jedis.del(key_qnt);
                        
                    String key_team = key_load+i+":team";
                    jedis.del(key_team);
              
                    i++;
                }
            }
            closePool();
        }
}
