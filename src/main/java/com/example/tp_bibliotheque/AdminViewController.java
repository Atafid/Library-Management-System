package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class AdminViewController {
    @FXML private GridPane userGrid;
    private User admin;

    public AdminViewController(User _admin) {
        admin = _admin;
    }

    public void initialize() throws SQLException {
        Vector<User> users = User.getAllUsers();

        for(int i=0;i<users.size();i++) {
            User u = users.get(i);

            Button userButton = new Button();
            userButton.setText(u.getMail());
            userButton.getStyleClass().add("user_button");

            Label empruntLabel = new Label();
            empruntLabel.setText(Emprunt.getCurrentEmpruntFromUser(u.getMail()).size()+" current borrows");

            UserViewController userController = new UserViewController(u);
            userButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "user-view.fxml", userController);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            if(!u.categorie.equals(Categorie.Bibliothécaire)) {
                ChoiceBox<Categorie> userCB = new ChoiceBox<Categorie>();
                userCB.getItems().addAll(Categorie.Cat1, Categorie.Cat2, Categorie.Cat3, Categorie.Forbidden);
                userCB.setValue(u.categorie);

                userCB.setOnAction((event) -> {
                    try {
                        u.setCategorie(userCB.getSelectionModel().getSelectedItem());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                userGrid.add(userCB, 2,i);
            }

            else {
                Label bibliothecaireLabel = new Label("Bibilothécaire");

                userGrid.add(bibliothecaireLabel, 2, i);
            }

            userGrid.add(userButton, 0, i);
            userGrid.add(empruntLabel, 1, i);
        }
    }

    @FXML
    private void onHomeClick(ActionEvent e) throws IOException {
        MainApplication.loadHome(e);
        //MainApplication.switchScene(e, "home-view.fxml", MainApplication.homeController);
    }
}
