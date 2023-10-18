package com.example.tp_bibliotheque;

import java.sql.Date;

public class Emprunt {
    private int bookID;
    private String userMail;
    private Date beginDate;
    private Date endDate;
    private boolean isFinished;

    public Emprunt(int _bookID, String _userMail, Date _beginDate, Date _endDate, boolean _isFinished) {
        bookID = _bookID;
        userMail = _userMail;
        beginDate = _beginDate;
        endDate = _endDate;
        isFinished = _isFinished;
    }

    public String getEndDate() {
        return(endDate.toString());
    }
}
