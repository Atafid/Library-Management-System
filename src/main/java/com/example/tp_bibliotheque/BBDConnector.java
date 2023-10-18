package com.example.tp_bibliotheque;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;

public class BBDConnector {
    protected Connection con;

    public BBDConnector() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3308/bdd", "root", "05116173");
            con.setAutoCommit(false);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void fillBDD() throws SQLException, IOException {
        String sql = "INSERT INTO Books VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = con.prepareStatement(sql);

        String csvFilePath = "books.csv";

        int BatchSize = 50;

        BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
        String lineText = null;

        int count = 0;

        lineReader.readLine();

        while((lineText = lineReader.readLine()) != null) {
            String[] data = lineText.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

            if (21 < data.length) {

                String title = data[1];
                String series = data[2];
                String author = data[3];
                String description = data[5];
                String language = data[6];
                String isbn = data[7];
                String genre = data[8];
                String edition = data[11];
                String page = data[12];
                String date = data[14];
                String coverImg = data[21];

                stmt.setInt(1, count);
                stmt.setString(2, title);
                stmt.setString(3, series);
                stmt.setString(4, author);
                stmt.setString(5, description);
                stmt.setString(6, language);
                stmt.setString(7, isbn);
                stmt.setString(8, genre);
                stmt.setString(9, edition);
                stmt.setString(10, page);
                stmt.setString(11, date);
                stmt.setString(12, coverImg);

                stmt.addBatch();

                if (count % BatchSize == 0) {
                    stmt.executeBatch();
                }

                count++;
            }
        }

        lineReader.close();

        stmt.executeBatch();

        con.commit();
    }
}
