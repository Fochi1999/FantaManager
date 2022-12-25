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
    
    requires org.mongodb.driver.core;
    requires org.mongodb.bson;
    requires org.mongodb.driver.sync.client;
    requires com.google.gson;
    requires redis.clients.jedis;
    requires json.simple;
    requires org.apache.commons.pool2;

    opens it.unipi.dii.ingin.lsmsd.fantamanager to javafx.fxml;
    exports it.unipi.dii.ingin.lsmsd.fantamanager;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;
    opens it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers to javafx.fxml;

}
