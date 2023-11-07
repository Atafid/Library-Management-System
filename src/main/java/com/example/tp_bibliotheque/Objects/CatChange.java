package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//CLASSE REPRESENTANT UN CHANGEMENT DE CATEGORIE

public class CatChange {
    //*****************ATTRIBUTS*****************//

    //ID du changement de catégorie dans la BDD
    private final int id;

    //ID de l'utilisateur concerné, dans la BDD
    private final int userId;

    //ID de l'administrateur ayant effectué le changement, dans la BDD
    private final int adminId;

    //Date du changement de catégorie
    private final Date date;

    //Ancienne catégorie de l'utilisateur
    private final Categorie prevCat;

    //Nouvelle catégorie de l'utilisateur
    private final Categorie newCat;


    //*****************METHODES*****************//

    //Constructeur de classe
    public CatChange(int _id, int _userId, int _adminId, Date _date, String _prevCat, String _newCat) {
        id = _id;
        userId = _userId;
        adminId = _adminId;
        date = _date;
        prevCat = Categorie.getCatFromString(_prevCat);
        newCat = Categorie.getCatFromString(_newCat);
    }

    //Méthode static pour ajouter un changement de catégorie à la BDD
    public static void addCatChange(int userId, int adminId, Date date, Categorie prevCat, Categorie newCat) throws SQLException {
        //Requête SQL ajoutant le changement de catégorie, l'ID étant en auto_increment
        String querry = "INSERT INTO catChange VALUES(0,?,?,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, userId);
        stmt.setInt(2, adminId);
        stmt.setDate(3, date);
        stmt.setString(4, prevCat.getStringFromCat());
        stmt.setString(5, newCat.getStringFromCat());

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static récupérant l'ID d'un CatChange à partir des ses attributs, dans la BDD
    public static int getIdFromBDD(int userId, int adminId, Date date, Categorie prevCat, Categorie newCat) throws SQLException {
        //Requête SQL récupérant l'ID correspondant au CatChange ayant tous les attributs
        String idQuerry = "SELECT id FROM catChange WHERE userId=? AND adminId=? AND date=? AND prevCat=? AND newCat=? LIMIT 1";
        PreparedStatement idStmt = MainApplication.bddConn.con.prepareStatement(idQuerry);

        idStmt.setInt(1,userId);
        idStmt.setInt(2, adminId);
        idStmt.setDate(3, date);
        idStmt.setString(4, prevCat.getStringFromCat());
        idStmt.setString(5, newCat.getStringFromCat());

        ResultSet rs = idStmt.executeQuery();
        while(rs.next()) {
            return(rs.getInt(1));
        }

        return(-1);
    }

    //Méthode static renvoyant une variable CatChange à partir de son ID dans la BDD
    public static CatChange getCatChange(int id) throws SQLException {
        //Requête SQL récupérant le CatChange
        String querry = "SELECT * FROM catChange WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            return(new CatChange(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                    rs.getDate(4), rs.getString(5), rs.getString(6)));
        }

        return(null);
    }

    //GETTERS DE CLASSE
    public Categorie getPrevCat() { return prevCat; }
    public Categorie getNewCat() { return newCat; }
}
