package com.example.tp_bibliotheque;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class MainApplication extends Application {
    protected static String pepper;
    protected static int hashIncrementation;
    protected static BDDConnector bddConn;
    protected static Parent homeRoot;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

        pepper = "azerty";
        hashIncrementation = 5;

        bddConn = new BDDConnector();
    }

    @FXML
    public static void initHome(User user) throws IOException {
        HomeController homeController = new HomeController(user);

        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("home-view.fxml"));
        loader.setController(homeController);
        homeRoot = loader.load();
    }

    @FXML
    public static void loadHome(ActionEvent e) {
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();
        stage.getScene().setRoot(homeRoot);
    }

    @FXML
    public static void switchScene(ActionEvent e, String url) throws IOException {
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();

        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource(url)));
        stage.getScene().setRoot(newRoot);
    }

    @FXML
    public static void switchScene(ActionEvent e, String url, Object controller) throws IOException {
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(url));
        loader.setController(controller);

        stage.getScene().setRoot(loader.load());
    }

    public static void main(String[] args) {
        launch();
    }
}