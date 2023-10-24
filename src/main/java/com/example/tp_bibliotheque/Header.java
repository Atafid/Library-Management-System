package com.example.tp_bibliotheque;

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
import java.sql.SQLException;
import java.util.Vector;

public class Header {
    @FXML private HBox head;
    @FXML private Button homeButton;
    @FXML private TextField searchBar;
    @FXML private MenuBar userMenuBar;
    @FXML private Button adminButton;
    private User user;

    public Header(User _user) {
        user = _user;

        head = new HBox();
        head.setEffect(new DropShadow(10, Color.BLACK));
        head.getStyleClass().add("header");

        homeButton = new Button();
        ImageView homeView = new ImageView(new Image(getClass().getResourceAsStream("logo.png")));
        homeView.setFitHeight(50);
        homeView.setFitWidth(50);
        homeButton.setGraphic(homeView);

        ImageView searchView = new ImageView(new Image(getClass().getResourceAsStream("loupe.png")));
        searchView.setFitWidth(20);
        searchView.setFitHeight(20);
        searchBar = new TextField();
        searchBar.setPromptText("Search");

        userMenuBar = new MenuBar();
        Menu userMenu = new Menu();
        ImageView userView = new ImageView(new Image(getClass().getResourceAsStream("account.png")));
        userView.setFitHeight(50);
        userView.setFitWidth(50);
        userMenu.setGraphic(userView);
        userMenu.getStyleClass().add("menu");
        MenuItem userButton = new MenuItem("Account");
        MenuItem signOutButton = new MenuItem("Sign Out");
        userMenu.getItems().addAll(userButton, signOutButton);
        userMenuBar.getMenus().add(userMenu);

        adminButton = new Button();
        ImageView adminView = new ImageView(new Image(getClass().getResourceAsStream("key.png")));
        adminView.setFitHeight(50);
        adminView.setFitWidth(50);
        adminButton.setGraphic(adminView);
        adminButton.setVisible(user.categorie.equals(Categorie.Bibliothécaire));

        homeButton.setOnAction(event -> {
            try {
                onHomeClick(event);
                searchBar.setText("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        searchBar.setOnAction(event -> {
            try {
                onSearch(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        adminButton.setOnAction(event -> {
            try {
                onAdminClick(event);
                searchBar.setText("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        userButton.setOnAction(event -> {
            try {
                onUserClick(event);
                searchBar.setText("");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        signOutButton.setOnAction(event -> {
            try {
                onSignOut(event);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        head.getChildren().addAll(homeButton, searchView, searchBar, new Region(), adminButton, userMenuBar);
    }

    public HBox getHead() { return head; }
    public User getUser() { return user; }

    @FXML private void onSearch(ActionEvent e) throws IOException, SQLException {
        HomeController homeController = new HomeController(MainApplication.fstPageHome, MainApplication.sndPageHome);
        MainApplication.switchScene(e, "home-view.fxml", homeController);
        homeController.onSearch(searchBar.getText());
    }
    @FXML
    private void onHomeClick(ActionEvent e) throws IOException {
        HomeController homeController = new HomeController(MainApplication.fstPageHome, MainApplication.sndPageHome);
        MainApplication.switchScene(e, "home-view.fxml", homeController);
    }
    @FXML
    private void onSignOut(ActionEvent e) throws IOException {
        MainApplication.header = null;
        MainApplication.switchScene(e.copyFor(userMenuBar, null), "login-view.fxml");
    }
    @FXML
    private void onUserClick(ActionEvent e) throws IOException {
        UserViewController userViewController = new UserViewController(user);
        MainApplication.switchScene(e.copyFor(userMenuBar, null), "user-view.fxml", userViewController);
    }
    @FXML
    private void onAdminClick(ActionEvent e) throws IOException {
        AdminViewController adminViewController = new AdminViewController(user);
        MainApplication.switchScene(e, "admin-view.fxml", adminViewController);
    }
}
