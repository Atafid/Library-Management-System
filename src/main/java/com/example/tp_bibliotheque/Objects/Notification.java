package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

//ENUM REPRESENTANT LES TYPES DE NOTIFICATIONS

enum NotifType {
    //*****************VALEURS POSSIBLE*****************//
    LateBook,
    CatChange,
    AdminNotif,
    ReservationArrived,
    ForcedBookReturn;


    //*****************METHODES*****************//

    //String à afficher dans la notification en fonction du type de celle-ci
    public String toString(int _infoId) throws SQLException {
        if (this.equals(NotifType.LateBook)) {
            return ("You are late for the book : "+Book.getBookFromEmprunt(_infoId).getTitle());
        } else if (this.equals(NotifType.CatChange)) {
            return ("Your categorie changed from "+
                    com.example.tp_bibliotheque.Objects.CatChange.getCatChange(_infoId).getPrevCat().getName()+" to "+
                    com.example.tp_bibliotheque.Objects.CatChange.getCatChange(_infoId).getNewCat().getName());
        } else if (this.equals(NotifType.AdminNotif)) {
            return ("The user "+User.getUserFromId(_infoId).getMail()+"has a book late");
        } else if (this.equals(NotifType.ReservationArrived)) {
            return ("Your reservation arrived for the book : "+Book.getBook(_infoId).getTitle());
        } else if (this.equals(NotifType.ForcedBookReturn)) {
            return ("An admin return your borrow for the book : "+Book.getBookFromEmprunt(_infoId).getTitle());
        }
        return null;
    }
}


//CLASSE REPRESENTANT UNE NOTIFICATION -> PageObject

public class Notification implements PageObject {
    //*****************ATTRIBUTS*****************//

    //ID de la notification dans la BDD
    private final int id;

    //ID de l'utilisateur recevant la notification, dans la BDD
    private final int userId;

    //Type de la notification
    private final NotifType type;

    //Date de la notification
    private final Date date;

    //Statut de lecture de la notification
    private final Boolean read;

    //ID de l'information relative à la notification (ID du livre si notification de retard, ID du CatChange si notification de changement de catégorie, etc)
    private final int infoId;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Notification(int _id, int _userId, String _type, Date _date, Boolean _read, int _infoId) {
        id = _id;
        userId = _userId;
        type = stringToType(_type);
        date = _date;
        read = _read;
        infoId = _infoId;
    }

    //Méthode static permettant d'ajouter une notification à la BDD
    public static void addNotification(int _userId, String _type, Date _date, int _infoId) throws SQLException {
        //Requête SQL ajoutant la notification
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

    //Méthode static permettant de marquer une notification comme lue
    public static void readNotification(int _id) throws SQLException {
        //Requête SQL mettant à jour le statut de la notification
        String querry = "UPDATE Notifications SET isRead=true WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _id);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static récupérant toutes les notification non lues d'un utilisateur
    public static Vector<Notification> getUnreadNotification(int _userId) throws SQLException {
        Vector<Notification> res = new Vector<Notification>();

        //Requête SQL récupérant les notification
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

    //Méthode static permettant de récuperer toutes les notifications, lues ou non, d'un utilisateur
    public static Vector<PageObject> getNotifications(int _userId) throws SQLException {
        Vector<PageObject> res = new Vector<PageObject>();

        //Requête SQL récupérant les notifications
        String querry = "SELECT * FROM Notifications WHERE userId=? ORDER BY date DESC";
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

    //Méthode permettant de récuperer le type de notification à partir de sa String dans la BDD
    private NotifType stringToType(String type) {
        switch(type) {
            case "L":
                return(NotifType.LateBook);
            case "C":
                return(NotifType.CatChange);
            case "A":
                return(NotifType.AdminNotif);
            case "R":
                return(NotifType.ReservationArrived);
            case "F":
                return(NotifType.ForcedBookReturn);
            default:
                return(NotifType.LateBook);
        }
    }

    //GETTER DE CLASSE
    public int getId() { return id; }
    public NotifType getType() {
        return(type);
    }
    public Date getDate() {
        return(date);
    }
    public String getString() throws SQLException {
        return(this.type.toString(infoId)+", "+this.date);
    }

    //Méthode implémentant la méthode fillGrid du contrat PageObject pour remplir la grille d'une page de notifications
    @Override
    public void fillGrid(GridPane grid, int rowIdx) {
        //Label de la notification
        Label notifLabel = new Label();
        try {
            notifLabel.setText(this.getString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Ajout à l'interface graphique
        grid.add(notifLabel, 0, rowIdx);
    }
}
