package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Objects.Author;
import com.example.tp_bibliotheque.Objects.Book;
import com.example.tp_bibliotheque.MainApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class AuthorViewController {
    @FXML private AnchorPane root;
    @FXML private Label authorName;
    @FXML private GridPane booksGrid;

    private Author author;
    private Vector<Book> books;

    public AuthorViewController(Author _author) throws SQLException {
        author = _author;
        books = author.getBooks();
    }

    public void initialize() {
        root.getChildren().add(MainApplication.header.getHead());

        authorName.setText(author.getName()+" "+author.getLastName());

        for(int i=0;i<books.size();i++) {
            Book b = books.get(i);

            Button bookButton = new Button();
            bookButton.setText(b.getTitle());
            bookButton.getStyleClass().add("book_button");

            BookViewController bookController = new BookViewController(b);
            bookButton.setOnAction(event -> {
                try {
                    MainApplication.switchScene(event, "fxml/book-view.fxml", bookController);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            booksGrid.add(bookButton, 0, i);
        }
    }
}
