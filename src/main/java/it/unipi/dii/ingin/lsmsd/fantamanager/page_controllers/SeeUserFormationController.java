package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.SeeUserMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;



public class SeeUserFormationController implements Initializable {

	public static Document user_document;
	public static int matchday=1;
	@FXML
	private Parent root;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		System.out.println(user_document.toString());
		Document formations= (Document) user_document.get("formations");
		System.out.println(formations.toString());
		Object matchdayDocument= (Object) formations.get(matchday);
		System.out.println(matchdayDocument.getClass());
		System.out.println(matchday);
	}
	

	
}
