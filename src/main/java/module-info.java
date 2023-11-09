module com.example.tp_bibliotheque {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.junit.jupiter.api;
    requires junit;

    opens com.example.tp_bibliotheque to javafx.fxml;
    exports com.example.tp_bibliotheque;
    exports com.example.tp_bibliotheque.Controllers;
    opens com.example.tp_bibliotheque.Controllers to javafx.fxml;
    exports com.example.tp_bibliotheque.Objects;
    opens com.example.tp_bibliotheque.Objects to javafx.fxml;
    exports com.example.tp_bibliotheque.Tests;
    opens com.example.tp_bibliotheque.Tests to javafx.fxml;
}