package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//CLASSE REPRESENTANT UNE RESERVATION

public class Reservation {
    //*****************ATTRIBUTS*****************//

    //ID de la réservation dans la BDD
    private final int id;

    //ISBN de l'édition relative à la réservation
    private final int printWorkId;

    //ID de l'utilisateur relatif à la réservation, dans la BDD
    private final int userId;

    //Date de la réservation
    private final Date date;

    //Place de l'utilisateur dans la file d'attente
    private final int place;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Reservation(int _id, int _printWorkId, int _userId, Date _date, int _place) {
        id = _id;
        printWorkId = _printWorkId;
        userId = _userId;
        date = _date;
        place = _place;
    }

    //Méthode static permettant d'ajouter une réservation à la BDD
    public static void addReservation(int _printWorkId, int _userId, Date _date, int _place) throws SQLException {
        //Requête SQL ajout la réservation
        String querry = "INSERT INTO Reservation VALUES(0,?,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _printWorkId);
        stmt.setInt(2, _userId);
        stmt.setDate(3, _date);
        stmt.setInt(4, _place);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static permettant de récupérer la réservation en cours pour une édition et un utilisateur donnés, renvoie null si aucune réservation
    public static Reservation getCurrentReservation(int _printWorkId, int _userId) throws SQLException {
        //Requête SQL récupérant la réservation
        String quer = "SELECT * FROM Reservation WHERE printedWorkID=? AND userId=? AND place>0";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,_printWorkId);
        dispStmt.setInt(2, _userId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Reservation reservation = new Reservation(rs.getInt(1), rs.getInt(2),
                    rs.getInt(3), rs.getDate(4), rs.getInt(5));
            return(reservation);
        }

        return(null);
    }

    //Méthode static permettant de récupérer le nombre de personne ayant une réservation en cours pour une édition donnée, renvoie -1 si aucune réservation
    public static int getNumberReservation(int _printWorkId) throws SQLException {
        //Requête SQL récupérant le nombre voulu
        String quer = "SELECT COUNT(DISTINCT userId) FROM Reservation WHERE printedWorkID=? AND place>0";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,_printWorkId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            int res = rs.getInt(1);
            return(res);
        }

        return(-1);
    }

    //Méthode static permettant de mettre à jour la file d'attente des réservations d'une édition, en réduisant les places de 1
    public static void updatePlaces(int _printWorkId) throws SQLException {
        //Rquête SQL mettant à jour les informations
        String querry = "UPDATE Reservation SET place=place-1 WHERE printedWorkID=? AND place>0";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _printWorkId);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static permettant de récupérer l'utilisateur premier dans la file d'attente d'une réservation
    public static User getFirstUser(int _printWorkId) throws SQLException {
        //Requête SQL permettant de récupérer l'utilisateur
        String quer = "SELECT userId FROM Reservation WHERE printedWorkID=? AND place=1";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,_printWorkId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User fstUser = User.getUserFromId(rs.getInt(1));
            return(fstUser);
        }

        return(null);
    }

    //GETTERS DE CLASSE
    public int getPlace() { return(place); }
}
