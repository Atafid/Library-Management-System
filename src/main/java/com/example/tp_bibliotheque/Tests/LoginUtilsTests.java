package com.example.tp_bibliotheque.Tests;

import com.example.tp_bibliotheque.Login.LoginUtils;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.SQLException;

//CLASSE DE TESTS DE METHODES RELATIVES AUX LOGINUTILS

public class LoginUtilsTests {
    //*****************METHODES*****************//

    //Test de la fonction Salt()
    @org.junit.Test
    public void Salt() throws SQLException {
        System.out.println("Testing Salt");

        String salt = LoginUtils.Salt();

        //Longueur du salt
        assertEquals(6, salt.length());

        System.out.println("Test Passed :)");
    }
}
