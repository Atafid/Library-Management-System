package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

//CLASSE REPRESENTANT UN COMMENTAIRE -> PageObject

public class Comment implements PageObject {
    //*****************ATTRIBUTS*****************//

    //ID du commentaire dans la BDD
    private final int id;

    //ID de l'utilisateur ayant écrit le commentaire, dans la BDD
    private final int userId;

    //ID du livre relatif au commentaire, dans la BDD
    private final int bookId;

    //Date du commentaire
    private final Date date;

    //Note du commentaire
    private final int note;

    //Contenu du commentaire
    private final String content;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Comment(int _id, int _userId, int _bookId, Date _date, int _note, String _content) {
        id = _id;
        userId = _userId;
        bookId = _bookId;
        date = _date;
        note = _note;
        content = _content;
    }

    //Méthode static permettant d'ajouter un commentaire à la BDD
    public static void addComment(int _userId, int _bookId, Date _date, int _note, String _content) throws SQLException {
        //Requête SQL ajoutant le commentaire, l'ID étant en auto_increment
        String querry = "INSERT INTO Comment VALUES(0,?,?,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, _userId);
        stmt.setInt(2, _bookId);
        stmt.setDate(3, _date);
        stmt.setInt(4, _note);
        stmt.setString(5, _content);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    //Méthode staic permettant d'obtenir tous les commentaires à partir de l'ID d'un livre
    public static Vector<PageObject> getComment(int _bookId) throws SQLException {
        Vector<PageObject> res = new Vector<PageObject>();

        //Requête SQL récupérant les commentaires
        String querry = "SELECT * FROM Comment WHERE bookId=? ORDER BY date DESC";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, _bookId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Comment comment = new Comment(rs.getInt(1),rs.getInt(2), rs.getInt(3), rs.getDate(4), rs.getInt(5), rs.getString(6));
            res.add(comment);
        }

        return(res);
    }

    //GETTERS DE CLASSE
    public int getUserId() { return userId; }
    public int getNote() { return note; }
    public String getContent() { return content; }
    public Date getDate() { return date; }

    //Méthode implémentant la méthode fillGrid du contrat PageObject pour remplir la grille d'une page de commentaires
    @Override
    public void fillGrid(GridPane grid, int rowIdx) {
        //Label du commentaire avec le mail de l'utilisateur, la note et la date
        Label noteLabel = new Label();
        try {
            noteLabel.setText(String.valueOf("- "+note+"/5, "+ User.getUserFromId(userId).getMail()+" : "));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //TextFlow du contenu du commentaire
        Label contentLabel = new Label(content);

        //Ajout à l'interface graphique
        grid.add(noteLabel, 0, rowIdx);
        grid.add(contentLabel, 1, rowIdx);
    }
}
