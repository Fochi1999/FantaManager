module it.unipi.dii.ingin.lsmsd.fantamanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires org.json;
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires com.google.gson;
    requires redis.clients.jedis;
    requires json.simple;
    requires org.apache.commons.pool2;
    requires com.fasterxml.jackson.databind;

    opens it.unipi.dii.ingin.lsmsd.fantamanager to javafx.fxml;
    exports it.unipi.dii.ingin.lsmsd.fantamanager;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.users;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.trades;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.formation;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.cards;
    opens it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers to javafx.fxml;
    opens it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.users to javafx.fxml;
    opens it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.trades to javafx.fxml;
    opens it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.cards to javafx.fxml;
    opens it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers.formation to javafx.fxml;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.formation;
    opens it.unipi.dii.ingin.lsmsd.fantamanager.formation to javafx.fxml,com.fasterxml.jackson.databind;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.user to com.fasterxml.jackson.databind;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.admin to com.fasterxml.jackson.databind;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.player_classes to com.google.gson;

}
