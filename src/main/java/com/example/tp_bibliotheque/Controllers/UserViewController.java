package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.LoginPage;
import com.example.tp_bibliotheque.Objects.*;
import com.example.tp_bibliotheque.MainApplication;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

class ModifButton {
    private User user;
    private Label userLabel;
    private Button button;
    private PasswordField checkPassword;
    private TextField textField;
    private TextField confirmTextField;
    private Label error;
    private Button change;
    private String type;

    public ModifButton(User _user, Label _userLabel, Button _button, PasswordField _checkPassword, TextField _textField,
                       TextField _confirmTextField, Label _error, Button _change, String _type) {
        user = _user;
        userLabel = _userLabel;
        button = _button;
        checkPassword = _checkPassword;
        textField = _textField;
        confirmTextField = _confirmTextField;
        error = _error;
        change = _change;
        type = _type;

        error.setVisible(false);
        checkPassword.setVisible(false);
        textField.setVisible(false);
        confirmTextField.setVisible(false);
        change.setVisible(false);

        button.setOnAction(event -> {
            onModifClick();
        });

        change.setOnAction(event -> {
            onTextEnter();
        });

    }

    private void onModifClick() {
        button.setText("Cancel");

        checkPassword.setVisible(true);
        checkPassword.setText("");
        textField.setVisible(true);
        textField.setText("");
        confirmTextField.setVisible(true);
        confirmTextField.setText("");

        error.setVisible(false);
        change.setVisible(true);

        button.setOnAction(event -> {
            onCancelClick();
        });
    }

    private void onTextEnter() {
        String newVal = textField.getText();

        if(!newVal.equals(confirmTextField.getText())) {
            onCancelClick();

            error.setText("Different values");
            error.setVisible(true);

            button.setOnAction(event -> {
                onModifClick();
            });
            return;
        }

        String hashPassword = LoginPage.hashFunction(checkPassword.getText(), user.getPasswordSalt(), MainApplication.pepper, MainApplication.hashIncrementation);

        if(!hashPassword.equals(user.getHashPassword())) {
            onCancelClick();

            error.setText("Wrong Password");
            error.setVisible(true);

            return;
        }

        switch(type) {
            case "mail":
                if(LoginPage.isValidMail(newVal)) {
                    try {
                        user.setMail(newVal);
                        userLabel.setText(newVal);
                    } catch (SQLException e) {
                        error.setText("Mail already linked with an account");
                        error.setVisible(true);
                    }
                }

                else {
                    error.setText("Invalid Mail");
                    error.setVisible(true);
                }
                break;

            case "password":
                try {
                    user.setPassword(LoginPage.hashFunction(newVal, user.getPasswordSalt(), MainApplication.pepper, MainApplication.hashIncrementation));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                break;
        }

        onCancelClick();

        return;
    }

    public void onCancelClick() {
        button.setText("Modify");

        checkPassword.setVisible(false);
        textField.setVisible(false);
        confirmTextField.setVisible(false);

        change.setVisible(false);

        button.setOnAction(event -> {
            onModifClick();
        });
    }
}

public class UserViewController {
    @FXML private AnchorPane root;
    @FXML private Label mailLabel;
    @FXML private Label nameLabel;
    @FXML private Label lastNameLabel;
    @FXML private Label passwordLabel;
    @FXML private Label errorMail;
    @FXML private Label errorPass;
    @FXML private Button modifMail;
    @FXML private Button modifPass;
    @FXML private PasswordField passMail;
    @FXML private PasswordField passPass;
    @FXML private TextField newMail;
    @FXML private TextField newPass;
    @FXML private TextField confMail;
    @FXML private TextField confPass;
    @FXML private Button changeMail;
    @FXML private Button changePass;

    @FXML private GridPane empruntGrid;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page empruntsPage;

    private User user;

    public UserViewController(User _user) {
        user = _user;
    }

    public void initialize() throws SQLException {
        root.getChildren().add(MainApplication.header.getHead());

        mailLabel.setText(user.getMail());
        nameLabel.setText(user.getName());
        lastNameLabel.setText(user.getLastName());

        new ModifButton(user, mailLabel, modifMail, passMail, newMail, confMail, errorMail, changeMail, "mail");
        new ModifButton(user, passwordLabel, modifPass, passPass, newPass, confPass, errorPass, changePass, "password");

        empruntsPage = new Page(prevButton, nextButton, Emprunt.getAllEmpruntsFromUser(user.getId()), empruntGrid, pageLabel);
    }
}
