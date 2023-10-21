package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class BookViewController {
    @FXML private Label titleLabel;
    @FXML private TextFlow genreText;
    @FXML private TextFlow descriptionLabel;
    @FXML private GridPane creditsGrid;
    @FXML private GridPane editionGrid;
    @FXML private ImageView coverView;
    @FXML private Button homeButton;

    private Book book;


    public BookViewController(Book _book) {
        book = _book;
    }
    public void initialize() throws SQLException {
        titleLabel.setText(book.getTitle());

        String[] bookGenres = book.getGenres();
        Text genres = new Text(bookGenres[0]);
        for(int i=1; i<bookGenres.length; i++) {
            genres.setText(genres.getText()+", "+bookGenres[i]);
        }
        genreText.getChildren().add(genres);

        Text description = new Text(book.getDescription());
        descriptionLabel.getChildren().add(description);

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
                    MainApplication.switchScene(event, "author-view.fxml", authorController);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            creditsGrid.add(creditButton, i, 0);
        }

        Vector<Edition> editionVector = book.getEditions();
        for(int i=0;i<editionVector.size();i++) {
            Edition e = editionVector.get(i);
            e.updateButtons();

            editionGrid.add(e.editionLabel, 0, i);
            editionGrid.add(e.borrowButton, 1, i);
            editionGrid.add(e.borrowedLabel, 1, i);
            editionGrid.add(e.returnButton, 2, i);
        }

        Image cover = new Image(book.getCoverImg());
        coverView.setImage(cover);
    }

    @FXML
    private void onHomeClick(ActionEvent e) throws IOException {
        MainApplication.loadHome(e);
        //MainApplication.switchScene(e, "home-view.fxml", MainApplication.homeController);
    }
}
