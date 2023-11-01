package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.Controllers.BookViewController;
import com.example.tp_bibliotheque.MainApplication;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Emprunt implements PageObject {
    private int id;
    private String editionISBN;
    private int userId;
    private Date beginDate;
    private Date hypEndDate;
    private Date realEndDate;
    private boolean isFinished;

    public Emprunt(int _id, String _editionISBN, int _userId, Date _beginDate, Date _endDate, boolean _isFinished) {
        id = _id;
        editionISBN = _editionISBN;
        userId = _userId;
        beginDate = _beginDate;
        hypEndDate = _endDate;
        isFinished = _isFinished;
    }
    public Emprunt(int _id, String _editionISBN, int _userId, Date _beginDate, Date _endDate, Date _realEndDate, boolean _isFinished) {
        id = _id;
        editionISBN = _editionISBN;
        userId = _userId;
        beginDate = _beginDate;
        hypEndDate = _endDate;
        realEndDate = _realEndDate;
        isFinished = _isFinished;
    }

    public static void addEmprunt(String editionIsbn, int userId, Date beginDate, Date endDate) throws SQLException {
        String querry = "INSERT INTO Emprunt VALUES(0,?,?,?,?,NULL,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, editionIsbn);
        stmt.setInt(2, userId);
        stmt.setDate(3, beginDate);
        stmt.setDate(4, endDate);
        stmt.setBoolean(5, false);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }
    public static Emprunt getCurrentEmprunt(String isbn, int userId) throws SQLException {
        String querry = "SELECT * FROM Emprunt WHERE userId=? AND editionISBN=? AND isFinished=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, userId);
        dispStmt.setString(2,isbn);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), rs.getDate(6), rs.getBoolean(7));
            return(emprunt);
        }

        return(null);
    }
    public static Vector<Emprunt> getCurrentEmpruntsFromEdition(String editionISBN) throws SQLException {
        Vector<Emprunt> res = new Vector<Emprunt>();

        String querry = "SELECT * FROM Emprunt WHERE editionISBN=? AND isFinished=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1, editionISBN);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), rs.getBoolean(6));
            res.add(emprunt);
        }

        return(res);
    }
    public static Vector<PageObject> getAllEmpruntsFromUser(int userId) throws SQLException {
        Vector<PageObject> res = new Vector<PageObject>();

        String querry = "SELECT * FROM Emprunt WHERE userId=? ORDER BY isFinished ASC";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, userId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), rs.getDate(6), rs.getBoolean(7));
            res.add(emprunt);
        }

        return(res);
    }
    public static Vector<Emprunt> getCurrentEmpruntsFromUser(int userId) throws SQLException {
        Vector<Emprunt> res = new Vector<Emprunt>();

        String querry = "SELECT * FROM Emprunt WHERE userId=? AND isFinished=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, userId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), rs.getBoolean(6));
            res.add(emprunt);
        }

        return(res);
    }
    public static void finishEmprunt(int id, Date date) throws SQLException {
        String querry = "UPDATE Emprunt SET isFinished=true, realEnddate=? WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setDate(1, date);
        stmt.setInt(2, id);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    public int getId() { return(id); }
    public String getEditionISBN() { return(editionISBN); }
    public int getUserId() { return(userId); }
    public String getUserMail() throws SQLException {
        return(User.getUserFromId(userId).getMail());
    }
    public String getStringHypEndDate() {
        return(hypEndDate.toString());
    }
    public Date getHypEndDate() { return(hypEndDate); }
    public String getStringRealEndDate() {
        return(realEndDate.toString());
    }
    public Book getBook() throws SQLException {
        String querry = "SELECT * FROM Books b JOIN Edition e ON e.bookID=b.id WHERE e.isbn=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1, editionISBN);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Book book = new Book(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5));
            return(book);
        }

        return(null);
    }
    public Boolean checkLateStatus() {
        return(new Date(System.currentTimeMillis()).after(hypEndDate) && !isFinished);
    }
    public Boolean isNotified() throws SQLException {
        Vector<Notification> res = new Vector<Notification>();

        String querry = "SELECT * FROM Notifications WHERE infoId=? AND type='L'";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            return(true);
        }

        return(false);
    }

    public void setEndDate(Date date) {
        realEndDate = date;
    }

    @Override
    public void fillGrid(GridPane grid, int rowIdx) {
        if(!isFinished) {
            Book empruntBook = null;
            try {
                empruntBook = this.getBook();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Button empruntButton = new Button();
            empruntButton.setText(empruntBook.getTitle()+" "+editionISBN);
            empruntButton.getStyleClass().add("emprunt_button");

            Label empruntLabel = new Label();
            empruntLabel.setText(", until : "+getStringHypEndDate());

            if(checkLateStatus()) {
                empruntLabel.setText(empruntLabel.getText()+" LATE!");
            }

            BookViewController bookController = new BookViewController(empruntBook);
            empruntButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "fxml/book-view.fxml", bookController);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            grid.add(empruntButton, 0, rowIdx);
            grid.add(empruntLabel, 1, rowIdx);
        }

        else {
            Book empruntBook = null;
            try {
                empruntBook = getBook();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            Button empruntButton = new Button();
            empruntButton.setText(empruntBook.getTitle()+" "+editionISBN);
            empruntButton.getStyleClass().add("emprunt_button");

            Label empruntLabel = new Label();
            empruntLabel.setText(", finished since : "+getStringRealEndDate());

            BookViewController bookController = new BookViewController(empruntBook);
            empruntButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "fxml/book-view.fxml", bookController);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            grid.add(empruntButton, 0, rowIdx);
            grid.add(empruntLabel, 1, rowIdx);
        }
    }
}
