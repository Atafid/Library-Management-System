package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.*;
import com.example.tp_bibliotheque.Objects.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Vector;

//CONTROLLER DE LA VIEW LIVRE

public class BookViewController {
    //*****************ATTRIBUTS*****************//

    //AnchorPane : racine de la fenêtre
    @FXML private AnchorPane root;

    //Label : titre du livre relatif à la fenêtre
    @FXML private Label titleLabel;

    //TextFlow : genres du livre relatif à la fenêtre
    @FXML private TextFlow genreText;

    //TextFlow : description du livre relatif à la fenêtre
    @FXML private TextFlow descriptionLabel;

    //GridPane : auteurs du livre relatif à la fenêtre
    @FXML private GridPane creditsGrid;

    //GridPane : éditions du livre relatif à la fenêtre
    @FXML private GridPane editionGrid;

    //ImageView : couverture du livre relatif à la fenêtre
    @FXML private ImageView coverView;

    //Slider : note du commentaire à envoyer
    @FXML private Slider noteBar;

    //TextArea : description du commentaire à envoyer
    @FXML private TextArea commentArea;

    //GridPane : commentaires déjà écrits
    @FXML private GridPane commentGrid;

    //Elements relatifs au système de page d'éléments à afficher : les commentaires déjà écrits
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page commentPage;

    //Livre relatif à la fenêtre
    private final Book book;


    //*****************METHODES*****************//

    //Contructeur de classe
    public BookViewController(Book _book) {
        book = _book;
    }

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    public void initialize() throws SQLException {
        //Ajout du header à la racine de la fenêtre
        root.getChildren().add(MainApplication.header.getHead());

        //Initialisation du label du titre du livre
        titleLabel.setText(book.getTitle());



        //***GENRES***//

        //Tableau des genres du livre
        String[] bookGenres = book.getGenres();

        //Ajout des genres à l'interface graphique
        Text genres = new Text(bookGenres[0]);
        for(int i=1; i<bookGenres.length; i++) {
            genres.setText(genres.getText()+", "+bookGenres[i]);
        }
        genreText.getChildren().add(genres);



        //***DESCRIPTION***//
        Text description = new Text(book.getDescription());
        descriptionLabel.getChildren().add(description);



        //***CREDITS***//
        String creditsString = "";

        //Tableau des crédits du livre
        Vector<HasWritten> creditsVector = book.getCredits();

        //Ajout des crédits à l'interface graphique
        for(int i=0;i<creditsVector.size();i++) {
            HasWritten h = creditsVector.get(i);

            //Boutton permettant d'accéder à l'auteur relaté dans les crédits
            Button creditButton = new Button();
            creditButton.setText(h.getAuthor().getName() + " " + h.getAuthor().getLastName() + " " + h.getRole()+", ");
            creditButton.getStyleClass().add("credit_button");

            //Appui sur le boutton -> Changement de view
            AuthorViewController authorController = new AuthorViewController(h.getAuthor());
            creditButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "fxml/author-view.fxml", authorController);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            creditsGrid.add(creditButton, i, 0);
        }



        //***EDITIONS***//

        //Tableau des éditions du livre
        Vector<Edition> editionVector = book.getEditions();

        //Ajout des éditions à l'interface graphique
        for(int i=0;i<editionVector.size();i++) {
            Edition e = editionVector.get(i);
            e.updateButtons();

            editionGrid.add(e.editionLabel, 0, i);
            editionGrid.add(e.borrowButton, 1, i);
            editionGrid.add(e.borrowedLabel, 1, i);
            editionGrid.add(e.returnButton, 2, i);
            editionGrid.add(e.reserveButton, 2, i);
            editionGrid.add(e.reserveLabel, 2, i);
        }



        //***COMMENTS***//

        //Initialisation de la page des commentaires déjà écrits
        try {
            commentPage = new Page(prevButton, nextButton, Comment.getComment(book.getId()), commentGrid, pageLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        coverView.setImage(book.getCoverImg());
    }

    //Méthode de mis à jour des commentaires déjà écrits
    private void updateCommentSection() throws SQLException {
        //Récupération des commentaires relatifs au livre
        commentPage.setObjects(Comment.getComment(book.getId()));
        commentPage.updateFXML();
    }

    //Méthode d'envoi d'un nouveau commentaire
    @FXML
    private void onSendClick() throws SQLException {
        //Ajout du commentaire à la BDD
        Comment.addComment(MainApplication.header.getUser().getId(), book.getId(), new Date(System.currentTimeMillis()), (int) noteBar.getValue(), commentArea.getText());
        //Mise à jour de la section commentaire déjà écrits
        updateCommentSection();

        //Réinitialisation de la section d'écriture d'un nouveau commentaire
        noteBar.setValue(1.0);
        commentArea.clear();
    }
}
