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
import org.controlsfx.control.spreadsheet.Grid;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.sql.Date;
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
            creditButton.setText(h.getAuthor().getName() + " " + h.getAuthor().getLast_name() + " " + h.getRole()+", ");
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

            Label editionLabel = new Label();
            editionLabel.setText(e.getEditorName()+", "+e.getPublishDate().toString());

            Button borrowButton = new Button();
            borrowButton.setText("Borrow");
            Label borrowedLabel = new Label();
            borrowedLabel.setText("Book borrowed until :");
            Button returnButton = new Button();
            returnButton.setText("Return");

            Emprunt currentEmprunt = e.getBorrowedStatus();
            if(currentEmprunt != null) {
                borrowButton.setVisible(false);

                borrowedLabel.setText(borrowedLabel.getText()+currentEmprunt.getHypEndDate()+" by "+currentEmprunt.getUserMail());

                if(!currentEmprunt.getUserMail().equals(HomeController.user.getMail())) {
                    returnButton.setVisible(false);
                }
            }

            else {
                borrowedLabel.setVisible(false);
                returnButton.setVisible(false);
            }

            borrowButton.setOnAction(event -> {
                try {
                    onClickEmprunt(e,borrowButton,borrowedLabel,returnButton);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            returnButton.setOnAction(event -> {
                try {
                    onClickReturn(currentEmprunt.getId(),borrowButton,borrowedLabel,returnButton);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });

            editionGrid.add(editionLabel, 0, i);
            editionGrid.add(borrowButton, 1, i);
            editionGrid.add(borrowedLabel, 1, i);
            editionGrid.add(returnButton, 2, i);
        }

        Image cover = new Image(book.getCoverImg());
        coverView.setImage(cover);
    }

    @FXML
    private void onHomeClick(ActionEvent e) throws IOException {
        MainApplication.loadHome(e);
        //MainApplication.switchScene(e, "home-view.fxml", MainApplication.homeController);
    }
    @FXML
    private void onClickEmprunt(Edition e, Button borrowButton, Label borrowedLabel, Button returnButton) throws SQLException {
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        long dayMillis = (long) (8.64*Math.pow(10,7));
        Date endDate = new Date(millis+10*dayMillis);

        Emprunt.addEmprunt(e, HomeController.user, currentDate, endDate);

        borrowButton.setVisible(false);

        borrowedLabel.setVisible(true);
        borrowedLabel.setText(borrowedLabel.getText()+endDate+" by "+HomeController.user.getMail());

        returnButton.setVisible(true);
        returnButton.setOnAction(event -> {
            try {
                onClickReturn(Emprunt.getCurrentEmpruntFromEdition(e).getId(),borrowButton,borrowedLabel,returnButton);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    @FXML
    private void onClickReturn(int empruntId, Button borrowButton, Label borrowedLabel, Button returnButton) throws SQLException {
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        Emprunt.finishEmprunt(empruntId, currentDate);

        borrowButton.setVisible(true);

        borrowedLabel.setVisible(false);
        returnButton.setVisible(false);
    }
}
