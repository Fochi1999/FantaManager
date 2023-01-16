package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.admin.retrieve_matchday;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.utilities;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static it.unipi.dii.ingin.lsmsd.fantamanager.admin.calculate_matchday.calculate_card_score;

public class AdminPageController implements Initializable {

    public AnchorPane root;
    public ChoiceBox matchday_list;
    @FXML
    private Button calculate_button;

    @FXML
    private HBox admin_hbox;

    @FXML
    private TableView table_matchday;

    @FXML
    private TextArea match_calculated;

    public void click_home(MouseEvent mouseEvent) throws IOException {

        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

            //for choice box to calculate matchday score
            for (int i = 1; i <= 38; i++) {
                matchday_list.getItems().add(i);
            }

            calculate_button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent e) {
                    //System.out.println("QUI INSERIRE COME CALCOLARE LA GIORNATA");
                    //qui da player_class
                    //retrieve_matchday((Integer) matchday_list.getValue());
                    //calculate_matchday((Integer) matchday_list.getValue());
                    //calculate_player_score((Integer) matchday_list.getValue());

                    //qui da admin/calculate_matchday
                    retrieve_matchday.retrieve_info_matchday((Integer) matchday_list.getValue());
                    calculate_card_score((Integer) matchday_list.getValue());
                }
            });

            //for the table matchday to calculate
            String elenco_match="";
            int[] array=global.updated_matchdays;

            for(int i=0;i<38;i++){
                if(array[i]==1){
                    //match calcolato
                    if(i%4==0 && i!=0)
                        elenco_match+="\n";
                    elenco_match+="(Matchday "+(i+1)+": YES) ";

                }else{
                    //match non calcolato
                    if(i%4==0 && i!=0)
                        elenco_match+="\n";
                    elenco_match+="(Matchday "+(i+1)+": NO) ";

                }
            }
            match_calculated.setText(elenco_match);
    }


    public void increase_next_matchday(MouseEvent mouseEvent) {
            int next_matchday=global.next_matchday+1;
            utilities.set_next_matchday_redis(next_matchday);
    }
}
