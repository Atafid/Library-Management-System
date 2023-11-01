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

public class BookViewController {
    @FXML private AnchorPane root;
    @FXML private Label titleLabel;
    @FXML private TextFlow genreText;
    @FXML private TextFlow descriptionLabel;
    @FXML private GridPane creditsGrid;
    @FXML private GridPane editionGrid;
    @FXML private ImageView coverView;
    @FXML private Slider noteBar;
    @FXML private TextArea commentArea;
    @FXML private GridPane commentGrid;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page commentPage;

    private Book book;


    public BookViewController(Book _book) {
        book = _book;
    }
    public void initialize() throws SQLException {
        root.getChildren().add(MainApplication.header.getHead());

        titleLabel.setText(book.getTitle());

        //GENRES
        String[] bookGenres = book.getGenres();
        Text genres = new Text(bookGenres[0]);
        for(int i=1; i<bookGenres.length; i++) {
            genres.setText(genres.getText()+", "+bookGenres[i]);
        }
        genreText.getChildren().add(genres);

        //DESCRIPTION
        Text description = new Text(book.getDescription());
        descriptionLabel.getChildren().add(description);

        //CREDITS
        String creditsString = "";
        Vector<HasWritten> creditsVector = book.getCredits();
        for(int i=0;i<creditsVector.size();i++) {
            HasWritten h = creditsVector.get(i);

            Button creditButton = new Button();
            creditButton.setText(h.getAuthor().getName() + " " + h.getAuthor().getLastName() + " " + h.getRole()+", ");
            creditButton.getStyleClass().add("credit_button");

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

        //EDITIONS
        Vector<Edition> editionVector = book.getEditions();
        for(int i=0;i<editionVector.size();i++) {
            Edition e = editionVector.get(i);
            e.updateButtons();

            editionGrid.add(e.editionLabel, 0, i);
            editionGrid.add(e.borrowButton, 1, i);
            editionGrid.add(e.borrowedLabel, 1, i);
            editionGrid.add(e.returnButton, 2, i);
        }

        //COMMENTS
        try {
            commentPage = new Page(prevButton, nextButton, Comment.getComment(book.getId()), commentGrid, pageLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        coverView.setImage(book.getCoverImg());
    }

    private void updateCommentSection() throws SQLException {
        commentPage.setObjects(Comment.getComment(book.getId()));
        commentPage.updateFXML();
    }


    @FXML
    private void onSendClick() throws SQLException {
        Comment.addComment(MainApplication.header.getUser().getId(), book.getId(), new Date(System.currentTimeMillis()), (int) noteBar.getValue(), commentArea.getText());
        updateCommentSection();

        noteBar.setValue(1.0);
        commentArea.clear();
    }
}
