package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.Objects.Comment;
import com.example.tp_bibliotheque.Objects.Notification;
import com.example.tp_bibliotheque.Objects.Page;
import com.example.tp_bibliotheque.Objects.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.Vector;

//CONTROLLER DE LA VIEW NOTIFICATIONS

public class NotifViewController {
    //*****************ATTRIBUTS*****************//

    //AnchorPane : racine de la fenêtre
    @FXML private AnchorPane root;

    //GridPane : notifications à afficher
    @FXML private GridPane notifGrid;

    //Elements relatifs au système de page d'éléments à afficher : les notifications de l'utilisateur
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page notifPage;

    //Utilisateur actuellement connecté
    private final User user;


    //*****************METHODES*****************//

    //Constructeur de classe
    public NotifViewController(User _user) {
        user = _user;
    }

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    public void initialize() {
        //Ajout du header à la racine de la fenêtre
        root.getChildren().add(MainApplication.header.getHead());

        //Initialisation de la page de notifications à afficher
        try {
            notifPage = new Page(prevButton, nextButton,  Notification.getNotifications(user.getId()), notifGrid, pageLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
