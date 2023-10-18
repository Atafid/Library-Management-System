package com.example.tp_bibliotheque;

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
