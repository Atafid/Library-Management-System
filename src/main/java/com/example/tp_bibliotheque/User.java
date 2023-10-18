package com.example.tp_bibliotheque;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    private String mail;
    private String name;
    private String hashPassword;
    private String passwordSalt;

    public User(String _mail, String _name, String _hashPassword, String _passwordSalt) {
        mail = _mail;
        name = _name;
        hashPassword = _hashPassword;
        passwordSalt = _passwordSalt;
    }

    public static void addUser(String mail, String name, String password, String passwordSalt) throws SQLException {
        String querry = "INSERT INTO User VALUES(?,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, mail);
        stmt.setString(2, name);
        stmt.setString(3, password);
        stmt.setString(4, passwordSalt);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    public static User getUser(String mail) throws SQLException {
        String querry = "SELECT * FROM User WHERE mail=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1,mail);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            User user = new User(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
            return(user);
        }

        return(null);
    }

    public String getMail() { return(mail); }
    public String getName() {
        return(name);
    }
    public String getHashPassword() {
        return(hashPassword);
    }
    public String getPasswordSalt() {
        return(passwordSalt);
    }

}
