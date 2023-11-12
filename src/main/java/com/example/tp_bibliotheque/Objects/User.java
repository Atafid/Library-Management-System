package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.Controllers.UserViewController;
import com.example.tp_bibliotheque.MainApplication;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

//CLASSE REPRESENTANT UN UTILISATEUR -> PERSONNE ET PAGEOBJECT

public class User extends Personne implements PageObject {
    //*****************ATTRIBUTS*****************//

    //ID de l'utilisateur dans la BDD
    private final int id;

    //Mail de l'utilisateur
    private String mail;

    //Mot de passe hashé de l'utilisateur
    private String hashPassword;

    //Salt de l'utilisateur
    private final String passwordSalt;

    //Nombre d'emprunts en cours de l'utilisateur
    private int borrowCount;

    //Catégorie de l'utilisateur
    public Categorie categorie;

    //ID de la bilbliothèque de l'utilisateur
    public int libraryId;

    //*****************METHODES*****************//

    //Constructeur de classe
    public User(int _id, String _mail, String _name, String _last_name, String _hashPassword, String _passwordSalt, String catString, int _libraryId) {
        super(_name, _last_name);

        id = _id;
        mail = _mail;
        hashPassword = _hashPassword;
        passwordSalt = _passwordSalt;

        try {
            borrowCount = Emprunt.getCurrentEmpruntsFromUser(id).size();
        } catch(SQLException e) {
            System.out.println(e);
        }

        categorie = Categorie.getCatFromString(catString);
        libraryId = _libraryId;
    }

