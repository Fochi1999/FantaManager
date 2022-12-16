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

    opens it.unipi.dii.ingin.lsmsd.fantamanager to javafx.fxml;
    exports it.unipi.dii.ingin.lsmsd.fantamanager;
    exports it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers;
    opens it.unipi.dii.ingin.lsmsd.fantamanager.page_controllers to javafx.fxml;
}