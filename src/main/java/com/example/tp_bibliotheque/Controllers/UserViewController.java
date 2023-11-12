package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Login.LoginUtils;
import com.example.tp_bibliotheque.Objects.*;
import com.example.tp_bibliotheque.MainApplication;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;

//CONTROLLER DE LA VIEW UTILISATEUR

//Classe des sections de modification des informations de l'utilisateur
class ModifSection {
    //*****************ATTRIBUTS*****************//

    //Utilisateur actuellement connecté
    private final User user;

    //Label : information de l'utilisateur à modifier
    private final Label userLabel;

    //Button : initie la modification
    private final Button modifButton;

    //PasswordField : champ du mot de passe
    private final PasswordField checkPassword;

    //TextField : champ de la mise à jour de l'information
    private final TextField textField;

    //TextField : champ de confirmation de la mise à jour de l'information
    private final TextField confirmTextField;

    //Label : erreur lors de la modification
    private final Label error;

    //Button : lance la modification
    private final Button changeButton;

    //Type de l'information à modifier (mail, mdp, etc)
    private final String type;


    //*****************METHODES*****************//

    //Constructeur de classe
    public ModifSection(User _user, Label _userLabel, String _type, GridPane parent, int rowIdx) {
        user = _user;
        userLabel = _userLabel;
        type = _type;

        //Initialisation des interfaces graphiques nécessaires
        modifButton = new Button("Modify");

        checkPassword = new PasswordField();
        checkPassword.setPromptText("Password");

        //Modification du mot de passe -> Utilisation de PasswordField
        if(type.equals("password")) {
            textField = new PasswordField();
            confirmTextField = new PasswordField();
        }

        //Autre modification -> Utilisation de TextField
        else {
            textField = new TextField();
            confirmTextField = new TextField();
        }

        textField.setPromptText("New "+type);
        confirmTextField.setPromptText("Confirm "+type);

        error = new Label();

        changeButton = new Button("Change");

        error.setVisible(false);
        checkPassword.setVisible(false);
        textField.setVisible(false);
        confirmTextField.setVisible(false);
        changeButton.setVisible(false);

        modifButton.setOnAction(event -> {
            onModifClick();
        });

        changeButton.setOnAction(event -> {
            onChangeClick();
        });

        error.getStyleClass().add("errorMess");
        checkPassword.getStyleClass().add("textField");
        textField.getStyleClass().add("textField");
        confirmTextField.getStyleClass().add("textField");
        modifButton.getStyleClass().add("buttonModif");
        changeButton.getStyleClass().add("buttonModif");

        //Ajout de l'interface graphique au parent correspondant
        parent.add(error, 2, rowIdx);
        parent.add(modifButton, 3, rowIdx);
        parent.add(checkPassword, 4, rowIdx);
        parent.add(textField, 5, rowIdx);
        parent.add(confirmTextField, 6, rowIdx);
        parent.add(changeButton, 7, rowIdx);
    }

    //Méthode appelée lors de l'initiation de la modification
    private void onModifClick() {
        //Mise à jour de l'interface graphique
        modifButton.setText("Cancel");

        checkPassword.setVisible(true);
        checkPassword.setText("");
        textField.setVisible(true);
        textField.setText("");
        confirmTextField.setVisible(true);
        confirmTextField.setText("");

        error.setVisible(false);
        changeButton.setVisible(true);

        modifButton.setOnAction(event -> {
            onCancelClick();
        });
    }

