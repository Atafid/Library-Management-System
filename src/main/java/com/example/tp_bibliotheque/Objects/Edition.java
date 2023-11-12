package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//CLASSE REPRESENTANT UNE EDITION

public class Edition {
    //*****************ATTRIBUTS*****************//

    //ISBN de l'édition
    private final String isbn;

    //ID du livre relatif à l'édition, dans la BDD
    private final int bookId;

    //Nom de l'éditeur
    private final String editorName;

    //Date de publication
    private final Date publishDate;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Edition(String _isbn, int _bookId, String _editorName, Date _publishDate) {
        isbn = _isbn;
        bookId = _bookId;
        editorName = _editorName;
        publishDate = _publishDate;
    }

    //Méthode static permettant d'ajouter une édtion à la BDD
    public static void addEdition(String isbn, int bookId, String editorName, Date publishDate) throws SQLException {
        //Requête SQL ajoutant l'édition à la BDD
        String querry = "INSERT INTO Edition VALUES(?,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, isbn);
        stmt.setInt(2, bookId);
        stmt.setString(3, editorName);
        stmt.setDate(4, publishDate);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static permettant d'obtenir l'édition à partir de son isbn
    public static Edition getEditionFromIsbn(String _isbn) throws SQLException {
        //Requête SQL récupérant l'édition
        String querry = "SELECT * FROM Edition WHERE isbn=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1, _isbn);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Edition edition = new Edition(rs.getString(1), rs.getInt(2), rs.getString(3),
                    rs.getDate(4));
            return(edition);
        }

        return(null);
    }

    //Méthode static permettant d'obtenir l'édition à partir d'une demande de mouvement d'exemplaire
    public static Edition getEditionFromMoveAsk(int _moveAskId) throws SQLException {
        //Requête SQL récupérant l'édition
        String querry = "SELECT * FROM Edition e JOIN PrintedWork p ON p.editionIsbn=e.isbn JOIN MoveAsk m ON m.printedWorkId=p.id" +
                " WHERE m.id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, _moveAskId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Edition edition = new Edition(rs.getString(1), rs.getInt(2), rs.getString(3),
                    rs.getDate(4));
            return(edition);
        }

        return(null);
    }

    //GETTERS DE CLASSE
    public String getIsbn() { return isbn; }
    public String getEditorName() { return editorName; }
    public Date getPublishDate() { return publishDate; }
}
