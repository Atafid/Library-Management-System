package com.example.tp_bibliotheque.Objects;

//ENUM REPRESENTANT UNE CATEGORIE D'UTILISATEUR

public enum Categorie {
    //*****************VALEURS POSSIBLE*****************//
    Cat1(1, 15, "Cat1"),
    Cat2(5, 20, "Cat2"),
    Cat3(10,30, "Cat3"),
    Bibliothécaire(100, 100, "Bibliothécaire"),
    Forbidden(0,0, "Forbidden");


    //*****************ATTRIBUTS*****************//

    //Nombre maximal d'emprunts de la catégorie
    private final int maxBorrowNumber;
    //Nombre maximal de jours d'emprunts de la catégorie
    private final int maxDaysBorrow;
    //Nom de la catégorie
    private final String name;


    //*****************METHODES*****************//

    //Constructeur
    private Categorie(int _maxBorrowNumber, int _maxDaysBorrow, String _name) {
        maxBorrowNumber = _maxBorrowNumber;
        maxDaysBorrow = _maxDaysBorrow;
        name = _name;
    }

    //Méthode static permettant d'obtenir la catégorie depuis sa chaîne de caractère dans la BDD
    public static Categorie getCatFromString(String catStr) {
        switch(catStr) {
            case("1"):
                return(Cat1);
            case("2"):
                return(Cat2);
            case("3"):
                return(Cat3);
            case("B"):
                return(Bibliothécaire);
            case("F"):
                return(Forbidden);
            default:
                return(Cat1);
        }
    }

    //Méthode static permettant d'obtenir la chaîne de caractère à stocker dans la BDD depuis la catégorie
    public String getStringFromCat() {
        switch(name) {
            case("Cat1"):
                return("1");
            case("Cat2"):
                return("2");
            case("Cat3"):
                return("3");
            case("Bibliothécaire"):
                return("B");
            case("Forbidden"):
                return("F");
            default:
                return("1");
        }
    }

    //GETTERS
    public int getMaxBorrowNumber() {
        return maxBorrowNumber;
    }
    public int getMaxDaysBorrow() {
        return maxDaysBorrow;
    }
    public String getName() {
        return(name);
    }
}
