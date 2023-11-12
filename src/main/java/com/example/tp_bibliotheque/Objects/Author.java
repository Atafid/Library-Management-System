package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

//CLASSE REPRESENTANT UN AUTEUR -> Personne

public class Author extends Personne {
    //*****************ATTRIBUTS*****************//

    //ID de l'auteur dans la BDD
    private final int id;
    //Date de naissance de l'auteur
    private final Date birth_date;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Author(int _id, String _name, String _last_name, Date _birth) {
        super(_name, _last_name);
        id = _id;
        birth_date = _birth;
    }

    //Méthode static permettant d'ajouter un auteur à la BDD
    public static void addAuthor(String name, String last_name, Date birthDate) throws SQLException {
        //Requête SQL ajoutant l'auteur à la BDD
        String querry = "INSERT INTO AUTHORS VALUES(0,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, name);
        stmt.setString(2, last_name);
        stmt.setDate(3, birthDate);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static permettant de récupérer l'id du dernier auteur inséré
    public static int getLastId() throws SQLException {
        //Requête SQL récupérant l'id
        String querry = "SELECT id FROM Authors ORDER BY id DESC LIMIT 1";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            int lastId = rs.getInt(1);
            return(lastId);
        }

        return(-1);
    }

    //Méthode renvoyant tous les livres écrits par l'auteur
    public Vector<PageObject> getBooks() throws SQLException {
        Vector<PageObject> res = new Vector<PageObject>();

        //Requête SQL récupérant les livres
        String quer = "SELECT * FROM Books b JOIN aEcrit e ON b.id = e.bookID WHERE e.authorID=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,id);

        ResultSet rs = dispStmt.executeQuery();

        //Ajout des livres dans le tableau à renvoyer
        while(rs.next()) {
            Book book = new Book(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5));
            res.add(book);
        }

        return(res);
    }

    //GETTERS DE CLASSE
    public int getId() { return id; }
    public Date getBirth_date() { return birth_date; }
}
