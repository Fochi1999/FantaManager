package it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.formation;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import it.unipi.dii.ingin.lsmsd.fantamanager.formation.player_formation;
import it.unipi.dii.ingin.lsmsd.fantamanager.util.util_controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
	@FXML
	private HBox HBox_gk;
	@FXML
	private HBox HBox_d;
	@FXML
	private HBox HBox_m;
	@FXML
	private HBox HBox_a;
	private HBox[] HBoxes;
	@FXML
	private VBox VBox_Bench;
	@FXML
	private Text text_module;
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		HBoxes=new HBox[4];
		HBoxes[0]=HBox_gk;
		HBoxes[1]=HBox_d;
		HBoxes[2]=HBox_m;
		HBoxes[3]=HBox_a;
		see_formation();

	}

	private void see_formation() {

		Document formations= (Document) user_document.get("formations");

		Document matchdayDocument= (Document) formations.get(Integer.toString(matchday));

		ArrayList<Long> module=(ArrayList) matchdayDocument.get("module");
		Document playersDocument=(Document) matchdayDocument.get("players");
		text_module.setText("Module: "+module.get(1)+"-"+module.get(2)+"-"+module.get(3));
		ArrayList<player_formation> players= new ArrayList<>();
		for(int i=0;i<19;i++){
			Document playerDocument= (Document) playersDocument.get(Integer.toString(i));
			String name=playerDocument.getString("name");
			String team=playerDocument.getString("team");
			Long id;
			Double vote= (double) 0.0;
			try{
				id=playerDocument.getLong("id");
			}catch(Exception e){
				try{
					id= Long.valueOf(playerDocument.getInteger("id"));
				} catch (Exception ex) {
					id=0L;
				}
			}
			try{
				vote=playerDocument.getDouble("vote");
			}catch(Exception e){
				try{
					vote= Double.valueOf(playerDocument.getInteger("vote"));
				} catch (Exception ex) {
					vote=0.0;
				}
			}
			player_formation p=new player_formation(name, Math.toIntExact(id),team,vote);
			players.add(p);


			/*System.out.println(playerDocument.toString());
			System.out.println("classi: name "+playerDocument.get("name").getClass()+" id "+playerDocument.get("id").getClass()
			+" team "+playerDocument.get("team").getClass()+" vote "+playerDocument.get("vote").getClass());*/

		}
		int index=0;
		for(int i=0;i<4;i++){
			for(int j=0;j<module.get(i);j++){
				player_formation p=players.get(index);
				System.out.println("Index: "+index+" player: "+p.getName());
				String name=p.name;
				Long id= (long) p.id;
				Double vote=p.vote;
				String team=p.team;
				VBox tabPlayer=new VBox();
				tabPlayer.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
				tabPlayer.setAlignment(Pos.TOP_CENTER);
				Text text_name=new Text(name);
				Text text_team=new Text(team);
				String vote_string=String.format("%.2f", vote).replaceAll("(\\.\\d+?)0*$", "$1");
				Text text_vote=new Text("vote: "+vote_string);
				tabPlayer.getChildren().addAll(text_name,text_team,text_vote);
				tabPlayer.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
				HBoxes[i].getChildren().add(tabPlayer);
				index++;
			}
		}
		Text text_bench=new Text("Bench:");
		text_bench.setFill(Color.WHITE);

		for(int i=11;i<19;i++){
			player_formation p=players.get(i);
			String name=p.name;
			Long id= (long) p.id;
			Double vote=p.vote;
			String team=p.team;
			VBox tabPlayer=new VBox();
			tabPlayer.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
			tabPlayer.setAlignment(Pos.TOP_CENTER);
			Text text_name=new Text(name);
			Text text_team=new Text(team);
			String vote_string=String.format("%.2f", vote).replaceAll("(\\.\\d+?)0*$", "$1");
			Text text_vote=new Text("vote: "+vote_string);
			tabPlayer.getChildren().addAll(text_name,text_team,text_vote);
			tabPlayer.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
			VBox_Bench.getChildren().add(tabPlayer);

		}
	}

	@FXML
	protected void click_home() throws IOException {

		System.out.println("Returning back to the home page...");
		Stage stage = (Stage)root.getScene().getWindow();
		util_controller.back_to_home(stage);
	}
	@FXML
	protected void click_back() throws IOException {
		Stage stage = (Stage)root.getScene().getWindow();
		System.out.println("Opening user page...");
		FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("see_user_page.fxml"));
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("Card info");
		stage.setScene(scene);
		stage.show();
	}




}
