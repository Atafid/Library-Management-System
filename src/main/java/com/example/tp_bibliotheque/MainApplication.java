package com.example.tp_bibliotheque;

import com.example.tp_bibliotheque.Objects.Book;
import com.example.tp_bibliotheque.Objects.User;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Vector;

public class MainApplication extends Application {
    public static String pepper;
    public static int hashIncrementation;
    public static BDDConnector bddConn;
    public static Header header;
    public static Vector<Book> fstPageHome;
    public static Vector<Book> sndPageHome;

    @Override
    public void start(Stage stage) throws IOException {
        pepper = "azerty";
        hashIncrementation = 5;

        bddConn = new BDDConnector();

        fstPageHome = Book.getPage(1);
        sndPageHome = Book.getPage(2);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Bibliothèque");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public static void switchScene(ActionEvent e, String url) throws IOException {
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();

        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource(url)));
        stage.getScene().setRoot(newRoot);
        System.gc();
    }

    @FXML
    public static void switchScene(ActionEvent e, String url, Object controller) throws IOException {
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(url));
        loader.setController(controller);

        stage.getScene().setRoot(loader.load());
        System.gc();
    }

    public static void main(String[] args) {
        launch();
    }
}