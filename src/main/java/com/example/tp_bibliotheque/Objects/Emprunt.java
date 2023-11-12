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

//CLASSE REPRESENTANT UN EMPRUNT -> PageObject

public class Emprunt implements PageObject {
    //*****************ATTRIBUTS*****************//

    //ID de l'emprunt dans la BDD
    private final int id;

    //ID de l'oeuvre imprimée relative à l'emprunt, dans la BDD
    private final int printedWorkId;

    //ID de l'utilisateur relatif à l'emprunt, dans la BDD
    private final int userId;

    //Date du début de l'emprunt
    private final Date beginDate;

    //Date de fin de l'emprunt hypothétique
    private final Date hypEndDate;

    //Date de fin réelle de l'emprunt
    private Date realEndDate;

    //Statut de finition de l'emprunt
    private final boolean isFinished;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Emprunt(int _id, int _printedWorkId, int _userId, Date _beginDate, Date _endDate, boolean _isFinished) {
        id = _id;
        printedWorkId = _printedWorkId;
        userId = _userId;
        beginDate = _beginDate;
        hypEndDate = _endDate;
        isFinished = _isFinished;
    }

    //Constructeur de classe surchargé, pour construire un emprunt dont la date de fin réelle est connue
    public Emprunt(int _id, int _printedWorkId, int _userId, Date _beginDate, Date _endDate, Date _realEndDate, boolean _isFinished) {
        id = _id;
        printedWorkId = _printedWorkId;
        userId = _userId;
        beginDate = _beginDate;
        hypEndDate = _endDate;
        realEndDate = _realEndDate;
        isFinished = _isFinished;
    }

    //Méthode static permettant d'ajouter un emprunt à la BDD
    public static void addEmprunt(int printedWorkId, int userId, Date beginDate, Date endDate) throws SQLException {
        //Requête SQL ajoutant l'emprunt à la BDD
        String querry = "INSERT INTO Emprunt VALUES(0,?,?,?,?,NULL,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, printedWorkId);
        stmt.setInt(2, userId);
        stmt.setDate(3, beginDate);
        stmt.setDate(4, endDate);
        stmt.setBoolean(5, false);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static permettant de récupérer l'emprunt en cours d'un utilisateur et d'une édition donnée, renvoie null si pas d'emprunt en cours
    public static Emprunt getCurrentEmprunt(int printedWorkId, int userId) throws SQLException {
        //Requête SQL récupérant l'emprunt
        String querry = "SELECT * FROM Emprunt WHERE userId=? AND printedWorkID=? AND isFinished=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, userId);
        dispStmt.setInt(2, printedWorkId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), rs.getDate(6), rs.getBoolean(7));
            return(emprunt);
        }

