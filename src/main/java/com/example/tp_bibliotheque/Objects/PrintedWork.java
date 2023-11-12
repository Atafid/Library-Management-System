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

public class PrintedWork {
    //Label : nom de l'édition
    @FXML
    public Label editionLabel;

    //Label : nom de la bibliothèque
    @FXML public Label libraryLabel;

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

    @FXML public Button askChangeButton;

    @FXML public Label askChangeLabel;

    private final int id;
    private final String editionIsbn;
    private final int libraryId;

    //Quantité
    private int quantity;

    //Quantité disponible
    private int availableQty;

    public PrintedWork(int _id, String _editionIsbn, int _libraryId, int _quantity) {
        id = _id;
        editionIsbn = _editionIsbn;
        libraryId = _libraryId;
        quantity = _quantity;

        //On calcule la quantité disponible avec le nombre d'emprunts en cours de l'édition
        try {
            availableQty = quantity-getEmprunts().size();
        } catch(SQLException e) {
            System.out.println(e);
        }

        //Initialisation de l'interface graphique
        editionLabel = new Label();
        try {
            editionLabel.setText(getEdition().getEditorName()+", "+getEdition().getIsbn()+", qty : "+quantity);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        libraryLabel = new Label();
        try {
            libraryLabel.setText(getLibrary().getName());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

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

        askChangeButton = new Button();
        askChangeButton.setText("Ask To Move");

        askChangeLabel = new Label();
        askChangeLabel.setText("Your asking have been send");
    }

    public static void addPrintedWork(String isbn, int _libraryId, int _qty) throws SQLException {
        String querry = "INSERT INTO PrintedWork VALUES(0,?,?,?)";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setString(1, isbn);
        stmt.setInt(2, _libraryId);
        stmt.setInt(3, _qty);

        stmt.addBatch();
        stmt.executeBatch();
        MainApplication.bddConn.con.commit();
    }

    public void removeQtyPrintedWork(int qtyToRemove) throws SQLException {
        quantity -= qtyToRemove;

        String querry = "UPDATE PrintedWork SET quantity=? WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, quantity);
        stmt.setInt(2, id);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    public void addQtyPrintedWork(int qtyToAdd) throws SQLException {
        quantity += qtyToAdd;

        String querry = "UPDATE PrintedWork SET quantity=? WHERE id=?";
        PreparedStatement stmt = MainApplication.bddConn.con.prepareStatement(querry);

        stmt.setInt(1, quantity);
        stmt.setInt(2, id);

        stmt.executeUpdate();
        MainApplication.bddConn.con.commit();
    }

    //Méthode static permettant d'obtenir une variable exemplaire à partir de l'ID d'un emprunt dans la BDD
    public static PrintedWork getPrintedWorkFromEmprunt(int empruntId) throws SQLException {
        //Requête SQL récupérant l'exemplaire
        String querry = "SELECT * FROM PrintedWork p JOIN Emprunt e ON p.id=e.printedWorkID WHERE e.id=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setInt(1, empruntId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            PrintedWork printedWork = new PrintedWork(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4));
            return(printedWork);
        }

        return(null);
    }

    public static PrintedWork getPrintedWorkFromIsbnAndLibrary(String isbn, int libraryId) throws SQLException {
        String querry = "SELECT * FROM PrintedWork WHERE editionIsbn=? AND libraryID=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1, isbn);
        dispStmt.setInt(2, libraryId);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            PrintedWork printedWork = new PrintedWork(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4));
            return(printedWork);
        }

        return(null);
    }


