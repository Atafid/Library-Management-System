package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Objects.Book;
import com.example.tp_bibliotheque.MainApplication;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Vector;

//CONTROLLER DE LA VIEW ACCUEIL

public class HomeController extends ApplicationController {
    //*****************ATTRIBUTS*****************//

    //GridPane : livres à afficher
    @FXML private GridPane bookGrid;

    //Elements relatifs au système de page d'éléments à afficher
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Vector<Book> previousPage;
    private Vector<Book> currentPage;
    private Vector<Book> nextPage;
    private int pageCount;
    private Boolean dispNextPage;
    private Boolean dispPrevPage;


    //*****************METHODES*****************//

    //Constructeur de classe
    public HomeController(Vector<Book> fstPage, Vector<Book> sndPage) {
        //On fournit les premières pages de livres déjà chargées en amont pour gagner en fluidité
        currentPage = fstPage;
        nextPage = sndPage;

        previousPage = new Vector<Book>();
        pageCount = 1;
        dispNextPage = true;
        dispPrevPage = true;
    }

    //Fonction se lançant à l'initialisation de javaFX juste après le constructeur
    public void initialize() throws SQLException {
        HomeController.super.initialize();

        //Ajout des livres à afficher à l'interface graphique
        for(int i=0;i<5;i++) {
            dispBook(currentPage.get(i), bookGrid, i, 0);
            dispBook(currentPage.get(5+i), bookGrid, i, 2);
        }

        //Initialisation des éléments de changement de page
        pageLabel.setText(String.valueOf(pageCount));
        previousButton.setVisible(false);
    }

