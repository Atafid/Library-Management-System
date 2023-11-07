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
