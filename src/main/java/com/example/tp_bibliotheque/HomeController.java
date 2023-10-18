package com.example.tp_bibliotheque;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;

public class HomeController {
    @FXML private Label label;
    @FXML private Button signOutButton;
    @FXML private GridPane fstGrid;
    @FXML private GridPane sndGrid;
    protected static User user;

    public HomeController(User _user) {
        user = _user;
        //MainApplication.bddConn.fillBDD();
    }

    public void initialize() throws SQLException {
        label.setText("Bienvenue "+user.getName()+" !");

        for(int i=0;i<fstGrid.getColumnCount();i++) {
            dispBook(MainApplication.bddConn.getBook(i), fstGrid, i);
        }

        for(int i=0;i<sndGrid.getColumnCount();i++) {
            dispBook(MainApplication.bddConn.getBook(fstGrid.getColumnCount()+i), sndGrid, i);
        }
    }

    @FXML
    private void onSignOut(ActionEvent e) throws IOException {
        MainApplication.homeRoot = null;
        MainApplication.switchScene(e, "login-view.fxml");
    }

    private void dispBook(Book book, GridPane parent, int column) {
        Image image = new Image(book.getCoverImg());
        ImageView imageView = new ImageView(image);

        imageView.getStyleClass().add("cover_image");
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);

        Button titleButton = new Button();
        titleButton.setText(book.getTitle());
        titleButton.getStyleClass().add("book_title");

        BookViewController bookController = new BookViewController(book);
        titleButton.setOnAction(event -> {
            try {
                MainApplication.switchScene(event, "book-view.fxml", bookController);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        parent.add(imageView, column, 0);
        parent.add(titleButton, column, 1);
    }
}
