package com.example.tp_bibliotheque;

public abstract class Personne {
    private String name;
    private String last_name;

    public Personne(String _name, String _last_name) {
        name = _name;
        last_name = _last_name;
    }

    public String getName() { return name; }

    public String getLastName() {
        return last_name;
    }
}
