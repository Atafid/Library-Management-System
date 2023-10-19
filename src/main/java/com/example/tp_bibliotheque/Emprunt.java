package com.example.tp_bibliotheque;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Emprunt {
    private int id;
    private String editionISBN;
    private String userMail;
    private Date beginDate;
    private Date hypEndDate;
    private Date realEndDate;
    private boolean isFinished;

    public Emprunt(int _id, String _editionISBN, String _userMail, Date _beginDate, Date _endDate, boolean _isFinished) {
        id = _id;
        editionISBN = _editionISBN;
        userMail = _userMail;
        beginDate = _beginDate;
        hypEndDate = _endDate;
        isFinished = _isFinished;
    }

    public static void addEmprunt(Edition edition, User user, Date beginDate, Date endDate) throws SQLException {
        String querry = "INSERT INTO Emprunt VALUES(0,?,?,?,?,NULL,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, edition.getIsbn());
        stmt.setString(2, user.getMail());
        stmt.setDate(3, beginDate);
        stmt.setDate(4, endDate);
        stmt.setBoolean(5, false);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }
    public static Emprunt getCurrentEmpruntFromEdition(Edition edition) throws SQLException {
        String querry = "SELECT * FROM Emprunt WHERE editionISBN=? AND isFinished=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1,edition.getIsbn());

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getDate(5), rs.getBoolean(6));
            return(emprunt);
        }

        return(null);
    }
    public static void finishEmprunt(int id, Date date) throws SQLException {
        String querry = "UPDATE Emprunt SET isFinished=true, realEnddate=? WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setDate(1, date);
        stmt.setInt(2, id);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    public int getId() { return(id); }
    public String getUserMail() { return(userMail); }
    public String getHypEndDate() {
        return(hypEndDate.toString());
    }
    public String getRealEndDate() {
        return(realEndDate.toString());
    }

    public void setEndDate(Date date) {
        realEndDate = date;
    }
}
