package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SignUpController {
    @FXML private TextField mailSignUp;
    @FXML private TextField nameSignUp;
    @FXML private PasswordField passSignUp;
    @FXML private PasswordField confirmPassSignUp;
    @FXML private Button goToLogin;

    @FXML
    public void initialize() {
        goToLogin.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "login-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @FXML
    protected void onSignUp(ActionEvent event) {
        String mail = mailSignUp.getText();
        String name = nameSignUp.getText();
        String password = passSignUp.getText();
        String confirmPassword = confirmPassSignUp.getText();

        if(mail.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            System.out.println("Remplir tous les champs");
            return;
        }

        if(!LoginPage.isValidMail(mail)) {
            System.out.println("Mail invalide");
            return;
        }

        if(!password.equals(confirmPassword)) {
            System.out.println("Mots de passe différents");
            return;
        }

        String userSalt = LoginPage.Salt();
        String hashPassword = LoginPage.hashFunction(password, userSalt, MainApplication.pepper, MainApplication.hashIncrementation);

        try {
            MainApplication.bddConn.addUser(mail, name, hashPassword, userSalt);
            MainApplication.switchScene(event, "login-view.fxml");
            System.out.println("Inscription réussie");
        } catch(Exception e) {
            System.out.println("Compte déjà existant");
        }

        return;
    }

}
