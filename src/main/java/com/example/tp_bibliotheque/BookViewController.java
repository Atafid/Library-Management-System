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
    @FXML private TextFlow descriptionLabel;
    @FXML private ImageView coverView;
    @FXML private Button homeButton;
    @FXML private Button empruntButton;
    @FXML private Label empruntLabel;

    private Book book;

    public BookViewController(Book _book) {
        book = _book;
    }

    public void initialize() throws SQLException {
        titleLabel.setText(book.getTitle());

        Text description = new Text(book.getDescription());
        descriptionLabel.getChildren().add(description);

        Image cover = new Image(book.getCoverImg());
        coverView.setImage(cover);

        if(book.getBorrowedStatus()) {
            empruntButton.visibleProperty().set(false);
            empruntLabel.setText(empruntLabel.getText()+MainApplication.bddConn.getCurrentEmpruntFromBook(book).getEndDate());
        }

        else {
            empruntLabel.visibleProperty().set(false);
        }
    }

    @FXML
    private void onHomeClick(ActionEvent e) throws IOException {
        MainApplication.loadHome(e);
        //MainApplication.switchScene(e, "home-view.fxml", MainApplication.homeController);
    }

    @FXML
    private void onClickEmprunt(ActionEvent e) throws SQLException {
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        long dayMillis = (long) (8.64*Math.pow(10,7));
        Date endDate = new Date(millis+10*dayMillis);

        MainApplication.bddConn.addEmprunt(book, HomeController.user, currentDate, endDate);

        empruntButton.visibleProperty().set(false);
        empruntLabel.visibleProperty().set(true);
        empruntLabel.setText(empruntLabel.getText()+endDate);
    }
}
