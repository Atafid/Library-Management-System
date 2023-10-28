package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CatChange {
    private int id;
    private int userId;
    private int adminId;
    private Date date;
    private Categorie prevCat;
    private Categorie newCat;

    public CatChange(int _id, int _userId, int _adminId, Date _date, String _prevCat, String _newCat) {
        id = _id;
        userId = _userId;
        adminId = _adminId;
        date = _date;
        prevCat = Categorie.getCatFromString(_prevCat);
        newCat = Categorie.getCatFromString(_newCat);
    }

    public static void addCatChange(int userId, int adminId, Date date, Categorie prevCat, Categorie newCat) throws SQLException {
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
    public static int getIdFromBDD(int userId, int adminId, Date date, Categorie prevCat, Categorie newCat) throws SQLException {
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
    public static CatChange getCatChange(int id) throws SQLException {
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

    public Categorie getPrevCat() { return prevCat; }
    public Categorie getNewCat() { return newCat; }
}
