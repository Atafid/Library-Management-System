package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.Objects.Comment;
import com.example.tp_bibliotheque.Objects.Notification;
import com.example.tp_bibliotheque.Objects.Page;
import com.example.tp_bibliotheque.Objects.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.Vector;

public class NotifViewController {
    @FXML private AnchorPane root;
    @FXML private GridPane notifGrid;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page notifPage;

    private User user;

    public NotifViewController(User _user) {
        user = _user;
    }

    public void initialize() {
        root.getChildren().add(MainApplication.header.getHead());

        try {
            notifPage = new Page(prevButton, nextButton,  Notification.getNotifications(user.getId()), notifGrid, pageLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
