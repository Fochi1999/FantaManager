package it.unipi.dii.ingin.lsmsd.fantamanager.util;

import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.user;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import java.util.ArrayList;
import org.bson.Document;

public class global {
    public static String nick;
    public static String id_user;

    public static ArrayList<card_collection> owned_cards_list = null;
    public static ArrayList<Document> full_card_list = null;
    
    public static user user;
    public static formation saved_formation_local;
    public static formation saved_formation_server=null;
    public static int max_card_id = 531;
    public static final String MONGO_URI = "mongodb://172.16.5.23:27017,172.16.5.24:27017,172.16.5.25:27017/"+
           "?retryWrites=true&w=majority&wtimeout=10000";
    public static final String DATABASE_NAME = "FantaManager";
    public static final String USERS_COLLECTION_NAME = "Users";
    public static final String TRADES_COLLECTION_NAME = "Trades";
    public static final String CARDS_COLLECTION_NAME = "Cards";
    
    public static final String REDIS_URI = "172.16.5.23";
    public static final int REDIS_PORT = 6379;
    
    public static int next_matchday = 0;
    public static int updated_matchdays[] = new int[38];
}
