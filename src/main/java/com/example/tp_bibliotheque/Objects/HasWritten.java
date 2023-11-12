package com.example.tp_bibliotheque.Objects;

//CLASSE REPRESENTANT LA RELATION ENTRE UN LIVRE ET SES AUTEURS

import com.example.tp_bibliotheque.MainApplication;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class HasWritten {
    //*****************ATTRIBUTS*****************//

    //Livre pris en compte
    private final Book book;
    //Auteur pris en compte
    private final Author author;
    //Rôle de l'auteur par rapport au livre (écrivain, illustrateur, etc)
    private final String role;


    //*****************METHODES*****************//

    //Constructeur de classe
    public HasWritten(Book _book, Author _author, String _role) {
        book = _book;
        author = _author;
        role = _role;
    }

    //Méthode static permettant d'ajouter une relation d'écriture dans la BDD
    public static void addHasWritten(int bookId, int authorId, String role) throws SQLException {
        //Requête SQL ajoutant la relation à la BDD
        String querry = "INSERT INTO AEcrit VALUES(?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, bookId);
        stmt.setInt(2, authorId);
        stmt.setString(3, role);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    //GETTERS DE CLASSE
    public Author getAuthor() {
        return author;
    }
    public String getRole() {
        return role;
    }
}
