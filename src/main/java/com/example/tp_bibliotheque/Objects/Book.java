package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.BDDConnector;
import com.example.tp_bibliotheque.Controllers.BookViewController;
import com.example.tp_bibliotheque.MainApplication;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

//CLASSE REPRESENTANT UN LIVRE -> PageObject

public class Book implements PageObject {
    //*****************ATTRIBUTS*****************//

    //ID du livre dans la BDD
    private final int id;

    //Titre du livre
    private final String title;

    //Url de l'image de la couverture du livre
    private final String coverImgUrl;

    //Image de la couverture du livre
    private final Image coverImg;

    //Tableaux des différents genres du livre
    private final String[] genres;

    //Description du livre
    private final String description;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Book(int _id, String _title, String _description, String _genres, String _coverImg) {
        id = _id;
        title = _title;
        coverImgUrl = _coverImg;

        //Chargement de l'image de couverture à l'aide de l'url
        coverImg = new Image(coverImgUrl);

        genres = cleanGenres(_genres);
        description = _description;
    }

    //Méthode static permettant de créer une variable livre à partir de son ID dans la BDD
    public static Book getBook(int id) throws SQLException {
        //Requête SQL récupérant le livre
        String querry = "SELECT * FROM Books WHERE id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1,id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Book book = new Book(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5));
            return(book);
        }

        return(null);
    }

    //Méthode static permettant d'obtenir tous les livres d'une page de l'écran d'accueil à partir du numéro de page
    public static Vector<Book> getPage(int pageNumber) {
        Vector<Book> res = new Vector<Book>();

        //La page affichant 10 livres, on les récupère dans l'ordre de leur ID correspondants à partir du numéro de la page
        for(int i=(pageNumber-1)*10+1;i<(pageNumber-1)*10+11;i++){
            try {
                res.add(Book.getBook(i));
            } catch(SQLException e) {
                e.printStackTrace();
            }
        }

        return(res);
    }

    //Méthode static permettant de récupérer les livres depuis une recherche
    public static Vector<Book> getBookFromSearch(String search) throws SQLException {
        Vector<Book> res = new Vector<Book>();

        //Requête SQL récupérant les livres
        String quer = "SELECT DISTINCT b.id FROM Books b JOIN aEcrit h ON h.bookID=b.id JOIN Authors a ON h.authorID = a.id" +
                " WHERE b.title LIKE ? OR b.description LIKE ? OR a.name LIKE ? OR a.last_name LIKE ? LIMIT 10";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(quer);

        //Permet de regarder si la recherche entrée est contenue dans l'une des colonnes SQL
        search = "%"+search+"%";
        stmt.setString(1,search);
        stmt.setString(2,search);
        stmt.setString(3,search);
        stmt.setString(4,search);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            res.add(getBook(rs.getInt(1)));
        }

        return(res);
    }

    //Méthode static permettant de récupérer un variable livre depuis l'ID d'un emprunt dans la BDD
    public static Book getBookFromEmprunt(int empruntId) throws SQLException {
        //Requête SQL récupérant l'ID du livre
        String quer = "SELECT bookId FROM Edition ed JOIN Emprunt e ON ed.isbn = e.editionISBN WHERE e.id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(quer);

        stmt.setInt(1,empruntId);

        ResultSet rs = stmt.executeQuery();

        while(rs.next()) {
            return(getBook(rs.getInt(1)));
        }

        return(null);
    }

    //Méthode static permettant de récupérer un tableau de genre depuis une chaîne de caractère comme stockée dans la BDD
    public static String[] cleanGenres(String genresTable) {
        //Retire les '[]' de la chaîne de caractère
        genresTable = BDDConnector.cleanAttribute(genresTable);

        //Récupère le tableau des genres séparés par des virgules
        String[] genresRaw = genresTable.split(",");

        //Le premier genre n'a pas d'espace avant lui
        genresRaw[0] = BDDConnector.cleanAttribute(genresRaw[0]);

        for(int i=1;i<genresRaw.length;i++) {
            //Retire l'espace et le ' des chaînes de caractères récupérées
            genresRaw[i] = BDDConnector.cleanAttribute(genresRaw[i], 2, genresRaw[i].length()-1);
        }

        return(genresRaw);
    }

    //Méthode récupérant les crédits du livre
    public Vector<HasWritten> getCredits() throws SQLException {
        Vector<HasWritten> res = new Vector<HasWritten>();

        //Requête SQL récupérant les informations des auteurs du livre
        String quer = "SELECT a.id, a.name, a.last_name, a.birth, e.role FROM Authors a JOIN aEcrit e ON a.id = e.authorID WHERE e.bookID=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,this.getId());

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            //Création d'un variable auteur pour la création d'une variable HasWritten
            Author newAuthor = new Author(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDate(4));
            HasWritten newHasWritten = new HasWritten(this, newAuthor, rs.getString(5));

            res.add(newHasWritten);
        }

        return(res);
    }

    //Méthode récupérant les éditions du livre
    public Vector<Edition> getEditions() throws SQLException {
        Vector<Edition> res = new Vector<Edition>();

        //Requête SQL récupérant les éditions
        String quer = "SELECT * FROM Edition e JOIN Books b ON b.id = e.bookID WHERE e.bookID=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,this.getId());

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Edition newEdition = new Edition(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getDate(4), rs.getInt(5));
            res.add(newEdition);
        }

        return(res);
    }

    //Méthode récupérant la note moyenne du livre
    public double getAverageNote() throws SQLException {
        double res = 0.;
        int count = 0;

        //Requête SQL récupérant toutes les notes des commentaires du livre
        String quer = "SELECT note FROM Comment WHERE bookID=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,this.getId());

        ResultSet rs = dispStmt.executeQuery();

        //Calcul de la moyenne des notes
        while(rs.next()) {
            res += rs.getInt(1);
            count++;
        }

        //Livre n'a aucun commentaire -> Pas de note moyenne
        if(count==0) {
            return(-1);
        }

        return(res/count);
    }

    //GETTERS DE CLASSE
    public int getId() { return(id); }
    public String getTitle() {
        return title;
    }
    public String getCoverImgUrl() {
        return coverImgUrl;
    }
    public Image getCoverImg() { return coverImg; }
    public String[] getGenres() { return genres; }
    public String getDescription() { return description; }

    //Méthode implémentant la méthode fillGrid du contrat PageObject pour remplir la grille d'une page de livres
    @Override
    public void fillGrid(GridPane grid, int rowIdx) {
        //Boutton du titre du livre qui renvoie vers sa view
        Button bookButton = new Button();
        bookButton.setText(title);
        bookButton.getStyleClass().add("book_button");

        BookViewController bookController = new BookViewController(this);
        bookButton.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "fxml/book-view.fxml", bookController);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //Ajout à l'interface graphique
        grid.add(bookButton, 0, rowIdx);
    }
}
