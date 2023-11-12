package com.example.tp_bibliotheque.Login;

import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.Objects.Library;
import com.example.tp_bibliotheque.Objects.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.SQLException;

//CONTROLLER DE LA VIEW INSCRIPTION

public class SignUpController {
    //*****************ATTRIBUTS*****************//

    //TextField : champ du mail
    @FXML private TextField mailSignUp;

    //TextField : champ du prénom
    @FXML private TextField nameSignUp;

    //TextField : champ du nom de famille
    @FXML private TextField lastNameSignUp;

    //PasswordField : champ du mot de passe
    @FXML private PasswordField passSignUp;

    //PasswordField : champ de confirmation du mot de passe
    @FXML private PasswordField confirmPassSignUp;

    //ChoiceBox : choix de la bibliothèque
    @FXML private ChoiceBox<Library> chooseLibrary;

    //Button : redirige vers la page de connexion
    @FXML private Button goToLogin;

    //Label : erreur d'inscription
    @FXML private Label errorLabel;


    //*****************METHODES*****************//

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    @FXML
    public void initialize() throws SQLException {
        try {
            for(int i = 0;i<Library.countLibrary();i++) {
                chooseLibrary.getItems().addAll(Library.getLibraryFromId(i+1));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        chooseLibrary.setValue(Library.getLibraryFromId(1));

        //Changement de view lors de la redirection vers la page de connexion
        goToLogin.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "fxml/login-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        errorLabel.setVisible(false);
    }

    //Méthode appelée lors de la tentative d'inscription
    @FXML
    protected void onSignUp(ActionEvent event) {
        //Récupération des différents champs remplis
        String mail = mailSignUp.getText();
        String name = nameSignUp.getText();
        String lastName = lastNameSignUp.getText();
        String password = passSignUp.getText();
        String confirmPassword = confirmPassSignUp.getText();
        Library library = chooseLibrary.getValue();

        //Un champ vide -> ERREUR
        if(mail.isEmpty() || name.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorLabel.setText("Fill every fields");
            errorLabel.setVisible(true);
            return;
        }

        //Mail invalide -> ERREUR
        if(!LoginUtils.isValidMail(mail)) {
            errorLabel.setText("Invalid Mail");
            errorLabel.setVisible(true);
            return;
        }

        //Mots de passe différents -> ERREUR
        if(!password.equals(confirmPassword)) {
            errorLabel.setText("Different passwords");
            errorLabel.setVisible(true);
            return;
        }

        //Initialisation du salt et calcul du hash du mot de passe du nouvel utilisateur
        String userSalt = LoginUtils.Salt();
        String hashPassword = LoginUtils.hashFunction(password, userSalt, MainApplication.pepper, MainApplication.hashIncrementation);

        //Nouveau mail -> INSCRIPTION
        try {
            //Ajout de l'utilisateur à la BDD
            User.addUser(mail, name, lastName, hashPassword, userSalt,"1", library.getId());

            //Redirection vers la page de connexion
            MainApplication.switchScene(event, "fxml/login-view.fxml");
        }

        //Mail déjà dans la BDD -> ERREUR
        catch(Exception e) {
            errorLabel.setText("Account already exists");
            errorLabel.setVisible(true);
        }
    }

}
