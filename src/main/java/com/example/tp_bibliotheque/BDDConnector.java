package com.example.tp_bibliotheque;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//CLASSE CONNECTOR A LA BDD

public class BDDConnector {
    //*****************ATTRIBUTS*****************//

    //Connexion à la BDD
    public Connection con;


    //*****************METHODES*****************//

    //Constructeur de classe
    public BDDConnector() {
        try {
            //Initialisation de la connexion
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3308/bdd", "root", "05116173");
            con.setAutoCommit(false);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    //Méthode static permettant de clean un attribut, en retirant la 1e et la dernière lettre
    public static String cleanAttribute(String str) {
        return(str.substring(1, str.length()-1));
    }

    //Méthode static surchargée permettant de clean un attribut, avec un indice de début et un de fin
    public static String cleanAttribute(String str, int debIdx, int endIdx) {
        return(str.substring(debIdx, endIdx));
    }

    //Méthode utilisée pour remplir la BDD
    public void fillBDD() throws SQLException, IOException {
        //Requête SQL pour remplir les livres, éditions, auteurs et aEcrit
        String querBooks = "INSERT INTO Books VALUES(?,?,?,?,?)";
        String querEdition = "INSERT INTO Edition VALUES(?,?,?,?,?)";
        String querAuthors = "INSERT INTO Authors VALUES(0,?,?,?)";
        String querHasWritten = "INSERT INTO aEcrit VALUES(?,?,?)";

        PreparedStatement stmtBooks = con.prepareStatement(querBooks);
        PreparedStatement stmtEdition = con.prepareStatement(querEdition);
        PreparedStatement stmtAuthors = con.prepareStatement(querAuthors);
        PreparedStatement stmtHasWritten = con.prepareStatement(querHasWritten);

        String csvFilePath = "books.csv";

        //Taille des échantillons du fichier CSV avant de commit
        int BatchSize = 50;

        BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
        String lineText = null;

        //Compte des livres et des auteurs
        int count = 1;
        int countAuthors = 1;

        lineReader.readLine();

        //Limite du nombre de livres à 5000
        while((lineText = lineReader.readLine()) != null && count < 5000) {
            //Formule regex permettant de split les attributs séparés par des virgules, non contenues dans des parenthèses ou des crochets
            String[] data = lineText.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            //Toutes les data du livres sont remplies
            if (21 < data.length) {

                //Récupération des attributs des livres
                String title = cleanAttribute(data[1]);
                String description = cleanAttribute(data[5]);
                String genre = cleanAttribute(data[8]);
                String coverImg = cleanAttribute(data[21]);

                //Récupération des attributs des éditions
                String isbn = cleanAttribute(data[7]);
                //Si ISBN = 9999999999999 (indéfini) ou trop long, on place un isbn unique
                if(isbn.equals("9999999999999") || isbn.length()>45) {
                    isbn = "9780024406"+count;
                }
                String editorName = cleanAttribute(data[13]);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                Date publishDate;
                try {
                    publishDate = new Date(dateFormat.parse(cleanAttribute(data[14])).getTime());
                } catch (ParseException e) {
                    publishDate = new Date(System.currentTimeMillis());
                }


                stmtBooks.setInt(1, count);
                stmtBooks.setString(2, title);
                stmtBooks.setString(3, description);
                stmtBooks.setString(4, genre);
                stmtBooks.setString(5, coverImg);

                stmtEdition.setString(1, isbn);
                stmtEdition.setInt(2, count);
                stmtEdition.setString(3, editorName);
                stmtEdition.setDate(4, publishDate);
                stmtEdition.setInt(5, new Random().nextInt(3)+1);


                //Récupération des attributs des auteurs
                String roles = "";
                String authorsRaw = cleanAttribute(data[3]);
                String[] authors = authorsRaw.split(", ");
                for(int i=0;i<authors.length;i++) {
                    //Formule regex permettant de séparer les auteurs, en récupérant leur nom, prénom et rôle (entre parenthèse dans le CSV), en laisant un blanc si l'un
                    //ou plusieurs des champs est vide
                    String regex = "^(.*?)((?: \\([^)]+\\))*)$";
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(authors[i]);

                    if (matcher.find()) {
                        String names = matcher.group(1).trim();
                        roles = matcher.group(2).trim();

                        String[] nameParts = names.split(" ");
                        String firstName = nameParts.length > 0 ? nameParts[0] : "";
                        String lastName = nameParts.length > 1 ? nameParts[nameParts.length - 1] : "";

                        stmtAuthors.setString(1, firstName);
                        stmtAuthors.setString(2, lastName);
                    }

                    stmtAuthors.setDate(3, new Date(System.currentTimeMillis()));
                    stmtAuthors.addBatch();

                    //Si rôle non précisé, on le place en "Author"
                    if (roles.equals("")) {
                        roles = "(Author)";
                    }

                    stmtHasWritten.setInt(1, count);
                    stmtHasWritten.setInt(2, countAuthors);
                    stmtHasWritten.setString(3, roles);

                    stmtHasWritten.addBatch();

                    countAuthors++;
                }

                stmtBooks.addBatch();
                stmtEdition.addBatch();

                //Commit des changements
                if (count % BatchSize == 0) {
                    stmtBooks.executeBatch();
                    stmtEdition.executeBatch();
                    stmtAuthors.executeBatch();
                    stmtHasWritten.executeBatch();
                }

                count++;
            }
        }

        lineReader.close();

        //Commit final
        stmtBooks.executeBatch();
        stmtEdition.executeBatch();
        stmtAuthors.executeBatch();
        stmtHasWritten.executeBatch();

        con.commit();
    }
}
