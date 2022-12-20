package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class OptionController {
	


    @FXML
    private Parent root;

    @FXML
    protected void click_home() throws IOException {

        Stage stage= (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }

    
}