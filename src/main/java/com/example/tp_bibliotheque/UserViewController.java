package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class UserViewController {
    @FXML private Button homeButton;
    @FXML private Label mailLabel;
    @FXML private Label nameLabel;
    @FXML private Label lastNameLabel;
    @FXML private GridPane empruntGrid;

    User user;

    public UserViewController(User _user) {
        user = _user;
    }

    public void initialize() throws SQLException {
        mailLabel.setText(user.getMail());
        nameLabel.setText(user.getName());
        lastNameLabel.setText(user.getLastName());

        Vector<Emprunt> currentEmprunt = Emprunt.getCurrentEmpruntFromUser(user.getId());
        Vector<Emprunt> finishedEmprunt = Emprunt.getFinishedEmpruntFromUser(user.getId());

        for(int i=0;i<currentEmprunt.size();i++) {
            Emprunt e = currentEmprunt.get(i);
            Book empruntBook = e.getBook();

            Button empruntButton = new Button();
            empruntButton.setText(empruntBook.getTitle()+" "+e.getEditionISBN());
            empruntButton.getStyleClass().add("emprunt_button");

            Label empruntLabel = new Label();
            empruntLabel.setText(", until : "+e.getStringHypEndDate());

            if(e.checkLateStatus()) {
                empruntLabel.setText(empruntLabel.getText()+" LATE!");
            }

            BookViewController bookController = new BookViewController(empruntBook);
            empruntButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "book-view.fxml", bookController);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            empruntGrid.add(empruntButton, 0, i);
            empruntGrid.add(empruntLabel, 1, i);
        }

        for(int i=0;i<finishedEmprunt.size();i++) {
            Emprunt e = finishedEmprunt.get(i);
            Book empruntBook = e.getBook();

            Button empruntButton = new Button();
            empruntButton.setText(empruntBook.getTitle()+" "+e.getEditionISBN());
            empruntButton.getStyleClass().add("emprunt_button");

            Label empruntLabel = new Label();
            empruntLabel.setText(", finished since : "+e.getStringRealEndDate());

            BookViewController bookController = new BookViewController(empruntBook);
            empruntButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "book-view.fxml", bookController);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            empruntGrid.add(empruntButton, 0, currentEmprunt.size()+i);
            empruntGrid.add(empruntLabel, 1, currentEmprunt.size()+i);
        }
    }

    @FXML
    private void onHomeClick(ActionEvent e) throws IOException {
        MainApplication.loadHome(e);
        System.gc();
        //MainApplication.switchScene(e, "home-view.fxml", MainApplication.homeController);
    }
}