    //Méthode appelée pour lancer la modification
    private void onChangeClick() {
        //Récupération de la nouvelle valeur de l'information
        String newVal = textField.getText();

        //Champ de confirmation différent -> ERREUR
        if(!newVal.equals(confirmTextField.getText())) {
            //Annulation de la modification
            onCancelClick();

            //Mise à jour de l'interface graphique
            error.setText("Different values");
            error.setVisible(true);

            modifButton.setOnAction(event -> {
                onModifClick();
            });

            //Sortie de la méthode
            return;
        }

        //Calcul du hash du mot de passe entré
        String hashPassword = LoginUtils.hashFunction(checkPassword.getText(), user.getPasswordSalt(), MainApplication.pepper, MainApplication.hashIncrementation);

        //Mot de passe invalide -> ERREUR
        if(!hashPassword.equals(user.getHashPassword())) {
            //Annulation de la modification
            onCancelClick();

            //Mise à jour de l'interface graphique
            error.setText("Wrong Password");
            error.setVisible(true);

            //Sortie de la méthode
            return;
        }

        //Lancement de la modification
        switch(type) {
            //Modification du mail
            case "mail":
                //Mail valide
                if(LoginUtils.isValidMail(newVal)) {
                    //Nouveau mail -> MODIFICATION
                    try {
                        user.setMail(newVal);
                        userLabel.setText(newVal);
                    }

                    //Mail déjà dans la BDD -> ERREUR
                    catch (SQLException e) {
                        error.setText("Mail already linked with an account");
                        error.setVisible(true);
                    }
                }

                //Mail invalide -> ERREUR
                else {
                    error.setText("Invalid Mail");
                    error.setVisible(true);
                }
                break;

            //Modification du mot de passe
            case "password":
                try {
                    user.setPassword(LoginUtils.hashFunction(newVal, user.getPasswordSalt(), MainApplication.pepper, MainApplication.hashIncrementation));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }

        //Mise à jour de l'interface graphique comme une annulation une fois la modification réussie
        onCancelClick();
    }

    //Méthode appelée lors de l'annulation de la modification
    public void onCancelClick() {
        //Mise à jour de l'interface graphique
        modifButton.setText("Modify");

        checkPassword.setVisible(false);
        textField.setVisible(false);
        confirmTextField.setVisible(false);

        changeButton.setVisible(false);

        modifButton.setOnAction(event -> {
            onModifClick();
        });
    }
}

public class UserViewController extends ApplicationController {
    //*****************ATTRIBUTS*****************//

    //Label : mail de l'utilisateur
    @FXML private Label mailLabel;

    //Label : prénom de l'utilisateur
    @FXML private Label nameLabel;

    //Label : nom de famille de l'utilisateur
    @FXML private Label lastNameLabel;

    //Label : mot de passe de l'utilisateur
    @FXML private Label passwordLabel;

    //Label : catégorie de l'utilisateur
    @FXML private Label catLabel;

    //Label : bibliothèque de l'utilisateur
    @FXML private Label libraryLabel;

    //GridPane : informations de l'utilisateur
    @FXML private GridPane infoGrid;

    //GridPane : emprunts de l'utilisateur
    @FXML private GridPane empruntGrid;

    //ELements relatifs au système de page d'élements à afficher
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page empruntsPage;

    //Utilisateur actuellement connecté
    private final User user;


    //*****************METHODES*****************//

    //Constructeur de classe
    public UserViewController(User _user) {
        user = _user;
    }

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    public void initialize() throws SQLException {
        UserViewController.super.initialize();

        //Initialisation de l'interface graphique
        mailLabel.setText(user.getMail());
        nameLabel.setText(user.getName());
        lastNameLabel.setText(user.getLastName());
        catLabel.setText(user.getCategorie().toString());
        libraryLabel.setText(Library.getLibraryFromId(user.getLibraryId()).getName());

        //L'utilisateur connecté est celui sur la page -> Possibilité de modifier ses informations
        if(user.getId() == MainApplication.header.getUser().getId()) {
            //Initialisation des sections de modifications des informations de l'utilisateur
            new ModifSection(user, mailLabel, "mail", infoGrid, 0);
            new ModifSection(user, passwordLabel, "password", infoGrid, 3);
        }

        //L'utilisateur est un administrateur -> Possibilité de changer la bibliothèque
        if(user.getCategorie().equals(Categorie.Bibliothécaire)) {
            //Label de bibliothèque remplacé par une ChoiceBox
            libraryLabel.setVisible(false);

            ChoiceBox<Library> chooseLibrary = new ChoiceBox<Library>();

            for(int i = 0;i<Library.countLibrary();i++) {
                chooseLibrary.getItems().addAll(Library.getLibraryFromId(i+1));
            }
            chooseLibrary.setValue(Library.getLibraryFromId(1));

            infoGrid.add(chooseLibrary, 1, 5);
        }

        //Initialisation de la page d'emprunts à afficher
        empruntsPage = new Page(prevButton, nextButton, Emprunt.getAllEmpruntsFromUser(user.getId()), empruntGrid, pageLabel);
    }
}
