package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class MoveAsk {
    private final int id;
    private final int userId;
    private final int printedWorkId;
    private final Date date;
    private Boolean isRead;
    private Boolean isAccepted;

    public MoveAsk(int _id, int _userId, int _printedWorkId, Date _date, Boolean _isRead, Boolean _isAccepted) {
        id = _id;
        userId = _userId;
        printedWorkId = _printedWorkId;
        date = _date;
        isRead = _isRead;
        isAccepted = _isAccepted;
    }

    public static void addMoveAsk(int _userId, int _printedWorkId, Date _date) throws SQLException {
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

    public static void answerMoveAsk(int _id, Boolean answer) throws SQLException {
        String querry = "UPDATE MoveAsk SET isAccepted=?, isRead=true WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setBoolean(1, answer);
        stmt.setInt(2, _id);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    public static MoveAsk getCurrentMoveAskFromUserAndBook(int _userId, int _printedWorkId) throws SQLException {
        //Requête SQL récupérant les emprunts
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

    public static String getAnswer(int _id) throws SQLException {
        String querry = "SELECT isAccepted FROM MoveAsk WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        dispStmt.setInt(1, _id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            if(rs.getBoolean(1)) { return("accepted"); }
            else { return("rejected"); }
        }

        return("");
    }

    public static Boolean getRead(int _id) throws SQLException {
        String querry = "SELECT isAccepted FROM MoveAsk WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        dispStmt.setInt(1, _id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            return(rs.getBoolean(1));
        }

        return(false);
    }

    public static int getIdFromBDD(int _userId, int _printedWorkId, Date _date) throws SQLException {
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