    //Méthode permettant d'afficher un livre sur la fenêtre
    private void dispBook(Book book, GridPane parent, int column, int row) {
        //Chargement de la couverture du livre
        ImageView imageView = new ImageView(book.getCoverImg());

        imageView.getStyleClass().add("cover_image");
        imageView.setFitHeight(150);
        imageView.setFitWidth(93);

        //Ajout du boutton redirigeant vers la view du livre
        Button titleButton = new Button();
        titleButton.setText(book.getTitle());
        try {
            //Calcul et affichage de la note moyenne du livre
            double avgNote = book.getAverageNote();

            //Condition vérifiant que le livre ait au moins été noté une fois
            if(avgNote>-1) {
                DecimalFormat df = new DecimalFormat("#0.0");
                titleButton.setText(titleButton.getText()+", "+df.format(avgNote));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        titleButton.wrapTextProperty().setValue(true);
        titleButton.getStyleClass().add("book_title");

        BookViewController bookController = new BookViewController(book);
        titleButton.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "fxml/book-view.fxml", bookController);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        //Ajout du livre au parent de l'interface graphique
        parent.add(imageView, column, row);
        parent.add(titleButton, column, row+1);
    }

    //Méthode copiant une page de livres donnée
    private Vector<Book> copyPage(Vector<Book> original) {
        Vector<Book> res = new Vector<Book>();
        for(Book b:original) {
            res.add(b);
        }

        return(res);
    }

    //Méthode permettant la recherche de livres avancée
    @FXML
    public void onSearch(String titleSearch, String authorSearch, String keywords) throws SQLException {
        //Recherche vide -> retour à l'écran d'accueil
        if(titleSearch.isEmpty() && authorSearch.isEmpty() && keywords.isEmpty()) {
            //Reinitialisation de la grille de livres et libération de la mémoire
            bookGrid.getChildren().clear();
            System.gc();

            //Affichage de l'écran d'accueil
            for (int i = 0; i < 5; i++) {
                dispBook(currentPage.get(i), bookGrid, i, 0);
                dispBook(currentPage.get(5 + i), bookGrid, i, 2);
            }

            nextButton.setVisible(true);
            previousButton.setVisible(true);
            pageLabel.setVisible(true);
        }

        //Recherche non vide
        else {
            //Récupération des livres correspondants à la recherche
            Vector<Book> searchedBooks = Book.getBookFromSearch(titleSearch, authorSearch, keywords);

            //Réinitialisation de la grille de livres
            bookGrid.getChildren().clear();

            //Affichage des livres recherchés
            for (int i = 0; i < searchedBooks.size()/2; i++) {
                dispBook(searchedBooks.get(i), bookGrid, i, 0);
                dispBook(searchedBooks.get(searchedBooks.size()/2 + i), bookGrid, i, 2);
            }

            nextButton.setVisible(false);
            previousButton.setVisible(false);
            pageLabel.setVisible(false);
        }
    }

    //Méthode surchargée permettant de faire une recherche simple -> recherche avancée avec le même champ
    @FXML public void onSearch(String research) throws SQLException {
        //Recherche vide -> retour à l'écran d'accueil
        if(research.isEmpty()) {
            //Reinitialisation de la grille de livres et libération de la mémoire
            bookGrid.getChildren().clear();
            System.gc();

            //Affichage de l'écran d'accueil
            for (int i = 0; i < 5; i++) {
                dispBook(currentPage.get(i), bookGrid, i, 0);
                dispBook(currentPage.get(5 + i), bookGrid, i, 2);
            }

            nextButton.setVisible(true);
            previousButton.setVisible(true);
            pageLabel.setVisible(true);
        }

        //Recherche non vide
        else {
            //Récupération des livres correspondants à la recherche
            Vector<Book> searchedBooks = Book.getBookFromSearch(research);

            //Réinitialisation de la grille de livres
            bookGrid.getChildren().clear();

            //Affichage des livres recherchés
            for (int i = 0; i < searchedBooks.size()/2; i++) {
                dispBook(searchedBooks.get(i), bookGrid, i, 0);
                dispBook(searchedBooks.get(searchedBooks.size()/2 + i), bookGrid, i, 2);
            }

            nextButton.setVisible(false);
            previousButton.setVisible(false);
            pageLabel.setVisible(false);
        }
    }

    //Méthode affichant la page suivante de livres
    @FXML
    private void onNextClick() {
        //On vérifie que les livres sont bien chargés en mémoire
        if(dispNextPage) {
            dispNextPage = false;
            bookGrid.getChildren().clear();

            // Supprimer les références aux anciens livres pour libérer la mémoire
            previousPage.clear();
            System.gc();

            //Affichage de la nouvelle page
            for (int i = 0; i < 5; i++) {
                dispBook(nextPage.get(i), bookGrid, i, 0);
                dispBook(nextPage.get(5 + i), bookGrid, i, 2);
            }

            //Mise à jour des informations relatives à la page
            pageCount++;
            pageLabel.setText(String.valueOf(pageCount));
            previousButton.setVisible(true);

            previousPage = copyPage(currentPage);
            currentPage = copyPage(nextPage);

            // Utiliser un Service pour charger les prochains livres en arrière-plan
            Service<Void> backgroundService = new Service<Void>() {
                @Override
                protected Task<Void> createTask() {
                    return new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            nextPage = Book.getPage(pageCount + 1);
                            dispNextPage = true;
                            return null;
                        }
                    };
                }
            };

            backgroundService.start();
        }
    }

    //Méthode permettant d'afficher la page précédente de livres
    @FXML
    private void onPreviousClick() {
        //On vérifie que les livres sont bien chargés en mémoire
        if(dispPrevPage) {
            dispPrevPage = false;
            bookGrid.getChildren().clear();

            // Supprimer les références aux anciens livres pour libérer la mémoire
            nextPage.clear();
            System.gc();

            //Affichage de la nouvelle page
            for (int i = 0; i < 5; i++) {
                dispBook(previousPage.get(i), bookGrid, i, 0);
                dispBook(previousPage.get(5 + i), bookGrid, i, 2);
            }

            //Mise à jour des informations relatives à la page
            pageCount--;
            pageLabel.setText(String.valueOf(pageCount));

            nextPage = copyPage(currentPage);
            currentPage = copyPage(previousPage);

            //Première page -> on ne peut plus afficher la page précédente
            if (pageCount == 1) {
                previousButton.setVisible(false);
                previousPage.clear();
                dispPrevPage = true;
            }

            //Numéro de page > 1
            else {
                // Utiliser un Service pour charger les prochains livres en arrière-plan
                Service<Void> backgroundService = new Service<Void>() {
                    @Override
                    protected Task<Void> createTask() {
                        return new Task<Void>() {
                            @Override
                            protected Void call() throws Exception {
                                previousPage = Book.getPage(pageCount - 1);
                                dispPrevPage = true;
                                return null;
                            }
                        };
                    }
                };

                backgroundService.start();
            }
        }
    }
}
