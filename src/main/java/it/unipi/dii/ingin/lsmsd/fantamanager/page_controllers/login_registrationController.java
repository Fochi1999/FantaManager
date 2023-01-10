package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.login;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.utilities;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.hash;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;

public class login_registrationController implements Initializable{

    @FXML
    private Parent root;
    
    @FXML
    private Text loginWarningText;
    @FXML
    private Text loginWarningTextReg;
    
    @FXML
    private TextField loginEmailFieldReg;
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
    private ChoiceBox loginRegionFieldReg;
    
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    
    	//add region list to his field
    	for(int i=0; i<utilities.regionList.length-1; i++) {
    		loginRegionFieldReg.getItems().add(utilities.regionList[i]);
    	}
    	
    	//hiding error texts
    	loginWarningText.setText("");
    	loginWarningTextReg.setText("");
    }
    
    @FXML
    protected void login() throws IOException, NoSuchAlgorithmException {
    	
    	//retrieving inputs
    	String pass =loginPasswordField.getText(); 
    	String username = loginUsernameField.getText();
    	
    	//missing input values
    	if(pass.equals("") || username.equals("")) {
        	loginWarningText.setText("Missing input values!");	
        	System.out.println("Missing input values!");
        	return;
        }
    	
    	//removing warning text
        loginWarningText.setText("");
    	loginWarningTextReg.setText("");
    	
    	//login
    	String hashPass=hash.MD5(loginPasswordField.getText());
        if(login.login(loginUsernameField.getText(), hashPass)) {
        	//open a new window (prevents uncorrect positioning of the window)
        	Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("home_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setResizable(false);
            stage.setTitle("Welcome to Fantamanager!");
            stage.setScene(scene);
            stage.show();
            
            //close the login window
            Stage loginstage = (Stage) root.getScene().getWindow();
            loginstage.close();
        }
        else{
            System.out.println("Login failed!");
            loginWarningText.setText("Username and password fields doesn't match or are incorrect!");
        }
    }
    @FXML
    protected void register() throws IOException, NoSuchAlgorithmException {
        
    	//retrieving inputs
    	String email = loginEmailFieldReg.getText();
    	String pass =loginPasswordFieldReg.getText(); 
    	String pass_conf =loginPasswordFieldReg2.getText(); 
        String username = loginUsernameFieldReg.getText();
        Object region_obj = loginRegionFieldReg.getValue();
        
        //input error cases
    	if(region_obj == null) {	//region not selected
    		loginWarningTextReg.setText("Missing input values!");
        	System.out.println("Missing input values!");
        	return;
    	}
    	String region = region_obj.toString();
        if(email.equals("") || pass.equals("") || pass_conf.equals("") || region.equals("") || username.equals("")) {
        	loginWarningTextReg.setText("Missing input values!");	//missing input values
        	System.out.println("Missing input values!");
        	return;
        }
    	if(loginPasswordFieldReg.getText().equals(loginPasswordFieldReg2.getText())==false){	//password fields not matching
            System.out.println("Password fields does not match!");
            loginWarningTextReg.setText("Password fields does not match!");
            return;
        }
    	if(!OptionsMongoDriver.validate_email(email)) {	//invalid email input
        	loginWarningTextReg.setText("Invalid email input!");
        	System.out.println("Invalid email input!");
        	return;
        }
    	
    	//removing warning text
        loginWarningText.setText("");
    	loginWarningTextReg.setText("");
    	
        
    	//registration
        String hashPass = hash.MD5(loginPasswordFieldReg.getText());
        if(login.register(loginUsernameFieldReg.getText(),hashPass, email, region)) {
            Stage stage = (Stage) root.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("home_page.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            stage.setResizable(false);
            stage.setTitle("Welcome to Fantamanager!");
            stage.setScene(scene);
            stage.show();
        }
        else{
            System.out.println("Registration failed!");
            loginWarningTextReg.setText("Registration failed! Username already in use or network error!");
        }
    }


}