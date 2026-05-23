module com.java.quanlykho {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires com.github.librepdf.openpdf;

    opens com.java.quanlykho to javafx.fxml;
    opens com.java.quanlykho.controller to javafx.fxml;
    opens com.java.quanlykho.model to javafx.base, javafx.fxml;

    exports com.java.quanlykho;
    exports com.java.quanlykho.controller;
    exports com.java.quanlykho.model;
    exports com.java.quanlykho.network;
    exports com.java.quanlykho.server;
    exports com.java.quanlykho.util;
}
