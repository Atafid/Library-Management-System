package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.MainApplication;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;

//CONTROLLER ABSTRAIT

public abstract class ApplicationController {
    //*****************ATTRIBUTS*****************//

    //AnchorPane : racine de la page
    @FXML private AnchorPane root;


    //*****************METHODES*****************//

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur+
    public void initialize() throws SQLException {
        //Ajout du header à la racine de la fenêtre
        root.getChildren().add(MainApplication.header.getHead());
    }
}
