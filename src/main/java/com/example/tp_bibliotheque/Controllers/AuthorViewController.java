package com.example.tp_bibliotheque.Controllers;

import com.example.tp_bibliotheque.Objects.Author;
import com.example.tp_bibliotheque.Objects.Book;
import com.example.tp_bibliotheque.MainApplication;
import com.example.tp_bibliotheque.Objects.Emprunt;
import com.example.tp_bibliotheque.Objects.Page;
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
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private Label pageLabel;
    private Page booksPage;

    private Author author;

    public AuthorViewController(Author _author) throws SQLException {
        author = _author;
    }

    public void initialize() {
        root.getChildren().add(MainApplication.header.getHead());

        authorName.setText(author.getName()+" "+author.getLastName());

        try {
            booksPage = new Page(prevButton, nextButton, author.getBooks(), booksGrid, pageLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
