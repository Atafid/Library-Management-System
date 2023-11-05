package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Reservation {
    private int id;
    private String isbn;
    private int userId;
    private Date date;
    private int place;

    public Reservation(int _id, String _isbn, int _userId, Date _date, int _place) {
        id = _id;
        isbn = _isbn;
        userId = _userId;
        date = _date;
        place = _place;
    }

    public static void addReservation(String _isbn, int _userId, Date _date, int _place) throws SQLException {
        String querry = "INSERT INTO Reservation VALUES(0,?,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, _isbn);
        stmt.setInt(2, _userId);
        stmt.setDate(3, _date);
        stmt.setInt(4, _place);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }
    public static Reservation getCurrentReservation(String _isbn, int _userId) throws SQLException {
        String quer = "SELECT * FROM Reservation WHERE isbn=? AND userId=? AND place>0";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setString(1,_isbn);
        dispStmt.setInt(2, _userId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Reservation reservation = new Reservation(rs.getInt(1), rs.getString(2),
                    rs.getInt(3), rs.getDate(4), rs.getInt(5));
            return(reservation);
        }

        return(null);
    }
    public static int getNumberReservation(String _isbn) throws SQLException {
        String quer = "SELECT COUNT(DISTINCT userId) FROM Reservation WHERE isbn=? AND place>0";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setString(1,_isbn);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            int res = rs.getInt(1);
            return(res);
        }

        return(-1);
    }
    public static void updatePlaces(String _isbn) throws SQLException {
        String querry = "UPDATE Reservation SET place=place-1 WHERE isbn=? AND place>0";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, _isbn);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }
    public static User getFirstUser(String _isbn) throws SQLException {
        String quer = "SELECT userId FROM Reservation WHERE isbn=? AND place=1";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setString(1,_isbn);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User fstUser = User.getUserFromId(rs.getInt(1));
            return(fstUser);
        }

        return(null);
    }

    public int getPlace() { return(place); }
}
