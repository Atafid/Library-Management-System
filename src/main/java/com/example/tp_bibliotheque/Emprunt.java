package com.example.tp_bibliotheque;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

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

    public Emprunt(int _id, String _editionISBN, String _userMail, Date _beginDate, Date _endDate, Date _realEndDate, boolean _isFinished) {
        id = _id;
        editionISBN = _editionISBN;
        userMail = _userMail;
        beginDate = _beginDate;
        hypEndDate = _endDate;
        realEndDate = _realEndDate;
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
    public static Vector<Emprunt> getCurrentEmpruntFromEdition(String editionISBN) throws SQLException {
        Vector<Emprunt> res = new Vector<Emprunt>();

        String querry = "SELECT * FROM Emprunt WHERE editionISBN=? AND isFinished=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1, editionISBN);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getDate(5), rs.getBoolean(6));
            res.add(emprunt);
        }

        return(res);
    }

    public static Vector<Emprunt> getCurrentEmpruntFromUser(String userMail) throws SQLException {
        Vector<Emprunt> res = new Vector<Emprunt>();

        String querry = "SELECT * FROM Emprunt WHERE userMail=? AND isFinished=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1, userMail);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getDate(5), rs.getBoolean(6));
            res.add(emprunt);
        }

        return(res);
    }

    public static Vector<Emprunt> getFinishedEmpruntFromUser(String userMail) throws SQLException {
        Vector<Emprunt> res = new Vector<Emprunt>();

        String querry = "SELECT * FROM Emprunt WHERE userMail=? AND isFinished=true";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1, userMail);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4), rs.getDate(5), rs.getDate(6), rs.getBoolean(7));
            res.add(emprunt);
        }

        return(res);
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
    public String getEditionISBN() { return(editionISBN); }
    public String getUserMail() { return(userMail); }
    public String getHypEndDate() {
        return(hypEndDate.toString());
    }
    public String getRealEndDate() {
        return(realEndDate.toString());
    }
    public Book getBook() throws SQLException {
        String querry = "SELECT * FROM Books b JOIN Edition e ON e.bookID=b.id WHERE e.isbn=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1, editionISBN);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Book book = new Book(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5));
            return(book);
        }

        return(null);
    }

    public void setEndDate(Date date) {
        realEndDate = date;
    }
}
