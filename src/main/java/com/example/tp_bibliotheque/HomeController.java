package com.example.tp_bibliotheque;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class HomeController {
    @FXML private Label label;
    @FXML private Button adminButton;
    @FXML private Button signOutButton;
    @FXML private GridPane bookGrid;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    @FXML private TextField searchArea;

    protected static User user;

    private Vector<Book> previousPage;
    private Vector<Book> currentPage;
    private Vector<Book> nextPage;
    private int pageCount;
    private Boolean dispNextPage;
    private Boolean dispPrevPage;

    public HomeController(User _user, Vector<Book> fstPage, Vector<Book> sndPage) {
        user = _user;
        //MainApplication.bddConn.fillBDD();

        currentPage = fstPage;
        nextPage = sndPage;
        previousPage = new Vector<Book>();
        pageCount = 1;
        dispNextPage = true;
        dispPrevPage = true;
    }
    public void initialize() throws SQLException {
        label.setText("Bienvenue "+user.getName()+" !");

        adminButton.setVisible(user.categorie.equals(Categorie.Bibliothécaire));

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
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

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

    @FXML
    private void onSignOut(ActionEvent e) throws IOException {
        MainApplication.homeRoot = null;
        MainApplication.switchScene(e, "login-view.fxml");
    }
    @FXML
    private void onUserClick(ActionEvent e) throws IOException {
        UserViewController userViewController = new UserViewController(user);
        MainApplication.switchScene(e, "user-view.fxml", userViewController);
    }
    @FXML
    private void onAdminClick(ActionEvent e) throws IOException {
        AdminViewController adminViewController = new AdminViewController(user);
        MainApplication.switchScene(e, "admin-view.fxml", adminViewController);
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

            previousPage = currentPage;
            currentPage = nextPage;

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

            nextPage = currentPage;
            currentPage = previousPage;
            if (pageCount == 1) {
                previousButton.setVisible(false);
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
