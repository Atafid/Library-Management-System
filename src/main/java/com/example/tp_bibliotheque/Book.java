package com.example.tp_bibliotheque;

import javafx.scene.image.Image;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Book {
    private int id;
    private String title;
    private String coverImgUrl;
    private Image coverImg;
    private String[] genres;
    private String description;

    public Book(int _id, String _title, String _description, String _genres, String _coverImg) {
        id = _id;
        title = _title;
        coverImgUrl = _coverImg;
        coverImg = new Image(coverImgUrl);
        genres = cleanGenres(_genres);
        description = _description;
    }

    public static Book getBook(int id) throws SQLException {
        String querry = "SELECT * FROM Books WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1,id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Book book = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5));
            return(book);
        }

        return(null);
    }
    public static Vector<Book> getPage(int pageNumber) {
        Vector<Book> res = new Vector<Book>();

        for(int i=(pageNumber-1)*10+1;i<(pageNumber-1)*10+11;i++){
            try {
                res.add(Book.getBook(i));
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        return(res);
    }

    public static String[] cleanGenres(String genresTable) {
        //clean [] char
        genresTable = BDDConnector.cleanAttribute(genresTable);

        String[] genresRaw = genresTable.split(",");
        //fst genre has no space before it
        genresRaw[0] = BDDConnector.cleanAttribute(genresRaw[0]);
        for(int i=1;i<genresRaw.length;i++) {
            //begin at 2 bc genres have a space before '
            genresRaw[i] = BDDConnector.cleanAttribute(genresRaw[i], 2, genresRaw[i].length()-1);
        }

        return(genresRaw);
    }

    public int getId() { return(id); }
    public String getTitle() {
        return title;
    }
    public String getCoverImgUrl() {
        return coverImgUrl;
    }
    public Image getCoverImg() { return coverImg; }
    public String[] getGenres() { return genres; }
    public String getDescription() { return description; }
    public Vector<HasWritten> getCredits() throws SQLException {
        Vector<HasWritten> res = new Vector<HasWritten>();

        String quer = "SELECT a.id, a.name, a.last_name, a.birth, e.role FROM Authors a JOIN aEcrit e ON a.id = e.authorID WHERE e.bookID=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,this.getId());

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Author newAuthor = new Author(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4));
            HasWritten newHasWritten = new HasWritten(this, newAuthor, rs.getString(5));

            res.add(newHasWritten);
        }

        return(res);
    }
    public Vector<Edition> getEditions() throws SQLException {
        Vector<Edition> res = new Vector<Edition>();

        String quer = "SELECT * FROM Edition e JOIN Books b ON b.id = e.bookID WHERE e.bookID=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,this.getId());

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Edition newEdition = new Edition(rs.getString(1), rs.getString(3), rs.getDate(4), rs.getInt(5));
            res.add(newEdition);
        }

        return(res);
    }
    public double getAverageNote() throws SQLException {
        double res = 0.;
        int count = 0;

        String quer = "SELECT note FROM Comment WHERE bookID=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,this.getId());

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            res += rs.getInt(1);
            count++;
        }

        if(count==0) {
            return(-1);
        }
        return(res/count);
    }
}
