package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignUpController {
    @FXML private TextField mailSignUp;
    @FXML private TextField nameSignUp;
    @FXML private PasswordField passSignUp;
    @FXML private PasswordField confirmPassSignUp;
    @FXML private Button goToLogin;
    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        goToLogin.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "login-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        errorLabel.setVisible(false);
    }

    @FXML
    protected void onSignUp(ActionEvent event) {
        String mail = mailSignUp.getText();
        String name = nameSignUp.getText();
        String password = passSignUp.getText();
        String confirmPassword = confirmPassSignUp.getText();

        if(mail.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Fill every fields");
            errorLabel.setVisible(true);
            return;
        }

        if(!LoginPage.isValidMail(mail)) {
            errorLabel.setText("Invalid Mail");
            errorLabel.setVisible(true);
            return;
        }

        if(!password.equals(confirmPassword)) {
            errorLabel.setText("Different passwords");
            errorLabel.setVisible(true);
            return;
        }

        String userSalt = LoginPage.Salt();
        String hashPassword = LoginPage.hashFunction(password, userSalt, MainApplication.pepper, MainApplication.hashIncrementation);

        try {
            User.addUser(mail, name, hashPassword, userSalt);
            MainApplication.switchScene(event, "login-view.fxml");
        } catch(Exception e) {
            errorLabel.setText("Account already exists");
            errorLabel.setVisible(true);
        }

        return;
    }

}