    //Méthode static permettant d'ajouter un utilisateur à la BDD
    public static void addUser(String mail, String name, String lastName, String password, String passwordSalt, String catString, int libraryId) throws SQLException {
        //Requête SQL permettant d'ajouter l'utilisateur
        String querry = "INSERT INTO User VALUES(0,?,?,?,?,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, mail);
        stmt.setString(2, name);
        stmt.setString(3, lastName);
        stmt.setString(4, password);
        stmt.setString(5, passwordSalt);
        stmt.setString(6, catString);
        stmt.setInt(7, libraryId);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static permettant de récupérer tous les utilisateurs
    public static Vector<PageObject> getAllUsers() throws SQLException {
        Vector<PageObject> res = new Vector<PageObject>();

        //Requête SQL récupérant tous les utilisateurs
        String querry = "SELECT * FROM User";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),rs.getString(6),rs.getString(7), rs.getInt(8));
            res.add(user);
        }

        return(res);
    }

    //Méthode static permettant de récupérer la variable d'un utilisateur à partir de son ID dans la BDD
    public static User getUserFromId(int id) throws SQLException {
        //Requête SQL récupérant les informations de l'utilisateur
        String querry = "SELECT * FROM User WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1,id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),rs.getString(6),rs.getString(7), rs.getInt(8));
            return(user);
        }

        return(null);
    }

    //Méthode static permettant d'obtenir l'utilisateur à partir de son mail
    public static User getUserFromMail(String mail) throws SQLException {
        //Requête SQL récupérant les informations de l'utilisateur
        String querry = "SELECT * FROM User WHERE mail=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1,mail);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),rs.getString(6),rs.getString(7), rs.getInt(8));
            return(user);
        }

        return(null);
    }

    //Méthode static permettant d'obtenir l'utilisateur à partir d'une demande de mouvement d'exemplaire
    public static User getUserFromMoveAsk(int _moveAskId) throws SQLException {
        //Requête SQL récupérant les informations de l'utilisateur
        String querry = "SELECT * FROM User u JOIN MoveASK m ON u.id=m.userId WHERE m.id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, _moveAskId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),rs.getString(6),rs.getString(7), rs.getInt(8));
            return(user);
        }

        return(null);
    }

    //Méthode permettant d'obtenir le tableau de tous les administrateurs
    public static Vector<User> getAdmin() throws SQLException {
        Vector<User> res = new Vector<User>();

        //Requête SQL récupérant les administrateurs
        String querry = "SELECT * FROM User WHERE categorie='B'";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),rs.getString(6),rs.getString(7), rs.getInt(8));
            res.add(user);
        }

        return(res);
    }

    //Méthode permettant de compter les emprunts en retard e l'utilisateur
    public int countLateBorrow() throws SQLException {
        //Tableau des emprunts en cours de l'utilisateur
        Vector<Emprunt> currentEmprunts = Emprunt.getCurrentEmpruntsFromUser(id);
        int res = 0;

        for(Emprunt e:currentEmprunts) {
            if(e.checkLateStatus()) {
                res++;
            }
        }

        return(res);
    }

    //GETTERS DE CLASSE
    public int getId() { return(id); }
    public String getMail() { return(mail); }
    public String getHashPassword() {
        return(hashPassword);
    }
    public String getPasswordSalt() {
        return(passwordSalt);
    }
    public Categorie getCategorie() { return(categorie); }
    public int getBorrowCount() { return(borrowCount); }
    public int getMaxBorrowCount() {
        return categorie.getMaxBorrowNumber();
    }
    public int getBorrowDays() {
        return categorie.getMaxDaysBorrow();
    }
    public int getLibraryId() { return libraryId; }

    //SETTERS DE CLASSE

    //Set un nouveau mail à l'utilisateur et dans la BDD
    public void setMail(String newMail) throws SQLException {
        String querry = "UPDATE User SET mail=? WHERE mail=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, newMail);
        stmt.setString(2, mail);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();

        mail = newMail;
    }

    //Set un nouveau mot de passe à l'utilisateur et dans la BDD
    public void setPassword(String newPassword) throws SQLException {
        String querry = "UPDATE User SET password=? WHERE mail=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, newPassword);
        stmt.setString(2, mail);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();

        hashPassword = newPassword;
    }
    public void setBorrowCount(int i) { borrowCount=i; }

    //Set une nouvelle catégorie à l'utilisateur et dans la BDD
    public void setCategorie(Categorie cat) throws SQLException {
        String querry = "UPDATE User SET categorie=? WHERE mail=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        String catStr;
        switch(cat) {
            case Cat1 -> catStr="1";
            case Cat2 -> catStr="2";
            case Cat3 -> catStr="3";
            case Bibliothécaire -> catStr="B";
            case Forbidden -> catStr="F";
            default -> catStr="1";
        }

        stmt.setString(1, catStr);
        stmt.setString(2, mail);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    //Méthode implémentant la méthode fillGrid du contrat PageObject pour remplir la grille d'une page d'utilisateur
    @Override
    public void fillGrid(GridPane grid, int rowIdx) {
        //Boutton permettant d'accéder à la view de l'utilisateur
        Button userButton = new Button();
        try {
            userButton.setText("- "+mail+", "+Library.getLibraryFromId(libraryId).getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        userButton.getStyleClass().add("user_button");

        UserViewController userController = new UserViewController(this);
        userButton.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "fxml/user-view.fxml", userController);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //Label du nombre d'emprunts de l'utilisateur
        Label empruntLabel = new Label();
        try {
            empruntLabel.setText(Emprunt.getCurrentEmpruntsFromUser(id).size()+" current borrows");

            if(countLateBorrow()>0) {
                empruntLabel.setText(empruntLabel.getText()+" "+countLateBorrow()+" Late");
                empruntLabel.getStyleClass().add("late");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //Non administrateur -> ChoiceBox pour pouvoir changer la catégorie
        if(!categorie.equals(Categorie.Bibliothécaire)) {
            //Initialisation des ChoiceBox
            ChoiceBox<Categorie> userCB = new ChoiceBox<Categorie>();
            userCB.getItems().addAll(Categorie.Cat1, Categorie.Cat2, Categorie.Cat3, Categorie.Forbidden);
            userCB.setValue(categorie);

            //Changement de catégorie lors d'un choix dans la ChoiceBox
            userCB.setOnAction((event) -> {
                try {
                    setCategorie(userCB.getSelectionModel().getSelectedItem());
                    CatChange.addCatChange(id, MainApplication.header.getUser().getId(), new Date(System.currentTimeMillis()), categorie, userCB.getSelectionModel().getSelectedItem());
                    Notification.addNotification(id, "C", new Date(System.currentTimeMillis()), CatChange.getIdFromBDD(id, MainApplication.header.getUser().getId(), new Date(System.currentTimeMillis()), categorie, userCB.getSelectionModel().getSelectedItem()));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            //Ajout à l'interface graphique
            grid.add(userCB, 2,rowIdx);
        }

        //Administrateur -> Label de catégorie
        else {
            Label bibliothecaireLabel = new Label("Librarian");

            //Ajout à l'interface graphique
            grid.add(bibliothecaireLabel, 2, rowIdx);
        }

        //Ajout à l'interface graphique
        grid.add(userButton, 0, rowIdx);
        grid.add(empruntLabel, 1, rowIdx);
    }
}
