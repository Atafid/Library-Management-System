package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

enum NotifType {
    LateBook,
    CatChange,
    adminNotif;

    public String toString(int _infoId) throws SQLException {
        if (this.equals(NotifType.LateBook)) {
            return ("You are late for the book : "+Book.getBookFromEmprunt(_infoId));
        } else if (this.equals(NotifType.CatChange)) {
            return ("Your categorie changed from "+
                    com.example.tp_bibliotheque.Objects.CatChange.getCatChange(_infoId).getPrevCat().getName()+" to "+
                    com.example.tp_bibliotheque.Objects.CatChange.getCatChange(_infoId).getNewCat().getName());
        } else if (this.equals(NotifType.adminNotif)) {
            return ("The user "+User.getUserFromId(_infoId).getMail()+"has a book late");
        }
        return null;
    }
}

public class Notification {
    private int id;
    private int userId;
    private NotifType type;
    private Date date;
    private Boolean read;
    private int infoId;

    public Notification(int _id, int _userId, String _type, Date _date, Boolean _read, int _infoId) {
        id = _id;
        userId = _userId;
        type = stringToType(_type);
        date = _date;
        read = _read;
        infoId = _infoId;
    }

    public static void addNotification(int _userId, String _type, Date _date, int _infoId) throws SQLException {
        String querry = "INSERT INTO Notifications VALUES(0,?,?,?,false,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _userId);
        stmt.setString(2, _type);
        stmt.setDate(3, _date);
        stmt.setInt(4, _infoId);

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
    public static Vector<Notification> getUnreadNotification(int _userId) throws SQLException {
        Vector<Notification> res = new Vector<Notification>();

        String querry = "SELECT * FROM Notifications WHERE isRead=false AND userId=? ORDER BY date DESC LIMIT 10";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _userId);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            Notification notif = new Notification(rs.getInt(1), rs.getInt(2),
                    rs.getString(3), rs.getDate(4), rs.getBoolean(5), rs.getInt(6));
            res.add(notif);
        }

        return(res);
    }
    public static Vector<Notification> getNotifications(int _userId) throws SQLException {
        Vector<Notification> res = new Vector<Notification>();

        String querry = "SELECT * FROM Notifications WHERE userId=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _userId);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            Notification notif = new Notification(rs.getInt(1), rs.getInt(2),
                    rs.getString(3), rs.getDate(4), rs.getBoolean(5), rs.getInt(6));
            res.add(notif);
        }

        return(res);
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

    public int getId() { return id; }
    public NotifType getType() {
        return(type);
    }
    public Date getDate() {
        return(date);
    }
    public String getString() throws SQLException {
        return(this.type.toString(infoId));
    }
}
