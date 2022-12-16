package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void click_option() {
        System.out.println("options");
    }
}