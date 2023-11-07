package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Objects.Author;
import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.Objects.Page;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;

//CONTROLLER DE LA VIEW AUTEUR

public class AuthorViewController {
    //*****************ATTRIBUTS*****************//

    //AnchorPane : racine de la page
    @FXML private AnchorPane root;

    //Label : nom de l'auteur relatif à la fenêtre
    @FXML private Label authorName;

    //GridPane : livres à afficher dans la fenêtre
    @FXML private GridPane booksGrid;

    //Elements relatifs au système de page d'éléments à afficher : les livres écrits
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page booksPage;

    //Auteur relatif à la fenêtre actuelle
    private final Author author;


    //*****************METHODES*****************//

    //Constructeur de classe
    public AuthorViewController(Author _author) {
        author = _author;
    }

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    public void initialize() {
        //Ajout du header à la racine de la fenêtre
        root.getChildren().add(MainApplication.header.getHead());

        //Initialisation du label relatif au nom de l'auteur
        authorName.setText(author.getName()+" "+author.getLastName());

        try {
            //Initialisation de la page servant à afficher les livres écrits par l'auteur relatif à la fenêtre
            booksPage = new Page(prevButton, nextButton, author.getBooks(), booksGrid, pageLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
