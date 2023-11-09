package com.example.tp_bibliotheque.Tests;

import com.example.tp_bibliotheque.BDDConnector;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

//CLASSE DE TESTS DE METHODES RELATIVES AU BDDCONNECTOR

public class BDDConnectorTests {
    //*****************METHODES*****************//

    //Test de la méthode cleanAttribute()
    @org.junit.Test
    public void cleanAttribute() throws SQLException {
        System.out.println("Testing Cleaning Attributes");

        //Réduction de la String "Test"
        assertEquals("es", BDDConnector.cleanAttribute("Test", 1, 3));
        //Réduction de la String "Testing Cleaning Attribute"
        assertEquals("ting Clean", BDDConnector.cleanAttribute("Testing Cleaning Attributes", 3, 13));

        System.out.println("Test Passed :)");
    }
}
