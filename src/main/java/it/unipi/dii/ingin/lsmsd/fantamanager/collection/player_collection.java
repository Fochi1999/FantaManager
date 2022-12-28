package it.unipi.dii.ingin.lsmsd.fantamanager.collection;

public class player_collection {

            int player_id;

            String name;

            int quantity;

            String team;

            String position;

            public player_collection(int player_id, String name,int quantity, String team, String position){
                    this.player_id=player_id;
                    this.name=name;
                    this.quantity=quantity;
                    this.team=team;
                    this.position=position;
            }

            //public static player_collection get_player_by_name(String name){
                //    return this;
            //}

            public int get_id() {
        return player_id;
    }

            public void set_id(int id) {
        this.player_id = id;
    }

            public String get_name(){return name;}

            public void set_name(String name){this.name=name;}

            public String get_team(){return team;}

            public void set_team(String team){this.team=team;}

            public String get_position(){return position;}

            public void set_position(String position){this.position=position;}

            public int get_quantity(){return quantity;}

            public void set_quantity(int quantity){this.quantity=quantity;}

            public void set_key_info(String key, String value) {



                System.out.println(key+"..."+value);

                switch(key) {
                    case "quantity":
                        this.quantity= Integer.parseInt(value);
                        break;
                    case "name":
                        this.name=value;
                        break;
                    case "position":
                        this.position=value;
                        break;
                    case "team":
                        this.team=value;
                        break;
                }

            }
}
