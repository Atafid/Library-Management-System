package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Calendar;
import java.sql.Date;

public class BookViewController {
    @FXML private Label titleLabel;
    @FXML private TextFlow genreText;
    @FXML private TextFlow descriptionLabel;
    @FXML private TextFlow publishDateText;
    @FXML private ImageView coverView;
    @FXML private Button homeButton;
    @FXML private Button empruntButton;
    @FXML private Label empruntLabel;
    @FXML private Button returnButton;

    private Book book;
    private Emprunt currentEmprunt;

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

        Text publishDate = new Text(book.getPublishDate());
        publishDateText.getChildren().add(publishDate);

        Image cover = new Image(book.getCoverImg());
        coverView.setImage(cover);

        if(book.getBorrowedStatus()) {
            empruntButton.setVisible(false);

            currentEmprunt = Emprunt.getCurrentEmpruntFromBook(book);
            empruntLabel.setText(empruntLabel.getText()+currentEmprunt.getHypEndDate()+" by "+currentEmprunt.getUserMail());

            if(!currentEmprunt.getUserMail().equals(HomeController.user.getMail())) {
                returnButton.setVisible(false);
            }
        }

        else {
            empruntLabel.setVisible(false);
            returnButton.setVisible(false);
        }
    }

    @FXML
    private void onHomeClick(ActionEvent e) throws IOException {
        MainApplication.loadHome(e);
        //MainApplication.switchScene(e, "home-view.fxml", MainApplication.homeController);
    }
    @FXML
    private void onClickEmprunt() throws SQLException {
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        long dayMillis = (long) (8.64*Math.pow(10,7));
        Date endDate = new Date(millis+10*dayMillis);

        Emprunt.addEmprunt(book, HomeController.user, currentDate, endDate);
        currentEmprunt = Emprunt.getCurrentEmpruntFromBook(book);

        empruntButton.setVisible(false);

        empruntLabel.setVisible(true);
        empruntLabel.setText(empruntLabel.getText()+endDate+" by "+HomeController.user.getMail());

        returnButton.setVisible(true);
    }
    @FXML
    private void onClickReturn() throws SQLException {
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        Emprunt.finishEmprunt(currentEmprunt.getId(), currentDate);

        empruntButton.setVisible(true);

        empruntLabel.setVisible(false);
        returnButton.setVisible(false);
        currentEmprunt = null;
    }
}
