package com.example.tp_bibliotheque.Objects;

public enum Categorie {
    Cat1(1, 15, "Cat1"),
    Cat2(5, 20, "Cat2"),
    Cat3(10,30, "Cat3"),
    Bibliothécaire(100, 100, "Bibliothécaire"),
    Forbidden(0,0, "Forbidden");

    private final int maxBorrowNumber;
    private final int maxDaysBorrow;
    private final String name;

    private Categorie(int _maxBorrowNumber, int _maxDaysBorrow, String _name) {
        maxBorrowNumber = _maxBorrowNumber;
        maxDaysBorrow = _maxDaysBorrow;
        name = _name;
    }

    public int getMaxBorrowNumber() {
        return maxBorrowNumber;
    }
    public int getMaxDaysBorrow() {
        return maxDaysBorrow;
    }
    public String getName() {
        return(name);
    }

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
}
