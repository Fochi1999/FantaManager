package it.unipi.dii.ingin.lsmsd.fantamanager.util;

import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.user;

public class global {
    public static String nick;
    public static String id_user;

    public static user user;
    public static formation saved_formation_local;
    public static formation saved_formation_server=null;
    
    public static final String MONGO_URI = "mongodb://localhost:27017";
    public static final String DATABASE_NAME = "FantaManager";
    public static final String USERS_COLLECTION_NAME = "Users";
    public static final String TRADES_COLLECTION_NAME = "Trades";
    public static final String CARDS_COLLECTION_NAME = "Cards";
    
    public static int next_matchday = 1;
    public static int updated_matchdays[] = new int[38];
}
