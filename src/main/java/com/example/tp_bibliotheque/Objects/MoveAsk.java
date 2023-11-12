package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

//CLASSE REPRESENTANT UNE DEMANDE DE MOUVEMENT DE LIVRE

public class MoveAsk {
    //*****************ATTRIBUTS*****************//

    //ID de la demande dans la BDD
    private final int id;

    //ID de l'utilisateur à l'origine de la demande dans la BDD
    private final int userId;

    //ID de l'exemplaire concerné dans la BDD
    private final int printedWorkId;

    //Date de la demande
    private final Date date;

    //Statut de lecture de la demande
    private Boolean isRead;

    //Statut d'acceptation de la demande
    private Boolean isAccepted;


    //*****************METHODES*****************//

    //Constructeur de classe
    public MoveAsk(int _id, int _userId, int _printedWorkId, Date _date, Boolean _isRead, Boolean _isAccepted) {
        id = _id;
        userId = _userId;
        printedWorkId = _printedWorkId;
        date = _date;
        isRead = _isRead;
        isAccepted = _isAccepted;
    }

    //Méthode static permettant d'ajouter une demande à la BDD
    public static void addMoveAsk(int _userId, int _printedWorkId, Date _date) throws SQLException {
        //Requête SQL ajoutant la demande à la BDD
        String querry = "INSERT INTO MoveAsk VALUES(0,?,?,?,?,NULL)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _userId);
        stmt.setInt(2, _printedWorkId);
        stmt.setDate(3, _date);
        stmt.setBoolean(4, false);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static permettant de répondre à une demande
    public static void answerMoveAsk(int _id, Boolean answer) throws SQLException {
        //Requête SQL permettant de répondre à la demande
        String querry = "UPDATE MoveAsk SET isAccepted=?, isRead=true WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setBoolean(1, answer);
        stmt.setInt(2, _id);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static permettant d'obtenir la demande en cours d'un utilisateur et d'un exemplaire donné
    public static MoveAsk getCurrentMoveAskFromUserAndBook(int _userId, int _printedWorkId) throws SQLException {
        //Requête SQL récupérant la demande
        String querry = "SELECT * FROM MoveAsk WHERE userId=? AND printedWorkID=? AND isRead=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        dispStmt.setInt(1, _userId);
        dispStmt.setInt(2, _printedWorkId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            MoveAsk moveAsk = new MoveAsk(rs.getInt(1), rs.getInt(2), rs.getInt(3),
                    rs.getDate(4), rs.getBoolean(5), rs.getBoolean(6));
            return(moveAsk);
        }

        return(null);
    }

    //Méthode static permettant d'obtenir la réponse à une demande
    public static String getAnswer(int _id) throws SQLException {
        //Requête SQL récupérant le statut d'acceptation de la demande
        String querry = "SELECT isAccepted FROM MoveAsk WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        dispStmt.setInt(1, _id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            //Conversion du statut en String
            if(rs.getBoolean(1)) { return("accepted"); }
            else { return("rejected"); }
        }

        return("");
    }

    //Méthode static permettant d'obtenir le statut de lecture d'une demande
    public static Boolean getRead(int _id) throws SQLException {
        //Requête SQL récupérant le statut de lecture
        String querry = "SELECT isAccepted FROM MoveAsk WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        dispStmt.setInt(1, _id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            return(rs.getBoolean(1));
        }

        return(false);
    }

    //Méthode static permettant d'obtenir l'ID d'une demande à partir de ses informations
    public static int getIdFromBDD(int _userId, int _printedWorkId, Date _date) throws SQLException {
        //Requête SQL récupérant l'ID
        String querry = "SELECT id FROM MoveAsk WHERE userId=? AND printedWorkID=? AND date=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        dispStmt.setInt(1, _userId);
        dispStmt.setInt(2, _printedWorkId);
        dispStmt.setDate(3, _date);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            return(rs.getInt(1));
        }

        return(-1);
    }
}
