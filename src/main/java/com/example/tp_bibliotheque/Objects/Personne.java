package com.example.tp_bibliotheque.Objects;

//CLASSE REPRESENTANT UNE PERSONNE (UTILISATEUR, AUTEUR, ETC)

public abstract class Personne {
    //*****************ATTRIBUTS*****************//

    //Prénom de la personne
    private final String name;

    //Nom de famille de la personne
    private final String last_name;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Personne(String _name, String _last_name) {
        name = _name;
        last_name = _last_name;
    }

    //GETTERS DE CLASSE
    public String getName() { return name; }
    public String getLastName() {
        return last_name;
    }
}
