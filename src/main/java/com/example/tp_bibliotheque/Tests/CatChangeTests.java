package com.example.tp_bibliotheque.Tests;

import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.Objects.CatChange;
import com.example.tp_bibliotheque.Objects.Categorie;
import java.sql.Date;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

//CLASSE DE TESTS DE METHODES RELATIVES AUX CATCHANGE

public class CatChangeTests {
    //*****************ATTRIBUTS*****************//

    //4e CatChange de la BDD
    private final CatChange catChangeFour;


    //*****************METHODES*****************//

    //Constructeur de classe
    public CatChangeTests() throws SQLException {
        //Pour pouvoir se connecter à la BDD
        MainApplication main = new MainApplication();

        catChangeFour = CatChange.getCatChange(4);
    }

    //Test de la méthode getIdFromBDD()
    @org.junit.Test
    public void getIdFromBDD() throws SQLException {
        System.out.println("Testing Getting ID From BDD");

        //ID du 4e CatChange de la BDD
        assertEquals(4, CatChange.getIdFromBDD(3, 5, catChangeFour.getDate(), Categorie.Forbidden, Categorie.Cat2));
        //Test d'un faux CatChange
        assertEquals(-1, CatChange.getIdFromBDD(2, 5, new Date(2023,11,1), Categorie.Forbidden, Categorie.Cat3));

        System.out.println("Test Passed :)");
    }
}
