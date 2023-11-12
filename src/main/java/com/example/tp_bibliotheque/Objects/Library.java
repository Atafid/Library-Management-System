package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Library {
    private final int id;
    private final String name;

    public Library(int _id, String _name) {
        id = _id;
        name = _name;
    }

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

    public static int countLibrary() throws SQLException {
        String querry = "SELECT COUNT(id) FROM Library";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            return(rs.getInt(1));
        }

        return(-1);
    }

    public String toString() {
        return(name);
    }

    public int getId() { return id; }
    public String getName() { return name; }
}
