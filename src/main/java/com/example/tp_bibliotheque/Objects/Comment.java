package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Comment {
    private int id;
    private int userId;
    private int bookId;
    private Date date;
    private int note;
    private String content;

    public Comment(int _id, int _userId, int _bookId, Date _date, int _note, String _content) {
        id = _id;
        userId = _userId;
        bookId = _bookId;
        date = _date;
        note = _note;
        content = _content;
    }

    public static void addComment(int _userId, int _bookId, Date _date, int _note, String _content) throws SQLException {
        String querry = "INSERT INTO Comment VALUES(0,?,?,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _userId);
        stmt.setInt(2, _bookId);
        stmt.setDate(3, _date);
        stmt.setInt(4, _note);
        stmt.setString(5, _content);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }
    public static Vector<Comment> getComment(int _bookId) throws SQLException {
        Vector<Comment> res = new Vector<Comment>();

        String querry = "SELECT * FROM Comment WHERE bookId=? ORDER BY date DESC";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, _bookId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Comment comment = new Comment(rs.getInt(1),rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getInt(5), rs.getString(6));
            res.add(comment);
        }

        return(res);
    }

    public int getUserId() { return userId; }
    public int getNote() { return note; }
    public String getContent() { return content; }
    public Date getDate() { return date; }
}
