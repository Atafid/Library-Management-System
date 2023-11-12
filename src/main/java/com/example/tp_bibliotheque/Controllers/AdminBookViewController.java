package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Objects.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.sql.Date;
import java.sql.SQLException;

//CONTROLLER DE LA VIEW ADMIN BOOK

public class AdminBookViewController extends ApplicationController {
    //*****************ATTRIBUTS*****************//

    //GridPane : champs de mouvements de livre
    @FXML
    private GridPane moveGrid;

    //TextField : ISBN de l'exemplaire à bouger
    @FXML
    private TextField moveIsbnField;

    //ChoiceBox : Bibliothèque de provenance de l'exemplaire à bouger
    @FXML
    private ChoiceBox<Library> fromLibrary;

    //ChoiceBox : Bibliothèque d'arrivée de l'exemplaire à bouger
    @FXML
    private ChoiceBox<Library> toLibrary;

    //TextField : Quantité de l'exemplaire à bouger
    @FXML
    private TextField moveQtyField;

    //Button : bouger l'exemplaire
    @FXML
    private Button moveButton;

    //Label : message d'erreur de mouvement
    @FXML
    private Label moveMess;

    //GridPane : champs d'ajout de livre
    @FXML
    private GridPane addGrid;

    //TextField : titre du livre à ajouter
    @FXML
    private TextField titleField;

    //TextField : genres du livre à ajouter
    @FXML
    private TextField genresField;

    //TextArea : description du livre à ajouter
    @FXML
    private TextArea descField;

    //TextField : cover du livre à ajouter
    @FXML
    private TextField coverField;

    //GridPane : boutons d'ajout et de suppression d'auteurs du livre à ajouter
    @FXML
    private GridPane addRemAuthorButtons;

    //Button : ajout d'auteur du livre à ajouter
    @FXML
    private Button addAuthorButton;

    //Button : suppression d'auteur du livre à ajouter
    @FXML
    private Button removeAuthorButton;

    //GridPane : boutons d'ajout et de suppression d'éditions du livre à ajouter
    @FXML
    private GridPane addRemEdiButtons;

    //Button : ajout d'une édition du livre à ajouter
    @FXML
    private Button addEdiButton;

    //Button : suppression d'une édition du livre à ajouter
    @FXML
    private Button removeEdiButton;

    //ChoiceBox : bibliothèque où ajouter le livre
    @FXML
    private ChoiceBox<Library> libraryChoiceBox;

    //Label : erreur lors de l'ajout
    @FXML
    private Label addMess;

    //L'administrateur actuellement connecté sur la page
    private final User admin;


    //*****************METHODES*****************//

    //Constructeur de classe
    public AdminBookViewController(User _admin) {
        admin = _admin;
    }

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    public void initialize() throws SQLException {
        AdminBookViewController.super.initialize();

        //Remplissage des ChoiceBox de bibliothèques
        for(int i = 0;i<Library.countLibrary();i++) {
            fromLibrary.getItems().addAll(Library.getLibraryFromId(i+1));
        }
        fromLibrary.setValue(Library.getLibraryFromId(1));

        for(int i = 0;i<Library.countLibrary();i++) {
            toLibrary.getItems().addAll(Library.getLibraryFromId(i+1));
        }
        toLibrary.setValue(Library.getLibraryFromId(1));

        for(int i = 0;i<Library.countLibrary();i++) {
            libraryChoiceBox.getItems().addAll(Library.getLibraryFromId(i+1));
        }
        libraryChoiceBox.setValue(Library.getLibraryFromId(1));

        //Message d'erreur et boutons de suppression non visibles
        moveMess.setVisible(false);
        addMess.setVisible(false);

        removeAuthorButton.setVisible(false);
        removeEdiButton.setVisible(false);
    }

    //Méthode permettant d'obtenir le noeud d'un GridPane à partir de ses indices de ligne et colonne
    private Node getNodeGridPane(GridPane grid, int rowIdx, int colIdx) {
        //Parcours des enfants du GridPane jusqu'au bon
        for(Node node : grid.getChildren()) {
            if(GridPane.getRowIndex(node) != null && GridPane.getRowIndex(node)==rowIdx && GridPane.getColumnIndex(node)!=null && GridPane.getColumnIndex(node)==colIdx) {
                return(node);
            }
        }

        return(null);
    }

