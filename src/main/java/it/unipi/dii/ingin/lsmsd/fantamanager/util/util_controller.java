package it.unipi.dii.ingin.lsmsd.fantamanager.util;

import it.unipi.dii.ingin.lsmsd.fantamanager.app;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class util_controller {
    public static void back_to_home(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("home_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Homepage");
        stage.setScene(scene);
        stage.show();
    }

    public static void go_to_shop(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("shop_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Shop page");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void go_to_ranking(Stage stage) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("ranking_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Ranking page");
        stage.setScene(scene);
        stage.show();
    }
    public static void go_to_formation(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("formation_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("formation page");
        stage.setScene(scene);
        stage.show();
    }
    public static void go_to_trades(Stage stage) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("trade_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Trades page");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void go_to_login(Stage stage) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("login_registration_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void go_to_loading(Stage stage) throws IOException {
    	FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("loading_screen.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Loading...");
        stage.setScene(scene);
        stage.show();
    }

    public static void go_to_card_page(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(app.class.getResource("see_card_page.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Trades page");
        stage.setScene(scene);
        stage.show();
    }
}
