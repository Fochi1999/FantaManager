package it.unipi.dii.ingin.lsmsd.fantamanager.player_classes;


public class player_class{

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
    public int credits;
    public general_statistics_class general_statistics;
    public statistics_class statistics;


    //CONSTRUCTOR
    public player_class(String player_fullname_input, String player_firstname_input, String player_lastname_input, int id_input, int age_input, String birth_date_input, String birth_place_input,
                        String birth_country_input, String nationality_input, String height_input, String weight_input, String position_input,
                        String team_input, String career_input, int credits_input)
    {
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
        this.credits = credits_input;

        this.general_statistics = new general_statistics_class();
        this.statistics = new statistics_class();
    }

    //CONTRUCTOR (only name)
    public player_class(String player_name_input, int id_input) {
        this.player_id = id_input;
        this.fullname = player_name_input;

        //case where the player is called only by his lastname
        String[] words = player_name_input.split(" ");
        if (words.length > 1){
            this.firstname = words[0];
            this.lastname = "";
            for(int i=1; i<words.length; i++) {
                this.lastname = this.lastname + words[i];
            }
        }
        else {
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
/*
	"name": "L. De Silvestri",
    "firstname": "Lorenzo", "lastname": "De Silvestri", "age": 34,
    "birth": {"date": "1988-05-23", "place": "Roma", "country": "Italy"}, "nationality": "Italy", "height": "186 cm", "weight": "84 kg",
    "Pos": "D", "team": "Bologna",
    "general_statistics":{
        "games": {
                "appearences": 31, "lineups": 27, "minutes": 2300, "captain": false
            },
        "substitutes": {"in": 4, "out": 9, "bench": 5},
         "shots": {"total": 14, "on": 7},
          "goals": {"total": 3, "conceded": 0, "assists": 2, "saves": null},
         "passes": {"total": 1073, "key": 21, "accuracy": 25},
         "tackles": {"total": 23, "blocks": 7, "interceptions": 26},
          "duels": {"total": 255, "won": 132},
          "dribbles": {"attempts": 18, "success": 6, "past": null},
          "fouls": {"drawn": 28, "committed": 28},
          "cards": {"yellow": 7, "yellowred": 0, "red": 0},
          "penalty": {"won": null, "commited": null, "scored": 0, "missed": 0, "saved": null}
    },
    "statistiche":{
            "matchday1":{
                    "CR": "8.80", "Plus": "1.20", "Apps": "1", "Starter": "1", "Mins": "90", "Goals": "2",
                     "Shots": "2", "On Tar. Shots": "2", "Pen Goals": "0", "Successful Dribbles": "0", "Ast": "0",
                     "Acc Pass": "59", "Key Pass": "1", "Fouls": "0", "Was Fouled": "1", "YC": "0", "RC": "0", "Rec Ball": "1",
                     "Tackles": "1", "Clean Sheets": "0", "xG":"0",  "xA":"0", "xGchain":"0.0", "xGBuildup":"0.0",

                     "shotsInfo":{
                                "X":"",
                                "Y":"",
                                "xG":"",
                                "min":"",
                                "results":"", //goal,blocked,fuori
                                "situation":"",
                                "shotType":"",
                                "assist":{
                                        "player":"",
                                        "action":""

                                }
                     }
                    },
            "matchdat2":{},


            "matchday38":{}
 },
 "carrier": "Lorenzo De Silvestri (Italian pronunciation: [lo\u02c8r\u025bntso de sil\u02c8v\u025bstri]; born 23 May 1988) is an Italian professional footballer who plays as a defender for Bologna. He is an offensive full-back with an imposing physique; although naturally a right-back, he is capable of playing on either flank as well as in the middle.",
 "credits":""
 }
}
*/
