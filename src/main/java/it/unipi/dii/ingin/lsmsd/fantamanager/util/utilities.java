package it.unipi.dii.ingin.lsmsd.fantamanager.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class utilities {

	public static String regionList[] = {"Abruzzo","Basilicata","Calabria","Campania","Emilia-Romagna",
			"Friuli-Venezia Giulia","Lazio","Liguria","Lombardy","Marche","Molise","Piedmont","Apulia",
			"Sardinia","Sicily","Trentino-South Tyrol","Tuscany", "Umbria","Aosta Valley","Veneto"};	
	
	public static boolean has_duplicates(long[] arr){
        HashMap<Long, Integer> map = new HashMap<>();

        for (long element : arr)
        {
            if(map.get(element) == null)
            {
                map.put(element, 1);
            }
            else
            {
                map.put(element, map.get(element)+1);
            }
        }

        Set<Map.Entry<Long, Integer>> entrySet = map.entrySet();

        for (Map.Entry<Long, Integer> entry : entrySet)
        {
            if(entry.getValue() > 1)
            {
                return true;
            }
        }
        return false;

    }
	
	//function for update a specific matchday
	public static void update_matchday(int matchday) {
		
		if(matchday < -1 || matchday > 38) { //error handling
			System.out.println("Matchday value out of range!");
			return;
		}
		
		JedisPool pool = new JedisPool(global.REDIS_URI, global.REDIS_PORT);
		
		//set
        try(Jedis jedis=pool.getResource()){
        	
        	if(matchday == -1) {	//initialize
        		int array[] = new int[38];
        		jedis.set("admin:updated_matchdays", Arrays.toString(array));
        		System.out.println("'updated_matchdays' matrix initialized");
        	}
        	else { //updating the array
        		global.updated_matchdays[matchday-1] = 1;    //se matchday 1, va messo ad indice 0 del vettore
        		jedis.set("admin:updated_matchdays", Arrays.toString(global.updated_matchdays));
        		System.out.println("Setted value of 'updated_matchdays' matrix on position " + matchday);
        	}
        }
        catch(Exception e){
        	System.out.println("Error while setting the value of 'updated_matchdays' matrix");
        }
        pool.close();

		set_next_matchday_redis(matchday+1); //se ho calcolato matchday=1, allora la prossima partita è la 2
	}



	//function that retrieve the array of updated matchdays
	public static int[] get_updated_matchdays() {
		JedisPool pool = new JedisPool(global.REDIS_URI, global.REDIS_PORT);
		int[] array = new int[38];
		//set
        try(Jedis jedis=pool.getResource()){
        	String value = jedis.get("admin:updated_matchdays");	//retrieving
        	String words[] = value.split(", ");
        	
        	for(int i=0;i<words.length;i++) {	//copying into array
        		if(i==0) {
        			array[i] = (words[i].charAt(1)-'0');
        		}
        		else if(i==37){
        			array[i] = (words[i].charAt(0)-'0');
        		}
        		else {
        			array[i] = Integer.parseInt(words[i]);
        		}
        	}
        	System.out.println("Updated matchdays retrieved:"+value);
        }
        catch(Exception e){
        	System.out.println("Error while getting updated matchdays matrix");
        }
        pool.close();
		get_next_matchday();
        return array;
	}

	private static void get_next_matchday() {   //prende da redis il valore della prossima giornata del campionato e lo inserisce in global.next_matchday
		JedisPool pool = new JedisPool(global.REDIS_URI, global.REDIS_PORT);
		int next_matchday;
		try(Jedis jedis=pool.getResource()){
			String value=jedis.get("admin:next_matchday");
			next_matchday= Integer.parseInt(value);
		}
		pool.close();
		global.next_matchday=next_matchday;
		System.out.println("next match:"+global.next_matchday);
	}

	private static void get_last_match_updated(int[] value) {  //restituisce ultima partita calcolata, quindi dal vettore salvato su redis

			int last_match_updated=0;

			for(int i=0;i< value.length;i++){
					if(value[i]==1){
						last_match_updated=i+1;
					}
			}

	}

	public static int getRandomInt(int min, int max){
		return (int) ((Math.random() * (max - min)) + min);
	}

	public static void set_next_matchday_redis(int next_matchday) {
		JedisPool pool = new JedisPool(global.REDIS_URI, global.REDIS_PORT);

		try(Jedis jedis=pool.getResource()){
			jedis.set("admin:next_matchday", String.valueOf(next_matchday));
		}
		pool.close();
	}

}
