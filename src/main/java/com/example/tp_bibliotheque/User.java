package com.example.tp_bibliotheque;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class User extends Personne {
    private final int id;
    private final String mail;
    private final String hashPassword;
    private final String passwordSalt;

    private int borrowCount;
    public Categorie categorie;

    public User(int _id, String _mail, String _name, String _last_name, String _hashPassword, String _passwordSalt, String catString) {
        super(_name, _last_name);

        id = _id;
        mail = _mail;
        hashPassword = _hashPassword;
        passwordSalt = _passwordSalt;

        try {
            borrowCount = Emprunt.getCurrentEmpruntFromUser(id).size();
        } catch(SQLException e) {
            System.out.println(e);
        }

        categorie = Categorie.getCatFromString(catString);
    }

    public static void addUser(String mail, String name, String lastName, String password, String passwordSalt, String catString) throws SQLException {
        String querry = "INSERT INTO User VALUES(0,?,?,?,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, mail);
        stmt.setString(2, name);
        stmt.setString(3, lastName);
        stmt.setString(4, password);
        stmt.setString(5, passwordSalt);
        stmt.setString(6, catString);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    public static Vector<User> getAllUsers() throws SQLException {
        Vector<User> res = new Vector<User>();

        String querry = "SELECT * FROM User";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),rs.getString(6),rs.getString(7));
            res.add(user);
        }

        return(res);
    }
    public static User getUserFromId(int id) throws SQLException {
        String querry = "SELECT * FROM User WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1,id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),rs.getString(6),rs.getString(7));
            return(user);
        }

        return(null);
    }
    public static User getUserFromMail(String mail) throws SQLException {
        String querry = "SELECT * FROM User WHERE mail=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1,mail);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),rs.getString(6),rs.getString(7));
            return(user);
        }

        return(null);
    }

    public int getId() { return(id); }
    public String getMail() { return(mail); }
    public String getHashPassword() {
        return(hashPassword);
    }
    public String getPasswordSalt() {
        return(passwordSalt);
    }
    public int getBorrowCount() { return(borrowCount); }
    public int getMaxBorrowCount() {
        return categorie.getMaxBorrowNumber();
    }
    public int getBorrowDays() {
        return categorie.getMaxDaysBorrow();
    }

    public void setBorrowCount(int i) { borrowCount=i; }
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

    public int countLateBorrow() throws SQLException {
        Vector<Emprunt> currentEmprunts = Emprunt.getCurrentEmpruntFromUser(id);
        int res = 0;

        for(Emprunt e:currentEmprunts) {
            if(e.checkLateStatus()) {
                res++;
            }
        }

        return(res);
    }
}
