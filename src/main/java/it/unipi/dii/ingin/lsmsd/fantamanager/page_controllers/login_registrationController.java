package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.login;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.hash;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class login_registrationController {

    //Stage stage = new Stage();

    @FXML
    private Parent root;
    @FXML
    private TextField loginPasswordFieldReg2;
    @FXML
    private TextField loginPasswordFieldReg;
    @FXML
    private TextField loginPasswordField;
    @FXML
    private TextField loginUsernameField;
    @FXML
    private TextField loginUsernameFieldReg;
    @FXML
    protected void login() throws IOException, NoSuchAlgorithmException {
        String hashPass=hash.MD5(loginPasswordField.getText());
        if(login.login(loginUsernameField.getText(),hashPass)) {
            Stage stage = (Stage) root.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("home_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Homepage");
            stage.setScene(scene);
            stage.show();
        }
        else{
            System.out.println("login fallito");
        }
    }
    @FXML
    protected void register() throws IOException, NoSuchAlgorithmException {
        if(loginPasswordFieldReg.getText().equals(loginPasswordFieldReg2.getText())==false){
            System.out.println("password diverse");
            return;
        }
        String hashPass=hash.MD5(loginPasswordFieldReg.getText());
        if(login.register(loginUsernameFieldReg.getText(),hashPass)) {
            Stage stage = (Stage) root.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("home_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("Homepage");
            stage.setScene(scene);
            stage.show();
        }
        else{
            System.out.println("registrazione fallito");
        }
    }


}