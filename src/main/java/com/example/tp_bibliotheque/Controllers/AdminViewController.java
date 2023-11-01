package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.*;
import com.example.tp_bibliotheque.Objects.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Vector;

public class AdminViewController {
    @FXML private AnchorPane root;
    @FXML private GridPane userGrid;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page userPage;
    private User admin;

    public AdminViewController(User _admin) {
        admin = _admin;
    }

    public void initialize() throws SQLException {
        root.getChildren().add(MainApplication.header.getHead());

        userPage = new Page(prevButton, nextButton, User.getAllUsers(),userGrid, pageLabel);
    }

}
