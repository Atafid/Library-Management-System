package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Objects.*;
import com.example.tp_bibliotheque.MainApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class UserViewController {
    @FXML private AnchorPane root;
    @FXML private Label mailLabel;
    @FXML private Label nameLabel;
    @FXML private Label lastNameLabel;
    @FXML private GridPane empruntGrid;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page empruntsPage;

    User user;

    public UserViewController(User _user) {
        user = _user;
    }

    public void initialize() throws SQLException {
        root.getChildren().add(MainApplication.header.getHead());

        mailLabel.setText(user.getMail());
        nameLabel.setText(user.getName());
        lastNameLabel.setText(user.getLastName());

        empruntsPage = new Page(prevButton, nextButton, Emprunt.getAllEmpruntsFromUser(user.getId()), empruntGrid, pageLabel);

    }
}