    //Méthode appelée lors du mouvement d'un livre
    @FXML
    public void onMoveClick() throws SQLException {
        //Récupération de l'exemplaire à bouger
        PrintedWork printedWorkToChange;
        try {
            printedWorkToChange = PrintedWork.getPrintedWorkFromIsbnAndLibrary(moveIsbnField.getText(), fromLibrary.getValue().getId());
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        //Exemplaire inexistant -> ERREUR
        if(printedWorkToChange==null) {
            moveMess.setText("Invalid Informations");
            moveMess.setVisible(true);
            return;
        }

        //Quantité à bouger
        int qtyToMove = Integer.parseInt(moveQtyField.getText());
        int toLibraryId = toLibrary.getValue().getId();

        //Trop grande quantité -> ERREUR
        if(printedWorkToChange.getAvailableQty()<qtyToMove) {
            moveMess.setText("Too much books");
            moveMess.setVisible(true);
            return;
        }

        //Exemplaire(s) déjà présent dans la bibliothèque d'arrivée ou non
        PrintedWork newPrintedWork = PrintedWork.getPrintedWorkFromIsbnAndLibrary(moveIsbnField.getText(), toLibraryId);

        //Exemplaire(s) déjà présent -> Ajout de la quantité voulue
        if(newPrintedWork!=null) {
            newPrintedWork.addQtyPrintedWork(qtyToMove);
        }

        //Exemplaire inexistante -> Ajout à la BDD
        else {
            PrintedWork.addPrintedWork(printedWorkToChange.getEdition().getIsbn(), toLibraryId, qtyToMove);
        }

        //Quantité retirée à l'exemplaire de départ
        printedWorkToChange.removeQtyPrintedWork(qtyToMove);

        //Message de succès
        moveMess.setText("Book(s) moved");
        moveMess.getStyleClass().add("success");
        moveMess.setVisible(true);
    }

    //Méthode appelée lors de l'ajout d'un livre
    @FXML
    public void onAddBook() throws SQLException {
        //Récupération des informations du livre à ajouter
        String title = titleField.getText();
        String[] genres = genresField.getText().split(",");
        String description = descField.getText();
        String coverImg = coverField.getText();

        //Information(s) manquante(s) -> ERREUR
        if(title.isEmpty() || genres.length==0 || description.isEmpty() || coverImg.isEmpty()) {
            addMess.setText("Invalid Informations for book");
            addMess.setVisible(true);
            return;
        }

        //Nettoyage des gens au bon format
        String genresBDD;
        if(genres.length>=2) {
            genresBDD = "['"+genres[0]+"', ";
            for(int j=1;j<genres.length-1;j++) {
                genresBDD += "'"+genres[j].substring(1)+"', ";
            }
            genresBDD += "'"+genres[genres.length-1].substring(1)+"']";
        }

        else {
            genresBDD="["+genres[0]+"]";
        }

        //Ajout du livre à la BDD
        Book.addBook(title, description, genresBDD, coverImg);
        int bookId = Book.getLastId();

        //Parcours des auteurs du livre à ajouter
        int buttonColumnAuthor = GridPane.getColumnIndex(addRemAuthorButtons);
        for(int i=0;i<buttonColumnAuthor;i++) {
            //Récupération des informations de l'auteur
            GridPane authorField = (GridPane) getNodeGridPane(addGrid, 1, i);

            TextField roleField = (TextField) getNodeGridPane(authorField, 3, 0);
            CheckBox newAuthorCheck = (CheckBox) getNodeGridPane(authorField, 4, 0);

            String role = roleField.getText();
            Boolean isNew = newAuthorCheck.isSelected();

           int authorId;
           //Si l'auteur est nouveau -> Ajout à la BDD
           if(isNew) {
               TextField nameField = (TextField) getNodeGridPane(authorField, 0, 0);
               TextField lastNameField = (TextField) getNodeGridPane(authorField, 1, 0);
               DatePicker birthDatePicker = (DatePicker) getNodeGridPane(authorField, 2, 0);

               String name = nameField.getText();
               String lastName = lastNameField.getText();
               Date birthDate= Date.valueOf(birthDatePicker.getValue());

               //Information(s) manquante(s) -> ERREUR
               if(name.isEmpty() || lastName.isEmpty() || role.isEmpty() || birthDate.toString().isEmpty()) {
                   addMess.setText("Invalid Informations for author");
                   addMess.setVisible(true);
                   return;
               }

               //Ajout de l'auteur à la BDD
               Author.addAuthor(name, lastName, birthDate);
               authorId = Author.getLastId();
           }

           //Auteur déjà existant
           else {
               TextField idField = (TextField) getNodeGridPane(authorField, 0, 0);
               String idText = idField.getText();

               //Information(s) manquante(s) -> ERREUR
               if(idText.isEmpty()) {
                   addMess.setText("Invalid Informations for author");
                   addMess.setVisible(true);
                   return;
               }

               //Récupération de l'ID de l'auteur
               authorId = Integer.parseInt(idText);
           }

           //Ajout de la relation livre/auteur à la BDD
           HasWritten.addHasWritten(bookId, authorId, role);
        }

        //Parcours des éditions du livre à ajouter
        int buttonColumnEdi = GridPane.getColumnIndex(addRemEdiButtons);
        for(int i=0;i<buttonColumnAuthor;i++) {

            //Récupération des informations de l'édition
            GridPane ediField = (GridPane) getNodeGridPane(addGrid, 2, i);

            TextField isbnField = (TextField) getNodeGridPane(ediField, 0, 0);
            TextField editorField = (TextField) getNodeGridPane(ediField, 1, 0);
            DatePicker publishDateField = (DatePicker) getNodeGridPane(ediField, 2, 0);
            Slider qtySlider = (Slider) getNodeGridPane(ediField, 3, 0);
            ChoiceBox<Library> choiceLibrary = (ChoiceBox<Library>) getNodeGridPane(ediField, 4, 0);

            String isbn = isbnField.getText();
            String editor = editorField.getText();
            Date publishDate = Date.valueOf(publishDateField.getValue());
            int qty = (int) qtySlider.getValue();
            Library library = choiceLibrary.getValue();

            //Information(s) manquante(s) -> ERREUR
            if(isbn.isEmpty() || editor.isEmpty() || publishDate.toString().isEmpty()) {
                addMess.setText("Invalid Informations for edition");
                addMess.setVisible(true);
                return;
            }

            //Ajout de l'édition et des exemplaire à la BDD
            Edition.addEdition(isbn, bookId, editor, publishDate);
            PrintedWork.addPrintedWork(isbn, library.getId(), qty);
        }

        addMess.setText("Book successfully added");
        addMess.getStyleClass().add("success");
        addMess.setVisible(true);
        return;
    }

    //Méthode permettant de remplir la grille des informations d'un nouvel auteur
    private void addAuthorFieldGrid(GridPane grid) {
        //Champs du nouvel auteur
        TextField nameField = new TextField();
        nameField.setPromptText("Author Name");

        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Author Last Name");

        DatePicker birthDate = new DatePicker();
        birthDate.setPromptText("Birth Date");

        TextField roleField = new TextField();
        roleField.setPromptText("Author Role");

        CheckBox newAuthor = new CheckBox("New Author");
        newAuthor.setSelected(true);
        newAuthor.setOnAction(event -> {
            onNewClick(event);
        });

        //Ajout à l'interface graphique
        grid.addColumn(0, nameField, lastNameField, birthDate, roleField, newAuthor);
    }

    //Méthode appelée lors de l'appui sur la CheckBox New d'un auteur
    @FXML
    public void onNewClick(ActionEvent e) {
        //Récupération de la grille d'informations
        CheckBox checkBox = (CheckBox) e.getSource();
        GridPane fieldsGrid = (GridPane) checkBox.getParent();
        fieldsGrid.getChildren().clear();

        //L'auteur est nouveau -> appel de la méthode correspondante
        if(checkBox.isSelected()) {
            addAuthorFieldGrid(fieldsGrid);
        }

        //Auteux existant -> mise à jour de l'interface graphique
        else {
            //Champ de l'auteur existant (recherche par ID)
            TextField idField = new TextField();
            idField.setPromptText("ID");

            TextField roleField = new TextField();
            roleField.setPromptText("Author Role");

            CheckBox newAuthor = new CheckBox("New Author");
            newAuthor.setSelected(false);
            newAuthor.setOnAction(event -> {
                onNewClick(event);
            });

            //Ajout à l'interface graphique
            fieldsGrid.add(idField, 0, 0);
            fieldsGrid.add(roleField, 0, 3);
            fieldsGrid.add(newAuthor, 0, 4);
        }
    }

    //Méthode appelée lors de l'ajout d'un auteur
    @FXML
    public void onAddAuthor() {
        //Dernière colonne actuellement occupée
        int buttonColumn = GridPane.getColumnIndex(addRemAuthorButtons);

        //Ajout des boutons d'ajout et de suppression une colonne plus loin
        addGrid.getChildren().remove(addRemAuthorButtons);
        addGrid.add(addRemAuthorButtons, buttonColumn+1, 1);

        //Méthode spécifique
        GridPane newAuthorFields = new GridPane();
        newAuthorFields.getStyleClass().add("gridAuthor");
        addAuthorFieldGrid(newAuthorFields);

        //Ajout à l'interface graphique
        addGrid.add(newAuthorFields, buttonColumn, 1);

        //Si trop d'auteurs -> bouton d'ajout non visible
        if(buttonColumn+1==4) {
            addAuthorButton.setVisible(false);
        }

        //Bouton de suppression d'auteur visible
        removeAuthorButton.setVisible(true);
    }

    //Méthode appelée lors de la suppression d'un auteur
    @FXML
    public void onRemoveAuthor() {
        //Dernière colonne actuellement occupée
        int buttonColumn = GridPane.getColumnIndex(addRemAuthorButtons);

        //Suppression du dernier auteur
        Node lastAuthorField = getNodeGridPane(addGrid, 1, buttonColumn-1);
        addGrid.getChildren().remove(lastAuthorField);

        //Ajout des boutons d'ajout et de suppression une colonne avant
        addGrid.getChildren().remove(addRemAuthorButtons);
        addGrid.add(addRemAuthorButtons, buttonColumn-1, 1);

        //Si un seul auteur -> bouton de suppression non visible
        if(buttonColumn==0) {
            removeAuthorButton.setVisible(false);
        }

        //Bouton d'ajout visible
        addAuthorButton.setVisible(true);
    }

    //Méthode appelée lors de l'ajout d'une édition
    @FXML
    public void onAddEdi() throws SQLException {
        //Dernière colonne actuellement occupée
        int buttonColumn = GridPane.getColumnIndex(addRemEdiButtons);

        //Ajout des boutons d'ajout et de suppression un colonne plus loin
        addGrid.getChildren().remove(addRemEdiButtons);
        addGrid.add(addRemEdiButtons, buttonColumn+1, 2);

        //Champs à ajouter
        GridPane newEdiFields = new GridPane();
        newEdiFields.getStyleClass().add("gridEdition");

        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");

        TextField editorNameField = new TextField();
        editorNameField.setPromptText("Editor Name");

        DatePicker publishDate = new DatePicker();
        publishDate.setPromptText("Publish Date");

        Slider qtySlider = new Slider();
        qtySlider.setMax(5);
        qtySlider.setMin(1);
        qtySlider.setShowTickMarks(true);
        qtySlider.setShowTickLabels(true);
        qtySlider.setSnapToTicks(true);
        qtySlider.setBlockIncrement(1);
        qtySlider.setMajorTickUnit(1);
        qtySlider.setMinorTickCount(0);
        qtySlider.setValue(1);

        ChoiceBox<Library> chooseLibrary = new ChoiceBox<>();
        for(int i = 0;i<Library.countLibrary();i++) {
            chooseLibrary.getItems().addAll(Library.getLibraryFromId(i+1));
        }
        chooseLibrary.setValue(Library.getLibraryFromId(1));

        newEdiFields.addColumn(0, isbnField, editorNameField, publishDate, qtySlider, chooseLibrary);

        //Ajout à l'interface graphique
        addGrid.add(newEdiFields, buttonColumn, 2);

        //Trop d'éditions -> bouton d'ajout non visible
        if(buttonColumn+1==4) {
            addEdiButton.setVisible(false);
        }

        //Bouton de suppression visible
        removeEdiButton.setVisible(true);
    }

    //Méthode appelée lors de la suppression d'une édition
    @FXML
    public void onRemoveEdi() {
        //Dernière colonne actuellement occupée
        int buttonColumn = GridPane.getColumnIndex(addRemEdiButtons);

        //Suppression de la dernière édition
        Node lastEdiField = getNodeGridPane(addGrid, 2, buttonColumn-1);
        addGrid.getChildren().remove(lastEdiField);

        //Ajout des boutons d'ajout et de suppression une colonne avant
        addGrid.getChildren().remove(addRemEdiButtons);
        addGrid.add(addRemEdiButtons, buttonColumn-1, 2);

        //Si une seule édition -> bouton de suppression non visible
        if(buttonColumn==0) {
            removeEdiButton.setVisible(false);
        }

        //Bouton d'ajout visible
        addEdiButton.setVisible(true);
    }
}
