package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Objects.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;

//CONTROLLER DE LA VIEW ADMIN USER

public class AdminUserViewController extends ApplicationController {
    //*****************ATTRIBUTS*****************//

    //GridPane : utilisateurs à afficher dans la fenêtre
    @FXML private GridPane userGrid;

    //Elements relatifs au système de pages d'élements à afficher : les utilisateurs
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page userPage;

    //L'administrateur actuellement connecté sur la page
    private final User admin;


    //*****************METHODES*****************//

    //Constructeur de classe
    public AdminUserViewController(User _admin) {
        admin = _admin;
    }

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    public void initialize() throws SQLException {
        AdminUserViewController.super.initialize();

        //Initialisation de la page d'utilisateurs à afficher
        userPage = new Page(prevButton, nextButton, User.getAllUsers(),userGrid, pageLabel);
    }

}
