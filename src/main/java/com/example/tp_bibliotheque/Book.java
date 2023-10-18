package com.example.tp_bibliotheque;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class Book {
    private int id;
    private String title;
    private String coverImg;
    private String[] genres;
    private String description;
    private String publishDate;

    public Book(int _id, String _title, String _coverImg, String _genres, String _description, String _publishDate) {
        id = _id;
        title = _title;
        coverImg = _coverImg;
        genres = cleanGenres(_genres);
        description = _description;
        publishDate = _publishDate;
    }

    public static Book getBook(int id) throws SQLException {
        String querry = "SELECT * FROM Books WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1,id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Book book = new Book(rs.getInt(1), Book.cleanAttribute(rs.getString(2)), Book.cleanAttribute(rs.getString(12)),
                    Book.cleanAttribute(rs.getString(8)), Book.cleanAttribute(rs.getString(5)), Book.cleanAttribute(rs.getString(11)));
            return(book);
        }

        return(null);
    }

    public static String cleanAttribute(String str) {
        return(str.substring(1, str.length()-1));
    }
    public static String cleanAttribute(String str, int debIdx, int endIdx) {
        return(str.substring(debIdx, endIdx));
    }
    public static String[] cleanGenres(String genresTable) {
        //clean [] char
        genresTable = cleanAttribute(genresTable);

        String[] genresRaw = genresTable.split(",");
        //fst genre has no space before it
        genresRaw[0] = cleanAttribute(genresRaw[0]);
        for(int i=1;i<genresRaw.length;i++) {
            //begin at 2 bc genres have a space before '
            genresRaw[i] = cleanAttribute(genresRaw[i], 2, genresRaw[i].length()-1);
        }

        return(genresRaw);
    }

    public int getId() { return(id); }
    public String getTitle() {
        return title;
    }
    public String getCoverImg() {
        return coverImg;
    }
    public String[] getGenres() { return genres; }
    public String getDescription() { return description; }
    public String getPublishDate() { return publishDate; }
    public Boolean getBorrowedStatus() throws SQLException {
        return(Emprunt.getCurrentEmpruntFromBook(this) != null);
    }
}
