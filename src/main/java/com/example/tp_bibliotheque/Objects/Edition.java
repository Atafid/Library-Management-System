package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Vector;

public class Edition {
    @FXML public Label editionLabel;
    @FXML public Button borrowButton;
    @FXML public Label borrowedLabel;
    @FXML public Button returnButton;
    @FXML public Button reserveButton;
    @FXML public Label reserveLabel;

    private String isbn;
    private int bookId;
    private String editorName;
    private Date publishDate;
    private int quantity;
    private int availableQty;

    public Edition(String _isbn, int _bookId, String _editorName, Date _publishDate, int _qty) {
        isbn = _isbn;
        bookId = _bookId;
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
        borrowedLabel.setText("Book borrowed until : ");

        returnButton = new Button();
        returnButton.setText("Return");

        reserveButton = new Button();
        reserveButton.setText("Reserve");

        reserveLabel = new Label();
        reserveLabel.setText("Book reserved, you are : ");
    }

    public String getIsbn() { return isbn; }
    public Vector<Emprunt> getEmprunts() throws SQLException {
        return(Emprunt.getCurrentEmpruntsFromEdition(isbn));
    }

    public void updateButtons() throws SQLException {
        User user = MainApplication.header.getUser();
        Emprunt currentEmprunt = Emprunt.getCurrentEmprunt(isbn, user.getId());

        editionLabel.setText(editorName+", "+publishDate.toString()+", qty : "+availableQty);

        //EMPRUNT EN COURS
        if(currentEmprunt != null) {
            borrowButton.setVisible(false);

            borrowedLabel.setText("Book borrowed until : "+currentEmprunt.getStringHypEndDate()+" by "+currentEmprunt.getUserMail());
            borrowedLabel.setVisible(true);

            returnButton.setVisible(true);

            reserveButton.setVisible(false);
            reserveLabel.setVisible(false);
        }

        //PAS D'EMPRUNTS EN COURS
        else {
            Reservation currentReservation = Reservation.getCurrentReservation(isbn, user.getId());

            //LIVRE DEJA RESERVE
            if(currentReservation != null) {
                borrowButton.setVisible(false);

                borrowedLabel.setText("Book already borrowed");
                borrowedLabel.setVisible(true);

                returnButton.setVisible(false);
                reserveButton.setVisible(false);

                reserveLabel.setVisible(true);
                reserveLabel.setText(reserveLabel.getText()+currentReservation.getPlace());
            }

            //LIVRE NON RESERVE
            else {
                //L'UTILISATEUR PEUT ENCORE EMPRUNTE
                if(user.getBorrowCount()<user.getMaxBorrowCount()) {
                    //IL RESTE DES LIVRES
                    if(getEmprunts().size() < quantity) {
                        borrowButton.setVisible(true);
                        borrowedLabel.setVisible(false);
                        returnButton.setVisible(false);
                        reserveButton.setVisible(false);
                        reserveLabel.setVisible(false);
                    }

                    //PLUS DE LIVRES DISPONIBLES
                    else {
                        //RESERVATIONS PLEINES
                        if(Reservation.getNumberReservation(isbn)>=5) {
                            borrowButton.setVisible(false);

                            borrowedLabel.setText("Book already borrowed and too many reservations");
                            borrowedLabel.setVisible(true);

                            returnButton.setVisible(false);
                            reserveButton.setVisible(false);
                            reserveLabel.setVisible(false);
                        }

                        //RESERVATIONS POSSIBLE
                        else {
                            borrowButton.setVisible(false);

                            borrowedLabel.setText("Book already borrowed");
                            borrowedLabel.setVisible(true);

                            returnButton.setVisible(false);
                            reserveButton.setVisible(true);
                            reserveLabel.setVisible(false);
                        }
                    }
                }

                //L'UTILISATEUR NE PEUT PLUS EMPRUNTE
                else {
                    borrowButton.setVisible(false);

                    borrowedLabel.setText("You have borrowed too much books");
                    borrowedLabel.setVisible(true);

                    returnButton.setVisible(false);

                    reserveButton.setVisible(false);
                    reserveLabel.setVisible(false);
                }

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

        reserveButton.setOnAction(event -> {
            try {
                onClickReserve();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
    @FXML
    private void onClickEmprunt() throws SQLException {
        User user = MainApplication.header.getUser();

        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        long dayMillis = (long) (8.64*Math.pow(10,7));
        Date endDate = new Date(millis+user.getBorrowDays()*dayMillis);

        Emprunt.addEmprunt(this.getIsbn(), user.getId(), currentDate, endDate);

        user.setBorrowCount(user.getBorrowCount()+1);
        availableQty -= 1;

        updateButtons();
    }
    @FXML
    private void onClickReturn(int empruntId) throws SQLException {
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        Emprunt.finishEmprunt(empruntId, currentDate);

        if(Reservation.getNumberReservation(isbn)==0) {
            MainApplication.header.getUser().setBorrowCount(MainApplication.header.getUser().getBorrowCount()-1);
            availableQty += 1;
        }

        else {
            User user = Reservation.getFirstUser(isbn);
            Reservation.updatePlaces(isbn);

            long dayMillis = (long) (8.64*Math.pow(10,7));
            Date endDate = new Date(millis+user.getBorrowDays()*dayMillis);

            Emprunt.addEmprunt(this.getIsbn(), user.getId(), currentDate, endDate);
            Notification.addNotification(user.getId(), "R", currentDate, bookId);
        }

        updateButtons();
    }
    @FXML
    private void onClickReserve() throws SQLException {
        int numReservation = Reservation.getNumberReservation(isbn);
        Reservation.addReservation(isbn, MainApplication.header.getUser().getId(), new Date(System.currentTimeMillis()), numReservation+1);

        User user = MainApplication.header.getUser();
        user.setBorrowCount(user.getBorrowCount()+1);

        updateButtons();
    }
}
