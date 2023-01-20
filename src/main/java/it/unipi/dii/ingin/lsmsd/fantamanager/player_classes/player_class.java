package it.unipi.dii.ingin.lsmsd.fantamanager.player_classes;


import com.google.gson.Gson;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.formation;
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
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

public class player_class {

    public int player_id;
    public String fullname;
    public String firstname;
    public String lastname;
    public int age;
    public String birth_date;
    public String birth_place;
    public String birth_country;
    public String nationality;
    public String height;
    public String weight;
    public String position;
    public String team;
    public String career;
    public int credits_init;
    public int credits;
    public general_statistics_class general_statistics;
    public statistics_class statistics;


    //CONSTRUCTOR
    public player_class(String player_fullname_input, String player_firstname_input, String player_lastname_input, int id_input, int age_input, String birth_date_input, String birth_place_input,
                        String birth_country_input, String nationality_input, String height_input, String weight_input, String position_input,
                        String team_input, String career_input, int credits_init, int credits_input) {
        this.player_id = id_input;
        this.fullname = player_fullname_input;
        this.firstname = player_firstname_input;
        this.lastname = player_lastname_input;
        this.age = age_input;
        this.birth_date = birth_date_input;
        this.birth_place = birth_place_input;
        this.birth_country = birth_country_input;
        this.nationality = nationality_input;
        this.height = height_input;
        this.weight = weight_input;
        this.position = position_input;
        this.team = team_input;
        this.career = career_input;
        this.credits_init=credits_init;
        this.credits = credits_input;

        this.general_statistics = new general_statistics_class();
        this.statistics = new statistics_class();
    }

    //CONSTRUCTOR (only name)
    public player_class(String player_name_input, int id_input) {
        this.player_id = id_input;
        this.fullname = player_name_input;

        //case where the player is called only by his lastname
        String[] words = player_name_input.split(" ");
        if (words.length > 1) {
            this.firstname = words[0];
            this.lastname = "";
            for (int i = 1; i < words.length; i++) {
                this.lastname = this.lastname + words[i];
            }
        } else {
            this.firstname = "";
            this.lastname = words[0];
        }

        this.age = 0;
        this.birth_date = "";
        this.birth_place = "";
        this.birth_country = "";
        this.nationality = "";
        this.height = "";
        this.weight = "";
        this.position = "";
        this.team = "";
        this.credits = 0;

        this.general_statistics = new general_statistics_class();
        this.statistics = new statistics_class();
    }
}