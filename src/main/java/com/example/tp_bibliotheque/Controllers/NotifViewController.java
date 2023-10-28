package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.Objects.Notification;
import com.example.tp_bibliotheque.Objects.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.Vector;

public class NotifViewController {
    @FXML private AnchorPane root;
    @FXML private GridPane notifGrid;

    private User user;
    private Vector<Notification> notifs;

    public NotifViewController(User _user) {
        user = _user;
        try {
            notifs = Notification.getNotifications(user.getId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        root.getChildren().add(MainApplication.header.getHead());

        for(int i=0;i<notifs.size();i++) {
            Notification n = notifs.get(i);
            try {
                Notification.readNotification(n.getId());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            Label notifLabel = null;
            try {
                notifLabel = new Label(n.getString());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            notifGrid.add(notifLabel, 0, i);
        }
    }
}
