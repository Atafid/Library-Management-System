package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Vector;

public class LoginController {

    @FXML private TextField usernameText;
    @FXML private PasswordField passText;
    @FXML private Button goToSignUp;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        goToSignUp.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "signUp-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        errorLabel.setVisible(false);
    }

    @FXML
    protected void onLoginClick(ActionEvent event) throws SQLException, IOException {
        User user = User.getUserFromMail(usernameText.getText());

        if(user == null) {
            errorLabel.setText("No user with such mail");
            errorLabel.setVisible(true);
            return;
        }

        String hashInputPassword = LoginPage.hashFunction(passText.getText(), user.getPasswordSalt(), MainApplication.pepper, MainApplication.hashIncrementation);

        if (Objects.equals(user.getHashPassword(), hashInputPassword)) {
            MainApplication.header = new Header(user);
            HomeController homeController = new HomeController(MainApplication.fstPageHome, MainApplication.sndPageHome);
            MainApplication.switchScene(event, "home-view.fxml", homeController);
        } else {
            errorLabel.setText("Wrong password");
            errorLabel.setVisible(true);
        }

    }
}