package com.example.tp_bibliotheque;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class Author extends Personne {
    private int id;
    private Date birth_date;

    public Author(int _id, String _name, String _last_name, Date _birth) {
        super(_name, _last_name);
        id = _id;
        birth_date = _birth;
    }

    public Vector<Book> getBooks() throws SQLException {
        Vector<Book> res = new Vector<Book>();

        String quer = "SELECT * FROM Books b JOIN aEcrit e ON b.id = e.bookID WHERE e.authorID=?";
        PreparedStatement dispStmt = MainApplication.bddConn.con.prepareStatement(quer);
        dispStmt.setInt(1,id);

        ResultSet rs = dispStmt.executeQuery();

        while(rs.next()) {
            Book book = new Book(rs.getInt(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5));
            res.add(book);
        }

        return(res);
    }

    public int getId() { return id; }
    public Date getBirth_date() { return birth_date; }
}
