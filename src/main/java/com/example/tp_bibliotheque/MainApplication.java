package com.example.tp_bibliotheque;

import com.example.tp_bibliotheque.Objects.Book;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

//CLASSE DE L'APPLICATION PRINCIPALE

public class MainApplication extends Application {
    //*****************ATTRIBUTS*****************//

    //Pepper de l'application
    public static String pepper;

    //Nombre d'incrémentation lors du hash
    public static int hashIncrementation;

    //BDDConnector
    public static BDDConnector bddConn;

    //Header
    public static Header header;

    //Premières pages d'accueil déjà chargées afin de fluidifier la connexion
    public static Vector<Book> fstPageHome;
    public static Vector<Book> sndPageHome;


    //*****************METHODES*****************//

    //Méthode d'initialisation
    @Override
    public void start(Stage stage) throws IOException {
        //Initialisation des attributs
        pepper = "azerty";
        hashIncrementation = 5;

        bddConn = new BDDConnector();

        fstPageHome = Book.getPage(1);
        sndPageHome = Book.getPage(2);

        //Chargement de la view de connexion
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("fxml/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setTitle("Bibliothèque");
        stage.setScene(scene);
        stage.show();
    }

    //Méthode static permettant de changer de scène
    @FXML
    public static void switchScene(ActionEvent e, String url) throws IOException {
        //Récupération du stage
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();

        //Chargement de la nouvelle view
        Parent newRoot = FXMLLoader.load(Objects.requireNonNull(MainApplication.class.getResource(url)));
        stage.getScene().setRoot(newRoot);
        System.gc();
    }

    //Méthode static surchargée permettant de changer de scène en spécifiant un controller
    @FXML
    public static void switchScene(ActionEvent e, String url, Object controller) throws IOException {
        //Récupération du stage
        Stage stage = (Stage) ((Node)e.getSource()).getScene().getWindow();

        //Chargement de la nouvelle view
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource(url));
        loader.setController(controller);

        stage.getScene().setRoot(loader.load());
        System.gc();
    }

    //Méthode static de lancement de l'application
    public static void main(String[] args) {
        launch();
    }
}