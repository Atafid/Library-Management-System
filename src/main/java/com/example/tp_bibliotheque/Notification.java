package com.example.tp_bibliotheque;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

enum NotifType {
    LateBook,
    CatChange,
    adminNotif;
}

public class Notification {
    private int id;
    private int userId;
    private NotifType type;
    private Date date;
    private Boolean read;

    public Notification(int _id, int _userId, String _type, Date _date, Boolean _read) {
        id = _id;
        userId = _userId;
        type = stringToType(_type);
        date = _date;
        read = _read;
    }

    public static void addNotification(int _userId, String _type, Date _date) throws SQLException {
        String querry = "INSERT INTO Notifications VALUES(0,?,?,?,false)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _userId);
        stmt.setString(2, _type);
        stmt.setDate(3, _date);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }
    public static void readNotification(int _id) throws SQLException {
        String querry = "UPDATE Notifications SET isRead=true WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _id);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }
    public static void getUnreadNotification(int _userId) {

    }

    private NotifType stringToType(String type) {
        switch(type) {
            case "L":
                return(NotifType.LateBook);
            case "C":
                return(NotifType.CatChange);
            case "A":
                return(NotifType.adminNotif);
            default:
                return(NotifType.LateBook);
        }
    }
}
