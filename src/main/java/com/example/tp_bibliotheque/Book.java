package com.example.tp_bibliotheque;

import java.sql.SQLException;

public class Book {
    private int id;
    private String title;
    private String coverImg;
    private String description;

    public Book(int _id, String _title, String _coverImg, String _description) {
        id = _id;
        title = _title;
        coverImg = _coverImg;
        description = _description;
    }

    public static String cleanAttribute(String str) {
        return(str.substring(1, str.length()-1));
    }

    public int getId() { return(id); }
    public String getTitle() {
        return title;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public String getDescription() { return description; }

    public Boolean getBorrowedStatus() throws SQLException {
        return(MainApplication.bddConn.getCurrentEmpruntFromBook(this) != null);
    }
}
