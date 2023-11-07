package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Header;
import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.LoginUtils;
import com.example.tp_bibliotheque.Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

//CONTROLLER DE LA VIEW CONNEXION

public class LoginController {
    //*****************ATTRIBUTS*****************//

    //TextField : champ du mail
    @FXML private TextField usernameText;

    //PasswordField : champ du mot de passe
    @FXML private PasswordField passText;

    //Button : redirige vers la page d'inscription
    @FXML private Button goToSignUp;

    //Label : erreur de connexion
    @FXML private Label errorLabel;


    //*****************METHODES*****************//

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    @FXML
    public void initialize() {
        //Changement de view lors de la redirection vers la page d'inscription
        goToSignUp.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "fxml/signUp-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        errorLabel.setVisible(false);
    }

    //Méthode appelé lors de la tentative de connexion
    @FXML
    protected void onLoginClick(ActionEvent event) throws SQLException, IOException {
        //Récupération de l'utilisateur se connectant
        User user = User.getUserFromMail(usernameText.getText());

        //Mail absent de la BDD -> ERREUR
        if(user == null) {
            errorLabel.setText("No user with such mail");
            errorLabel.setVisible(true);
            return;
        }

        //Calcul du hash du mot de passe rempli
        String hashInputPassword = LoginUtils.hashFunction(passText.getText(), user.getPasswordSalt(), MainApplication.pepper, MainApplication.hashIncrementation);

        //Mot de passe correspondant -> CONNEXION
        if (Objects.equals(user.getHashPassword(), hashInputPassword)) {
            //Initialisation du header
            MainApplication.header = new Header(user);

            //Changement de view vers l'accueil
            HomeController homeController = new HomeController(MainApplication.fstPageHome, MainApplication.sndPageHome);
            MainApplication.switchScene(event, "fxml/home-view.fxml", homeController);
        }

        //Mot de passe incorrect -> ERREUR
        else {
            errorLabel.setText("Wrong password");
            errorLabel.setVisible(true);
        }

    }
}