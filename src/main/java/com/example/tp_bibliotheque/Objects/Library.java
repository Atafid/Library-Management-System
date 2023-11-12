package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//CLASSE REPRESENTANT UNE BIBLIOTHEQUE

public class Library {
    //*****************ATTRIBUTS*****************//

    //ID de la bibliothèque dans la BDD
    private final int id;

    //Nom de la bibliothèque
    private final String name;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Library(int _id, String _name) {
        id = _id;
        name = _name;
    }

    //Méthode static permettant d'obtenir une bibliothèque à partir de son ID dans la BDD
    public static Library getLibraryFromId(int _id) throws SQLException {
        //Requête SQL récupérant l'édition
        String querry = "SELECT * FROM Library WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, _id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Library library = new Library(rs.getInt(1), rs.getString(2));
            return(library);
        }

        return(null);
    }

    //Méthode static permettant de compter le nombre de bibliothèques dans la BDD
    public static int countLibrary() throws SQLException {
        String querry = "SELECT COUNT(id) FROM Library";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            return(rs.getInt(1));
        }

        return(-1);
    }

    //Méthode de conversion en String
    public String toString() {
        return(name);
    }

    //GETTERS DE CLASSE
    public int getId() { return id; }
    public String getName() { return name; }
}
