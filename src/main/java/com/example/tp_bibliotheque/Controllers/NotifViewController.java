package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Objects.Notification;
import com.example.tp_bibliotheque.Objects.Page;
import com.example.tp_bibliotheque.Objects.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;

//CONTROLLER DE LA VIEW NOTIFICATIONS

public class NotifViewController extends ApplicationController{
    //*****************ATTRIBUTS*****************//

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
    public void initialize() throws SQLException {
        NotifViewController.super.initialize();

        //Initialisation de la page de notifications à afficher
        try {
            notifPage = new Page(prevButton, nextButton,  Notification.getNotifications(user.getId()), notifGrid, pageLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
