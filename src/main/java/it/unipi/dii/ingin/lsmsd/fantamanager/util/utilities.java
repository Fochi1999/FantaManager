package it.unipi.dii.ingin.lsmsd.fantamanager.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
                System.out.println("Duplicate Element : "+entry.getKey()+" - found "+entry.getValue()+" times.");
                return true;
            }
        }
        return false;

    }
}
