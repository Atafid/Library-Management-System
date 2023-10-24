package com.example.tp_bibliotheque;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class HomeController {
    @FXML private AnchorPane root;
    @FXML private GridPane bookGrid;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;

    private Vector<Book> previousPage;
    private Vector<Book> currentPage;
    private Vector<Book> nextPage;
    private int pageCount;
    private Boolean dispNextPage;
    private Boolean dispPrevPage;

    public HomeController(Vector<Book> fstPage, Vector<Book> sndPage) {
        //MainApplication.bddConn.fillBDD();
        currentPage = fstPage;
        nextPage = sndPage;
        previousPage = new Vector<Book>();
        pageCount = 1;
        dispNextPage = true;
        dispPrevPage = true;
    }
    public void initialize() throws SQLException {
        root.getChildren().add(MainApplication.header.getHead());

        for(int i=0;i<5;i++) {
            dispBook(currentPage.get(i), bookGrid, i, 0);
            dispBook(currentPage.get(5+i), bookGrid, i, 2);
        }

        pageLabel.setText(String.valueOf(pageCount));
        previousButton.setVisible(false);
    }

    private void dispBook(Book book, GridPane parent, int column, int row) {
        ImageView imageView = new ImageView(book.getCoverImg());

        imageView.getStyleClass().add("cover_image");
        imageView.setFitHeight(150);
        imageView.setFitWidth(93);

        Button titleButton = new Button();
        titleButton.setText(book.getTitle());
        try {
            double avgNote = book.getAverageNote();
            if(avgNote>-1) {
                titleButton.setText(titleButton.getText()+", "+String.valueOf(avgNote));
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
        titleButton.wrapTextProperty().setValue(true);
        titleButton.getStyleClass().add("book_title");

        BookViewController bookController = new BookViewController(book);
        titleButton.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "book-view.fxml", bookController);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        parent.add(imageView, column, row);
        parent.add(titleButton, column, row+1);
    }
    private Vector<Book> copyPage(Vector<Book> original) {
        Vector<Book> res = new Vector<Book>();
        for(Book b:original) {
            res.add(b);
        }

        return(res);
    }

    @FXML
    protected void onSearch(String research) throws SQLException {
        if(research.isEmpty()) {
            bookGrid.getChildren().clear();
            System.gc();

            for (int i = 0; i < 5; i++) {
                dispBook(currentPage.get(i), bookGrid, i, 0);
                dispBook(currentPage.get(5 + i), bookGrid, i, 2);
            }

            nextButton.setVisible(true);
            previousButton.setVisible(true);
            pageLabel.setVisible(true);
        }
        else {
            Vector<Book> searchedBooks = Book.getBookFromSearch(research);

            bookGrid.getChildren().clear();

            for (int i = 0; i < searchedBooks.size()/2; i++) {
                dispBook(searchedBooks.get(i), bookGrid, i, 0);
                dispBook(searchedBooks.get(searchedBooks.size()/2 + i), bookGrid, i, 2);
            }

            nextButton.setVisible(false);
            previousButton.setVisible(false);
            pageLabel.setVisible(false);
        }
    }

    @FXML
    private void onNextClick() throws InterruptedException {
        //On vérifie que les livres sont bien chargés en mémoire
        if(dispNextPage) {
            dispNextPage = false;
            bookGrid.getChildren().clear();

            // Supprimer les références aux anciens livres pour libérer la mémoire
            previousPage.clear();
            System.gc();

            for (int i = 0; i < 5; i++) {
                dispBook(nextPage.get(i), bookGrid, i, 0);
                dispBook(nextPage.get(5 + i), bookGrid, i, 2);
            }

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
    @FXML
    private void onPreviousClick() {
        if(dispPrevPage) {
            dispPrevPage = false;
            bookGrid.getChildren().clear();

            // Supprimer les références aux anciens livres pour libérer la mémoire
            nextPage.clear();
            System.gc();

            for (int i = 0; i < 5; i++) {
                dispBook(previousPage.get(i), bookGrid, i, 0);
                dispBook(previousPage.get(5 + i), bookGrid, i, 2);
            }

            pageCount--;
            pageLabel.setText(String.valueOf(pageCount));

            nextPage = copyPage(currentPage);
            currentPage = copyPage(previousPage);
            if (pageCount == 1) {
                previousButton.setVisible(false);
                previousPage.clear();
                dispPrevPage = true;
            } else {
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
