package com.example.tp_bibliotheque;

import java.sql.Date;
import java.sql.SQLException;

public class Edition {
    private String isbn;
    private String editorName;
    private Date publishDate;
    private int quantity;

    public Edition(String _isbn, String _editorName, Date _publishDate, int _qty) {
        isbn = _isbn;
        editorName = _editorName;
        publishDate = _publishDate;
        quantity = _qty;
    }

    public String getIsbn() { return isbn; }
    public String getEditorName() { return editorName; }
    public Date getPublishDate() { return publishDate; }
    public int getQuantity() { return quantity; }
    public Emprunt getBorrowedStatus() throws SQLException {
        return(Emprunt.getCurrentEmpruntFromEdition(this));
    }
}
