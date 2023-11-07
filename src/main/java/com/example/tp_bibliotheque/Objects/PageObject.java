package com.example.tp_bibliotheque.Objects;

import javafx.scene.layout.GridPane;

//CONTRAT A IMPLEMENTER POUR ETRE UN OBJET AFFICHABLE PAR PAGES

public interface PageObject {
    //*****************METHODES*****************//

    //Méthode de remplissage de la grille de la page
    public void fillGrid(GridPane grid, int rowIdx);
}
