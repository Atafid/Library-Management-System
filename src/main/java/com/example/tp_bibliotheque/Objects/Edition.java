package com.example.tp_bibliotheque.Objects;

import com.example.tp_bibliotheque.MainApplication;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

//CLASSE REPRESENTANT UNE EDITION

public class Edition {
    //*****************ATTRIBUTS*****************//

    //Label : nom de l'édition
    @FXML public Label editionLabel;

    //Button : pour emprunter l'édition
    @FXML public Button borrowButton;

    //Label : lorsque l'édition est empruntée
    @FXML public Label borrowedLabel;

    //Button : pour rendre l'édition
    @FXML public Button returnButton;

    //Button : pour réserver l'édition
    @FXML public Button reserveButton;

    //Label : lorsque l'édition est réservée
    @FXML public Label reserveLabel;

    //ISBN de l'édition
    private final String isbn;

    //ID du livre relatif à l'édition, dans la BDD
    private final int bookId;

    //Nom de l'éditeur
    private final String editorName;

    //Date de publication
    private final Date publishDate;

    //Quantité
    private final int quantity;

    //Quantité disponible
    private int availableQty;


    //*****************METHODES*****************//

    //Constructeur de classe
    public Edition(String _isbn, int _bookId, String _editorName, Date _publishDate, int _qty) {
        isbn = _isbn;
        bookId = _bookId;
        editorName = _editorName;
        publishDate = _publishDate;
        quantity = _qty;

        //On calcule la quantité disponible avec le nombre d'emprunts en cours de l'édition
        try {
            availableQty = quantity-getEmprunts().size();
        } catch(SQLException e) {
            System.out.println(e);
        }

        //Initialisation de l'interface graphique
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

    //Méthode static permettant d'obtenir une variable édition à partir de l'ID d'un emprunt dans la BDD
    public static Edition getEditionFromEmprunt(int empruntId) throws SQLException {
        //Requête SQL récupérant l'édition
        String querry = "SELECT * FROM Edition ed JOIN Emprunt em ON ed.isbn=em.editionISBN WHERE em.id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, empruntId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Edition edition = new Edition(rs.getString(1), rs.getInt(2), rs.getString(3),
                    rs.getDate(4), rs.getInt(5));
            return(edition);
        }

        return(null);
    }

    //Méthode mettant à jour l'interface graphique des bouttons relatifs à l'édition (emprunt, retour, etc)
    public void updateButtons() throws SQLException {
        //Récupération de l'emprunt de l'utilisateur actuel relativement à l'édition considérée
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

        //Action des différents bouttons
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

    //Méthode appelée lors de l'emprunt de l'édition
    @FXML
    private void onClickEmprunt() throws SQLException {
        User user = MainApplication.header.getUser();

        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        //Nombre de ms dans un jour
        long dayMillis = (long) (8.64*Math.pow(10,7));
        //Calcul de la date de retour maximale en fonction de la catégorie de l'utilisateur
        Date endDate = new Date(millis+user.getBorrowDays()*dayMillis);

        //Ajout de l'emprunt à la BDD
        Emprunt.addEmprunt(this.getIsbn(), user.getId(), currentDate, endDate);

        //Mise à jour des quantités et emprunts de l'utilisateur
        user.setBorrowCount(user.getBorrowCount()+1);
        availableQty -= 1;

        //Mise à jour de l'interface graphique
        updateButtons();
    }

    //Méthode appelée lors du retour de l'édition
    @FXML
    protected void onClickReturn(int empruntId) throws SQLException {
        long millis = System.currentTimeMillis();
        Date currentDate = new Date(millis);

        //Emprunt marqué comme terminé
        Emprunt.finishEmprunt(empruntId, currentDate);
        //Mise à jour du nombre d'emprunts de l'utilisateur
        MainApplication.header.getUser().setBorrowCount(MainApplication.header.getUser().getBorrowCount()-1);

        //Aucun réservation -> l'édition est à nouveau disponible
        if(Reservation.getNumberReservation(isbn)==0) {
            availableQty += 1;
        }

        //Réservation en cours -> nouvel emprunt immédiat
        else {
            //Récupération de l'utilisateur en tête de file d'attente
            User user = Reservation.getFirstUser(isbn);
            //Mise à jour de la file d'attente
            Reservation.updatePlaces(isbn);

            long dayMillis = (long) (8.64*Math.pow(10,7));
            Date endDate = new Date(millis+user.getBorrowDays()*dayMillis);

            //Emprunt et Notification de l'utilisateur ajoutée à la BDD
            Emprunt.addEmprunt(this.getIsbn(), user.getId(), currentDate, endDate);
            Notification.addNotification(user.getId(), "R", currentDate, bookId);
        }

        updateButtons();
    }

    //Méthode appelée lors de la réservation de l'édition
    @FXML
    private void onClickReserve() throws SQLException {
        //Récupération du nombre de personnes ayant réservé l'édition
        int numReservation = Reservation.getNumberReservation(isbn);
        //Ajout de la réservation à la BDD
        Reservation.addReservation(isbn, MainApplication.header.getUser().getId(), new Date(System.currentTimeMillis()), numReservation+1);

        //Mise à jour des emprunts de l'utilisateur (une réservation compte comme un emprunt)
        User user = MainApplication.header.getUser();
        user.setBorrowCount(user.getBorrowCount()+1);

        //Mise à jour de l'interface graphique
        updateButtons();
    }

    //GETTERS DE CLASSE
    public String getIsbn() { return isbn; }
    public Vector<Emprunt> getEmprunts() throws SQLException {
        return(Emprunt.getCurrentEmpruntsFromEdition(isbn));
    }
}
