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
	
	public static boolean has_duplicates(int[] arr){
        HashMap<Integer, Integer> map = new HashMap<>();

        for (int element : arr)
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

        Set<Map.Entry<Integer, Integer>> entrySet = map.entrySet();

        for (Map.Entry<Integer, Integer> entry : entrySet)
        {
            if(entry.getValue() > 1)
            {
                System.out.println("Duplicate Element, id: "+entry.getKey()+" - found "+entry.getValue()+" times.");
                return true;
            }
        }
        return false;

    }
	
	//function for update a specific matchday
	public static void update_matchday(int matchday) {
		
		if(matchday < 0 || matchday > 38) { //error handling
			System.out.println("Matchday value out of range!");
			return;
		}
		
		JedisPool pool = new JedisPool("localhost", 6379);
		
		//updating the array
		global.updated_matchdays[matchday] = 1;
		
        //set
        try(Jedis jedis=pool.getResource()){
        	jedis.set("admin:updated_matchdays", Arrays.toString(global.updated_matchdays));
        	System.out.println("Setted value of 'updated_matchdays' matrix on position " + matchday);
        }
        catch(Exception e){
        	System.out.println("Error while setting the value of 'updated_matchdays' matrix");
        }
        pool.close();
	}
	
	//function that retrieve the array of updated matchdays
	public static int[] get_updated_matchdays() {
		JedisPool pool = new JedisPool("localhost", 6379);
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
        	System.out.println("Updated matchdays retrieved");
        }
        catch(Exception e){
        	System.out.println("Error while getting updated matchdays matrix");
        }
        pool.close();
        return array;
	}
}
