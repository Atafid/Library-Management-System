package com.example.tp_bibliotheque.Objects;

//CLASSE REPRESENTANT LA RELATION ENTRE UN LIVRE ET SES AUTEURS

public class HasWritten {
    //*****************ATTRIBUTS*****************//

    //Livre pris en compte
    private final Book book;
    //Auteur pris en compte
    private final Author author;
    //Rôle de l'auteur par rapport au livre (écrivain, illustrateur, etc)
    private final String role;


    //*****************METHODES*****************//

    //Constructeur de classe
    public HasWritten(Book _book, Author _author, String _role) {
        book = _book;
        author = _author;
        role = _role;
    }

    //GETTERS DE CLASSE
    public Author getAuthor() {
        return author;
    }
    public String getRole() {
        return role;
    }
}
