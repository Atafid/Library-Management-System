package com.example.tp_bibliotheque.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.util.Vector;

//CLASSE REPRESENTANT UNE PAGE D'OBJET A AFFICHER

public class Page {
    //*****************ATTRIBUTS*****************//

    //Button : accéder à la page précédente
    @FXML private Button prevButton;

    //Button : accéder à la page suivante
    @FXML private Button nextButton;

    //GridPane : contient les éléments à afficher
    @FXML private GridPane grid;

    //Label : numéro de la page en cours
    @FXML private Label pageLabel;

    //Numéro de la page en cours
    private int pageCount;

    //Tableau des objets à afficher, tous implémentant le contrat PageObject
    private Vector<PageObject> objects;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Page(Button _prevButton, Button _nextButton, Vector<PageObject> _objects, GridPane _grid, Label _pageLabel) {
        prevButton = _prevButton;
        nextButton = _nextButton;
        grid = _grid;
        pageLabel = _pageLabel;

        pageCount = 1;
        objects = _objects;

        prevButton.setOnAction(event -> {
            onPrevClick();
        });

        nextButton.setOnAction(event -> {
            onNextClick();
        });

        updateFXML();
    }

    //Méthode mettant à jour l'interface graphique
    @FXML
    public void updateFXML() {
        //Boutton de la page précédente visible ssi numéro de page > 0
        prevButton.setVisible(pageCount>1);

        //Boutton de la page suivante visible ssi il reste assez d'objets à afficher
        nextButton.setVisible((pageCount+1)*grid.getRowCount()<objects.size());

        //Si aucun des bouttons n'est visible, on n'affiche pas le numéro de page, car inutile
        pageLabel.setVisible(prevButton.isVisible()||nextButton.isVisible());
        pageLabel.setText(String.valueOf(pageCount));

        //Ajout des objets à la grille
        grid.getChildren().clear();
        for(int i=0; i<grid.getRowCount();i++) {
            if((pageCount-1)*grid.getRowCount()+i>=objects.size()) {
                break;
            }
            objects.get((pageCount-1) * grid.getRowCount() + i).fillGrid(grid, i);
        }
    }

    //Méthode appelée pour afficher la page précédente
    public void onPrevClick() {
        //Mise à jour de l'interface graphique et du numéro de page
        pageCount--;
        updateFXML();
    }

    //Méthode appelée pour afficher la page suivante
    public void onNextClick() {
        //Mise à jour de l'interface graphique et du numéro de page
        pageCount++;
        updateFXML();
    }

    //SETTERS DE CLASSE
    public void setObjects(Vector<PageObject> newObjects) {
        objects = newObjects;
    }
}
