package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class AuthorViewController {
    @FXML private Label authorName;
    @FXML private GridPane booksGrid;

    private Author author;
    private Vector<Book> books;

    public AuthorViewController(Author _author) throws SQLException {
        author = _author;
        books = author.getBooks();
    }

    public void initialize() {
        authorName.setText(author.getName()+" "+author.getLast_name());

        for(int i=0;i<books.size();i++) {
            Book b = books.get(i);

            Button bookButton = new Button();
            bookButton.setText(b.getTitle());
            bookButton.getStyleClass().add("book_button");

            BookViewController bookController = new BookViewController(b);
            bookButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "book-view.fxml", bookController);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            booksGrid.add(bookButton, 0, i);
        }
    }


    @FXML
    private void onHomeClick(ActionEvent e) throws IOException {
        MainApplication.loadHome(e);
        //MainApplication.switchScene(e, "home-view.fxml", MainApplication.homeController);
    }
}
