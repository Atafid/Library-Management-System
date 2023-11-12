package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.Objects.Library;
import com.example.tp_bibliotheque.Objects.PrintedWork;
import com.example.tp_bibliotheque.Objects.User;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.sql.SQLException;
import java.util.Vector;

public class AdminBookViewController {
    //AnchorPane : racine de la page
    @FXML
    private AnchorPane root;

    @FXML
    private GridPane moveGrid;

    @FXML
    private TextField moveIsbnField;

    @FXML
    private ChoiceBox<Library> fromLibrary;

    @FXML
    private ChoiceBox<Library> toLibrary;

    @FXML
    private TextField moveQtyField;

    @FXML
    private Button moveButton;

    @FXML
    private Label moveMess;

    @FXML
    private GridPane addGrid;

    @FXML
    private GridPane addRemButtons;

    @FXML
    private Button addAuthorButton;

    @FXML
    private Button removeAuthorButton;

    //L'administrateur actuellement connecté sur la page
    private final User admin;


    //*****************METHODES*****************//

    //Constructeur de classe
    public AdminBookViewController(User _admin) {
        admin = _admin;
    }

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    public void initialize() throws SQLException {
        //Ajout du header à la racine de la fenêtre
        root.getChildren().add(MainApplication.header.getHead());

        for(int i = 0;i<Library.countLibrary();i++) {
            fromLibrary.getItems().addAll(Library.getLibraryFromId(i+1));
        }
        fromLibrary.setValue(Library.getLibraryFromId(1));

        for(int i = 0;i<Library.countLibrary();i++) {
            toLibrary.getItems().addAll(Library.getLibraryFromId(i+1));
        }
        toLibrary.setValue(Library.getLibraryFromId(1));

        moveMess.setVisible(false);

        removeAuthorButton.setVisible(false);
    }

    @FXML
    public void onMoveClick() throws SQLException {
        PrintedWork printedWorkToChange;
        try {
            printedWorkToChange = PrintedWork.getPrintedWorkFromIsbnAndLibrary(moveIsbnField.getText(), fromLibrary.getValue().getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if(printedWorkToChange==null) {
            moveMess.setText("Invalid Informations");
            moveMess.setVisible(true);
            return;
        }

        int qtyToMove = Integer.parseInt(moveQtyField.getText());
        int toLibraryId = toLibrary.getValue().getId();

        if(printedWorkToChange.getAvailableQty()<qtyToMove) {
            moveMess.setText("Too much books");
            moveMess.setVisible(true);
            return;
        }

        PrintedWork newPrintedWork = PrintedWork.getPrintedWorkFromIsbnAndLibrary(moveIsbnField.getText(), toLibraryId);

        if(newPrintedWork!=null) {
            newPrintedWork.addQtyPrintedWork(qtyToMove);
        }

        else {
            PrintedWork.addPrintedWork(printedWorkToChange.getEdition().getIsbn(), toLibraryId, qtyToMove);
        }

        printedWorkToChange.removeQtyPrintedWork(qtyToMove);

        moveMess.setText("Book(s) moved");
        moveMess.setVisible(true);
    }

    @FXML
    public void onAddAuthor() {
        int buttonColumn = GridPane.getColumnIndex(addRemButtons);

        addGrid.getChildren().remove(addRemButtons);
        addGrid.add(addRemButtons, buttonColumn+1, 1);

        GridPane newAuthorFields = new GridPane();

        TextField nameField = new TextField();
        nameField.setPromptText("Author Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Author Last Name");

        TextField roleField = new TextField();
        roleField.setPromptText("Author Role");

        CheckBox newAuthor = new CheckBox("New Author");

        newAuthorFields.addColumn(0, nameField, lastNameField, roleField, newAuthor);

        addGrid.add(newAuthorFields, buttonColumn, 1);

        if(buttonColumn+1==4) {
            addAuthorButton.setVisible(false);
        }

        removeAuthorButton.setVisible(true);
    }

    @FXML
    public void onRemoveAuthor() {
        int buttonColumn = GridPane.getColumnIndex(addRemButtons);

        for(Node node : addGrid.getChildren()) {
            if(node instanceof GridPane && GridPane.getRowIndex(node)==1 && GridPane.getColumnIndex(node)!=null && GridPane.getColumnIndex(node)==buttonColumn-1) {
                addGrid.getChildren().remove(node);
                break;
            }
        }

        addGrid.getChildren().remove(addRemButtons);
        addGrid.add(addRemButtons, buttonColumn-1, 1);

        if(buttonColumn==0) {
            removeAuthorButton.setVisible(false);
        }

        addAuthorButton.setVisible(true);
    }
}
