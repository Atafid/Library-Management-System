package com.example.tp_bibliotheque;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Edition {
    @FXML public Label editionLabel;
    @FXML public Button borrowButton;
    @FXML public Label borrowedLabel;
    @FXML public Button returnButton;

    private String isbn;
    private String editorName;
    private Date publishDate;
    private int quantity;
    private int availableQty;

    public Edition(String _isbn, String _editorName, Date _publishDate, int _qty) {
        isbn = _isbn;
        editorName = _editorName;
        publishDate = _publishDate;
        quantity = _qty;

        try {
            availableQty = quantity-getEmprunts().size();
        } catch(SQLException e) {
            System.out.println(e);
        }

        editionLabel = new Label();
        editionLabel.setText(editorName+", "+publishDate.toString()+", qty : "+quantity);

        borrowButton = new Button();
        borrowButton.setText("Borrow");

        borrowedLabel = new Label();
        borrowedLabel.setText("Book borrowed until :");

        returnButton = new Button();
        returnButton.setText("Return");
    }

    public String getIsbn() { return isbn; }
    public String getEditorName() { return editorName; }
    public Date getPublishDate() { return publishDate; }
    public int getQuantity() { return quantity; }
    public Vector<Emprunt> getEmprunts() throws SQLException {
        return(Emprunt.getCurrentEmpruntFromEdition(isbn));
    }
    public static Emprunt getUserEmprunt(Vector<Emprunt> emprunts) throws SQLException {
        for(Emprunt e:emprunts) {
            if(e.getUserMail().equals(HomeController.user.getMail())) {
                return(e);
            }
        }

        return(null);
    }

    public void updateButtons() throws SQLException {
        Vector<Emprunt> currentEmprunts = getEmprunts();
        Emprunt currentEmprunt = getUserEmprunt(currentEmprunts);

        editionLabel.setText(editorName+", "+publishDate.toString()+", qty : "+availableQty);

        if(currentEmprunt != null) {
            borrowButton.setVisible(false);

            borrowedLabel.setText("Book borrowed until : "+currentEmprunt.getHypEndDate()+" by "+currentEmprunt.getUserMail());
            borrowedLabel.setVisible(true);

            returnButton.setVisible(currentEmprunt.getUserMail().equals(HomeController.user.getMail()));
        }

        else {
            if(currentEmprunts.size() < quantity && HomeController.user.getBorrowCount()<HomeController.user.getMaxBorrowCount()) {
                borrowButton.setVisible(true);
                borrowedLabel.setVisible(false);
                returnButton.setVisible(false);
            }

            else {
                borrowButton.setVisible(false);

                borrowedLabel.setText("Impossible to borrow for now");
                borrowedLabel.setVisible(true);

                returnButton.setVisible(false);
            }
        }

        borrowButton.setOnAction(event -> {
            try {
                onClickEmprunt();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });

        returnButton.setOnAction(event -> {
            try {
                onClickReturn(currentEmprunt.getId());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    @FXML
    private void onClickEmprunt() throws SQLException {
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        long dayMillis = (long) (8.64*Math.pow(10,7));
        Date endDate = new Date(millis+HomeController.user.getBorrowDays()*dayMillis);

        Emprunt.addEmprunt(this, HomeController.user, currentDate, endDate);

        HomeController.user.setBorrowCount(HomeController.user.getBorrowCount()+1);
        availableQty -= 1;

        updateButtons();
    }
    @FXML
    private void onClickReturn(int empruntId) throws SQLException {
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        Emprunt.finishEmprunt(empruntId, currentDate);

        HomeController.user.setBorrowCount(HomeController.user.getBorrowCount()-1);
        availableQty += 1;

        updateButtons();
    }
}
