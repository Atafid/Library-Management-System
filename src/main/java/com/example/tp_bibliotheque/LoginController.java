package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class LoginController {

    @FXML private TextField usernameText;
    @FXML private PasswordField passText;
    @FXML private Button goToSignUp;

    @FXML
    public void initialize() {
        goToSignUp.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "signUp-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    protected void onLoginClick(ActionEvent event) throws SQLException, IOException {
        User user = MainApplication.bddConn.getUser(usernameText.getText());

        if(user == null) {
            System.out.println("No user with such mail");
            return;
        }

        String hashInputPassword = LoginPage.hashFunction(passText.getText(), user.getPasswordSalt(), MainApplication.pepper, MainApplication.hashIncrementation);

        if (Objects.equals(user.getHashPassword(), hashInputPassword)) {
            MainApplication.initHome(user);

            MainApplication.loadHome(event);
            //MainApplication.switchScene(event, "home-view.fxml", homeController);
        } else {
            System.out.println("Wrong password");
        }

    }
}