    //Méthode mettant à jour l'interface graphique des bouttons relatifs à l'édition (emprunt, retour, etc)
    public void updateButtons() throws SQLException {
        //Récupération de l'emprunt de l'utilisateur actuel relativement à l'édition considérée
        User user = MainApplication.header.getUser();
        Emprunt currentEmprunt = Emprunt.getCurrentEmprunt(id, user.getId());

        editionLabel.setText(getEdition().getEditorName()+", "+getEdition().getPublishDate().toString()+", qty : "+availableQty);

        //BONNE BIBLIOTHEQUE
        if(libraryId==user.getLibraryId()) {
            askChangeButton.setVisible(false);
            askChangeLabel.setVisible(false);

            //EMPRUNT EN COURS
            if (currentEmprunt != null) {
                borrowButton.setVisible(false);

                borrowedLabel.setText("Book borrowed until : " + currentEmprunt.getStringHypEndDate() + " by " + currentEmprunt.getUserMail());
                borrowedLabel.setVisible(true);

                returnButton.setVisible(true);

                reserveButton.setVisible(false);
                reserveLabel.setVisible(false);
            }

            //PAS D'EMPRUNTS EN COURS
            else {
                Reservation currentReservation = Reservation.getCurrentReservation(id, user.getId());

                //LIVRE DEJA RESERVE
                if (currentReservation != null) {
                    borrowButton.setVisible(false);

                    borrowedLabel.setText("Book already borrowed");
                    borrowedLabel.setVisible(true);

                    returnButton.setVisible(false);
                    reserveButton.setVisible(false);

                    reserveLabel.setVisible(true);
                    reserveLabel.setText(reserveLabel.getText() + currentReservation.getPlace());
                }

                //LIVRE NON RESERVE
                else {
                    //L'UTILISATEUR PEUT ENCORE EMPRUNTE
                    if (user.getBorrowCount() < user.getMaxBorrowCount()) {
                        //IL RESTE DES LIVRES
                        if (getEmprunts().size() < quantity) {
                            borrowButton.setVisible(true);
                            borrowedLabel.setVisible(false);
                            returnButton.setVisible(false);
                            reserveButton.setVisible(false);
                            reserveLabel.setVisible(false);
                        }

                        //PLUS DE LIVRES DISPONIBLES
                        else {
                            //RESERVATIONS PLEINES
                            if (Reservation.getNumberReservation(id) >= 5) {
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
        }

        //MAUVAISE BIBLIOTHEQUE
        else {
            MoveAsk currentMoveAsk = MoveAsk.getCurrentMoveAskFromUserAndBook(MainApplication.header.getUser().getId(), id);

            if(currentMoveAsk != null) {
                borrowButton.setVisible(false);

                borrowedLabel.setText("Book in an other library");
                borrowedLabel.setVisible(true);

                returnButton.setVisible(false);

                reserveButton.setVisible(false);
                reserveLabel.setVisible(false);

                askChangeButton.setVisible(false);
                askChangeLabel.setVisible(true);
            }

            else {
                borrowButton.setVisible(false);

                borrowedLabel.setText("Book in an other library");
                borrowedLabel.setVisible(true);

                returnButton.setVisible(false);

                reserveButton.setVisible(false);
                reserveLabel.setVisible(false);

                askChangeButton.setVisible(true);
                askChangeLabel.setVisible(false);
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

        askChangeButton.setOnAction(event -> {
            try {
                onClickAsk();
            } catch (SQLException e) {
                throw new RuntimeException(e);
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
        Emprunt.addEmprunt(id, user.getId(), currentDate, endDate);

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
        if(Reservation.getNumberReservation(id)==0) {
            availableQty += 1;
        }

        //Réservation en cours -> nouvel emprunt immédiat
        else {
            //Récupération de l'utilisateur en tête de file d'attente
            User user = Reservation.getFirstUser(id);
            //Mise à jour de la file d'attente
            Reservation.updatePlaces(id);

            long dayMillis = (long) (8.64*Math.pow(10,7));
            Date endDate = new Date(millis+user.getBorrowDays()*dayMillis);

            //Emprunt et Notification de l'utilisateur ajoutée à la BDD
            Emprunt.addEmprunt(id, user.getId(), currentDate, endDate);
            Notification.addNotification(user.getId(), "R", currentDate, getBookId());
        }

        updateButtons();
    }

    //Méthode appelée lors de la réservation de l'édition
    @FXML
    private void onClickReserve() throws SQLException {
        //Récupération du nombre de personnes ayant réservé l'édition
        int numReservation = Reservation.getNumberReservation(id);
        //Ajout de la réservation à la BDD
        Reservation.addReservation(id, MainApplication.header.getUser().getId(), new Date(System.currentTimeMillis()), numReservation+1);

        //Mise à jour des emprunts de l'utilisateur (une réservation compte comme un emprunt)
        User user = MainApplication.header.getUser();
        user.setBorrowCount(user.getBorrowCount()+1);

        //Mise à jour de l'interface graphique
        updateButtons();
    }

    @FXML
    private void onClickAsk() throws SQLException {
        askChangeButton.setVisible(false);
        askChangeLabel.setVisible(true);

        Date currentDate = new Date(System.currentTimeMillis());

        MoveAsk.addMoveAsk(MainApplication.header.getUser().getId(), id, currentDate);

        Vector<User> adminVector = User.getAdmin();
        for(User a: adminVector) {
            Notification.addNotification(a.getId(), "M", currentDate, MoveAsk.getIdFromBDD(MainApplication.header.getUser().getId(), id, currentDate));
        }
    }

    public Edition getEdition() throws SQLException {
        return(Edition.getEditionFromIsbn(editionIsbn));
    }
    public Library getLibrary() throws SQLException {
        return(Library.getLibraryFromId(libraryId));
    }
    public Vector<Emprunt> getEmprunts() throws SQLException {
        return(Emprunt.getCurrentEmpruntsFromPrintedWork(id));
    }
    public int getBookId() throws SQLException {
        //Requête SQL récupérant l'édition
        String querry = "SELECT id FROM Books b JOIN Edition e ON b.id=e.bookID WHERE e.isbn=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(querry);
        dispStmt.setString(1, editionIsbn);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            return(rs.getInt(1));
        }

        return(-1);
    }
    public int getId() { return id; }
    public int getQuantity() { return quantity; }
    public int getAvailableQty() { return availableQty; }
}
