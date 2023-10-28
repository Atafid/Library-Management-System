package com.example.tp_bibliotheque;

import com.example.tp_bibliotheque.Controllers.AdminViewController;
import com.example.tp_bibliotheque.Controllers.HomeController;
import com.example.tp_bibliotheque.Controllers.NotifViewController;
import com.example.tp_bibliotheque.Controllers.UserViewController;
import com.example.tp_bibliotheque.Objects.Categorie;
import com.example.tp_bibliotheque.Objects.Emprunt;
import com.example.tp_bibliotheque.Objects.Notification;
import com.example.tp_bibliotheque.Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Vector;

public class Header {
    @FXML private HBox head;
    @FXML private Button homeButton;
    @FXML private TextField searchBar;
    @FXML private MenuBar userMenuBar;
    @FXML private MenuBar notifBar;
    @FXML private Button adminButton;
    private User user;

    public Header(User _user) {
        user = _user;

        head = new HBox();
        head.setEffect(new DropShadow(10, Color.BLACK));
        head.getStyleClass().add("header");

        //HOME
        homeButton = new Button();
        ImageView homeView = new ImageView(new Image(getClass().getResourceAsStream("img/logo.png")));
        homeView.setFitHeight(50);
        homeView.setFitWidth(50);
        homeButton.setGraphic(homeView);

        homeButton.setOnAction(event -> {
            try {
                onHomeClick(event);
                searchBar.setText("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //SEARCH
        ImageView searchView = new ImageView(new Image(getClass().getResourceAsStream("img/loupe.png")));
        searchView.setFitWidth(20);
        searchView.setFitHeight(20);
        searchBar = new TextField();
        searchBar.setPromptText("Search");

        searchBar.setOnAction(event -> {
            try {
                onSearch(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        //USER
        userMenuBar = new MenuBar();
        Menu userMenu = new Menu();
        ImageView userView = new ImageView(new Image(getClass().getResourceAsStream("img/account.png")));
        userView.setFitHeight(50);
        userView.setFitWidth(50);
        userMenu.setGraphic(userView);
        userMenu.getStyleClass().add("menu");
        MenuItem userButton = new MenuItem("Account");
        MenuItem signOutButton = new MenuItem("Sign Out");
        userMenu.getItems().addAll(userButton, signOutButton);
        userMenuBar.getMenus().add(userMenu);

        userButton.setOnAction(event -> {
            try {
                onUserClick(event);
                searchBar.setText("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //NOTIFICATIONS
        notifBar = new MenuBar();
        Menu notifMenu = new Menu();
        notifMenu.getStyleClass().add("menu");
        try {
            Vector<Emprunt> emprunts = Emprunt.getCurrentEmpruntsFromUser(user.getId());
            Date currentDate = new Date(System.currentTimeMillis());
            for(Emprunt e:emprunts) {
                if(currentDate.after(e.getHypEndDate()) && !e.isNotified()) {
                    Notification.addNotification(user.getId(), "L", currentDate, e.getId());
                    for(User a:User.getAdmin()) {
                        Notification.addNotification(a.getId(), "A", currentDate, user.getId());
                    }
                }
            }

            Vector<Notification> notifs = Notification.getUnreadNotification(user.getId());
            int unreadNotif = notifs.size();

            for(Notification n:notifs) {
                MenuItem newNotif= new MenuItem(n.getString()+", "+n.getDate());
                notifMenu.getItems().add(newNotif);

                newNotif.setOnAction(event -> {
                    try {
                        Notification.readNotification(n.getId());
                        newNotif.setText("read");
                        notifMenu.setText(notifMenu.getText().substring(0, notifMenu.getText().length()-1)+(Integer.parseInt(notifMenu.getText().substring(notifMenu.getText().length()-1))-1));
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    searchBar.setText("");
                });
            }

            notifMenu.setText("Notifs : "+unreadNotif);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MenuItem showNotif = new MenuItem("Show all notifications");
        showNotif.setOnAction(event -> {
            try {
                onNotifClick(event);
                notifMenu.setText("Notifs : 0");
                searchBar.setText("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        notifMenu.getItems().add(showNotif);
        notifBar.getMenus().add(notifMenu);


        //ADMIN
        adminButton = new Button();
        ImageView adminView = new ImageView(new Image(getClass().getResourceAsStream("img/key.png")));
        adminView.setFitHeight(50);
        adminView.setFitWidth(50);
        adminButton.setGraphic(adminView);
        adminButton.setVisible(user.categorie.equals(Categorie.Bibliothécaire));

        adminButton.setOnAction(event -> {
            try {
                onAdminClick(event);
                searchBar.setText("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //SIGN OUT
        signOutButton.setOnAction(event -> {
            try {
                onSignOut(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        head.getChildren().addAll(homeButton, searchView, searchBar, new Region(), adminButton, notifBar, userMenuBar);
    }

    public HBox getHead() { return head; }
    public User getUser() { return user; }

    @FXML private void onSearch(ActionEvent e) throws IOException, SQLException {
        HomeController homeController = new HomeController(MainApplication.fstPageHome, MainApplication.sndPageHome);
        MainApplication.switchScene(e, "fxml/home-view.fxml", homeController);
        homeController.onSearch(searchBar.getText());
    }
    @FXML
    private void onHomeClick(ActionEvent e) throws IOException {
        HomeController homeController = new HomeController(MainApplication.fstPageHome, MainApplication.sndPageHome);
        MainApplication.switchScene(e, "fxml/home-view.fxml", homeController);
    }
    @FXML
    private void onSignOut(ActionEvent e) throws IOException {
        MainApplication.header = null;
        MainApplication.switchScene(e.copyFor(userMenuBar, null), "fxml/login-view.fxml");
    }
    @FXML
    private void onUserClick(ActionEvent e) throws IOException {
        UserViewController userViewController = new UserViewController(user);
        MainApplication.switchScene(e.copyFor(userMenuBar, null), "fxml/user-view.fxml", userViewController);
    }
    @FXML
    private void onNotifClick(ActionEvent e) throws IOException {
        NotifViewController notifController = new NotifViewController(user);
        MainApplication.switchScene(e.copyFor(notifBar, null), "fxml/notif-view.fxml", notifController);
    }
    @FXML
    private void onAdminClick(ActionEvent e) throws IOException {
        AdminViewController adminViewController = new AdminViewController(user);
        MainApplication.switchScene(e, "fxml/admin-view.fxml", adminViewController);
    }
}