        return(null);
    }

    //Méthode static permettant de récupérer tous les emprunts en cours d'une édition donnée
    public static Vector<Emprunt> getCurrentEmpruntsFromPrintedWork(int printedWorkId) throws SQLException {
        Vector<Emprunt> res = new Vector<Emprunt>();

        //Requête SQL récupérant les emprunts
        String querry = "SELECT * FROM Emprunt WHERE printedWorkID=? AND isFinished=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, printedWorkId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), rs.getBoolean(6));
            res.add(emprunt);
        }

        return(res);
    }

    //Méthode static permettant de récupérer tous les emprunts, finis ou non, d'un utilisateur
    public static Vector<PageObject> getAllEmpruntsFromUser(int userId) throws SQLException {
        Vector<PageObject> res = new Vector<PageObject>();

        //Requête SQL récupérant les emprunts
        String querry = "SELECT * FROM Emprunt WHERE userId=? ORDER BY isFinished ASC";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, userId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), rs.getDate(6), rs.getBoolean(7));
            res.add(emprunt);
        }

        return(res);
    }

    //Méthode static permettant de récupérer tous les emprunts en cours d'un utilisateur
    public static Vector<Emprunt> getCurrentEmpruntsFromUser(int userId) throws SQLException {
        Vector<Emprunt> res = new Vector<Emprunt>();

        //Requête SQL récupérant les emprunts
        String querry = "SELECT * FROM Emprunt WHERE userId=? AND isFinished=false";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, userId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Emprunt emprunt = new Emprunt(rs.getInt(1), rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getDate(5), rs.getBoolean(6));
            res.add(emprunt);
        }

        return(res);
    }

    //Méthode permettant de marqué un emprunt comme terminé
    public static void finishEmprunt(int id, Date date) throws SQLException {
        //Requête SQL terminant l'emprunt
        String querry = "UPDATE Emprunt SET isFinished=true, realEnddate=? WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setDate(1, date);
        stmt.setInt(2, id);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    //Méthode permettant de récupérer la variable du livre relatif à l'emprunt considéré
    public Book getBook() throws SQLException {
        //Requête SQL récupérant les informations du livre
        String querry = "SELECT * FROM Books b JOIN Edition e ON e.bookID=b.id JOIN PrintedWork p ON p.editionIsbn=e.isbn WHERE p.id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, printedWorkId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Book book = new Book(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5));
            return(book);
        }

        return(null);
    }

    //Méthode vérifiant si l'emprunt est en retard
    public Boolean checkLateStatus() {
        return(new Date(System.currentTimeMillis()).after(hypEndDate) && !isFinished);
    }

    //Méthode vérifiant si le retard de l'emprunt a été notifié
    public Boolean isNotified() throws SQLException {
        Vector<Notification> res = new Vector<Notification>();

        //Requête SQL récupérant les notification relatives à l'emprunt
        String querry = "SELECT * FROM Notifications WHERE infoId=? AND type='L'";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, id);

        ResultSet rs = stmt.executeQuery();

        //Notifications relatives à l'emprunt non vides -> retard notifié
        while(rs.next()) {
            return(true);
        }

        //Notifications relatives à l'emprunt vides -> retard non notifié
        return(false);
    }

    //GETTERS DE CLASSE
    public int getId() { return(id); }
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

    //SETTERS DE CLASSE
    public void setEndDate(Date date) {
        realEndDate = date;
    }

    //Méthode implémentant la méthode fillGrid du contrat PageObject pour remplir la grille d'une page d'emprunts
    @Override
    public void fillGrid(GridPane grid, int rowIdx) {
        //EMPRUNT EN COURS
        if(!isFinished) {
            //Récupération du livre relatif à l'emprunt
            Book empruntBook = null;
            try {
                empruntBook = this.getBook();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            //Boutton permettant d'accéder à la view du livre
            Button empruntButton = new Button();
            empruntButton.setText(empruntBook.getTitle());
            empruntButton.getStyleClass().add("emprunt_button");

            BookViewController bookController = new BookViewController(empruntBook);
            empruntButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "fxml/book-view.fxml", bookController);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });


            //Label du statut de l'emprunt
            Label empruntLabel = new Label();
            empruntLabel.setText(", until : "+getStringHypEndDate());

            if(checkLateStatus()) {
                empruntLabel.setText(empruntLabel.getText()+" LATE!");
            }


            //Boutton de retour de l'emprunt
            Button returnButton = new Button();
            returnButton.setText("Return");
            //Appui sur le boutton de retour -> retour de l'emprunt
            returnButton.setOnAction(event -> {
                try {
                    Date currentDate = new Date(System.currentTimeMillis());

                    //Retour de l'emprunt
                    PrintedWork.getPrintedWorkFromEmprunt(id).onClickReturn(id);

                    //Mise à jour de l'interface graphique
                    returnButton.setVisible(false);
                    empruntLabel.setText(", finished since : "+currentDate);

                    //Notification spécifique si un administrateur a forcé le retour d'un emprunt
                    if(MainApplication.header.getUser().categorie.equals(Categorie.Bibliothécaire)) {
                        Notification.addNotification(userId, "F", currentDate, id);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Ajout à l'interface graphique
            grid.add(empruntButton, 0, rowIdx);
            grid.add(empruntLabel, 1, rowIdx);
            grid.add(returnButton, 2, rowIdx);
        }

        //EMPRUNT TERMINE
        else {
            //Récupération du livre relatif à l'emprunt
            Book empruntBook = null;
            try {
                empruntBook = getBook();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            //Boutton permettant d'accéder à la view du livre
            Button empruntButton = new Button();
            empruntButton.setText(empruntBook.getTitle());
            empruntButton.getStyleClass().add("emprunt_button");

            BookViewController bookController = new BookViewController(empruntBook);
            empruntButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "fxml/book-view.fxml", bookController);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });


            //Label de statut de l'emprunt
            Label empruntLabel = new Label();
            empruntLabel.setText(", finished since : "+getStringRealEndDate());

            //Mise à jour de l'interface graphique
            grid.add(empruntButton, 0, rowIdx);
            grid.add(empruntLabel, 1, rowIdx);
        }
    }
}
