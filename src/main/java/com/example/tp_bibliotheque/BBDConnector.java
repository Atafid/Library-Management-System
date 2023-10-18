package com.example.tp_bibliotheque;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;

public class BBDConnector {
    private Connection con;

    public BBDConnector() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3308/bdd", "root", "05116173");
            con.setAutoCommit(false);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void fillBDD() throws SQLException, IOException {
        String sql = "INSERT INTO Books VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = con.prepareStatement(sql);

        String csvFilePath = "books.csv";

        int BatchSize = 50;

        BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
        String lineText = null;

        int count = 0;

        lineReader.readLine();

        while((lineText = lineReader.readLine()) != null) {
            String[] data = lineText.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            if (21 < data.length) {

                String title = data[1];
                String series = data[2];
                String author = data[3];
                String description = data[5];
                String language = data[6];
                String isbn = data[7];
                String genre = data[8];
                String edition = data[11];
                String page = data[12];
                String date = data[14];
                String coverImg = data[21];

                stmt.setInt(1, count);
                stmt.setString(2, title);
                stmt.setString(3, series);
                stmt.setString(4, author);
                stmt.setString(5, description);
                stmt.setString(6, language);
                stmt.setString(7, isbn);
                stmt.setString(8, genre);
                stmt.setString(9, edition);
                stmt.setString(10, page);
                stmt.setString(11, date);
                stmt.setString(12, coverImg);

                stmt.addBatch();

                if (count % BatchSize == 0) {
                    stmt.executeBatch();
                }

                count++;
            }
        }

        lineReader.close();

        stmt.executeBatch();

        con.commit();
    }

    public void addUser(String mail, String name, String password, String passwordSalt) throws SQLException {
        String querry = "INSERT INTO User VALUES(?,?,?,?)";
        PreparedStatement stmt = con.prepareStatement(querry);

        stmt.setString(1, mail);
        stmt.setString(2, name);
        stmt.setString(3, password);
        stmt.setString(4, passwordSalt);

        stmt.addBatch();
        stmt.executeBatch();
        con.commit();
    }

    public User getUser(String mail) throws SQLException {
        String querry = "SELECT * FROM User WHERE mail=?";
        PreparedStatement dispStmt = con.prepareStatement(querry);
        dispStmt.setString(1,mail);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
            return(user);
        }

        return(null);
    }

    public Book getBook(int id) throws SQLException {
        String querry = "SELECT * FROM Books WHERE id=?";
        PreparedStatement dispStmt = con.prepareStatement(querry);
        dispStmt.setInt(1,id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Book book = new Book(rs.getInt(1), Book.cleanAttribute(rs.getString(2)), Book.cleanAttribute(rs.getString(12)),
                    Book.cleanAttribute(rs.getString(5)));
            return(book);
        }

        return(null);
    }

    public void addEmprunt(Book book, User user, Date beginDate, Date endDate) throws SQLException {
        String querry = "INSERT INTO Emprunt VALUES(?,?,?,?,?)";
        PreparedStatement stmt = con.prepareStatement(querry);

        stmt.setInt(1, book.getId());
        stmt.setString(2, user.getMail());
        stmt.setDate(3, beginDate);
        stmt.setDate(4, endDate);
        stmt.setBoolean(5, false);

        stmt.addBatch();
        stmt.executeBatch();
        con.commit();
    }

    public Emprunt getCurrentEmpruntFromBook(Book book) throws SQLException {
        String querry = "SELECT * FROM Emprunt WHERE bookID=? AND isFinished=false";
        PreparedStatement dispStmt = con.prepareStatement(querry);
        dispStmt.setInt(1,book.getId());

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getString(2), rs.getDate(3), rs.getDate(4), rs.getBoolean(5));
            return(emprunt);
        }

        return(null);
    }
}
