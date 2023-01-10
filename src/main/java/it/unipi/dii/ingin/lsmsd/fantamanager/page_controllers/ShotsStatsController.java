package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.card_collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.collection.collection;
import it.unipi.dii.ingin.lsmsd.fantamanager.player_classes.see_card;
import it.unipi.dii.ingin.lsmsd.fantamanager.user.userMongoDriver.OptionsMongoDriver;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.global;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class ShotsStatsController implements Initializable{
	

	
	@FXML public Parent root;
	@FXML private ImageView field_image;
	@FXML private Pane pane_field;

	private int last_updated_matchday;

	private Document card_doc; //saving the card's document
	
	@Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
		
		//buy_card.setDisable(true);	//disabling the buy button
		
		search_card();
		last_updated_matchday = 2;	//TODO implementare
		System.out.println(card_doc.toString());
		Document stats=(Document)card_doc.get("statistics");
		System.out.println(stats.toString());
		Document matchdays=(Document)stats.get("matchday");
		System.out.println(matchdays.toString());
		for(int i=1;i<=last_updated_matchday;i++){
			Document matchday=(Document)matchdays.get("matchday"+i);
			System.out.println(matchday.toString());
			Document statsmatchday=(Document)matchday.get("stats");
			ArrayList<Document> shotsInfo=(ArrayList<Document>)statsmatchday.get("shotsInfo");
			System.out.println(shotsInfo.toString()+" class "+shotsInfo.getClass());
			for(int j=0;j<shotsInfo.size();j++){
				Document shot=shotsInfo.get(j);
				String minS=(String)shot.get("min");
				String type=(String)shot.get("shotType");
				String xS=(String)shot.get("X");
				String yS=(String)shot.get("Y");
				String result=(String)shot.get("results");
				String situation=(String)shot.get("situation");
				Document assist=(Document)shot.get("assist");
				System.out.println("x"+xS+" y "+yS+" result"+result);
				double x=Double.parseDouble(xS);
				double y=Double.parseDouble(yS);
				create_points_shots(x,y,result);
			}
		}




	}
	
	public void create_points_shots(double x,double y, String type){
		double Xlen=field_image.getImage().getWidth();
		double Ylen=field_image.getImage().getHeight();
		Circle point=new Circle(y*Xlen,x*Ylen,5);
		point.setFill(Color.DARKRED);
		pane_field.getChildren().add(point);

	}
	@FXML
    protected void click_back() throws IOException {
		System.out.println("Returning back to the shop page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.go_to_shop(stage);   
    }
	
	@FXML
    protected void click_home() throws IOException {

		System.out.println("Returning back to the home page...");
        Stage stage = (Stage)root.getScene().getWindow();
        util_controller.back_to_home(stage);
    }
	
	
	public void search_card() {
		
		//searching the card from the DB
		ObjectId card_id = new ObjectId(ShopController.card_id_input);
		card_doc = see_card.search_card(card_id);
    	
		if(card_doc == null) {	//handling error
			String t = ("An error has occurred while searching for the card. Please, exit the page and try again later.");
			return;
		}

    }
	
	

}
