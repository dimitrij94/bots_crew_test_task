package org.bots_crew.dmitriy_kostiushko.test.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class H2DatabaseConnector {
    private final String jdbcUrl = "jdbc:h2:~/BookShelf";
    private final String userName = "sa";
    private final String password = "";
    private final String createDBSQL = "CREATE TABLE IF NOT EXISTS BookShelf " +
            "(id INTEGER NOT NULL AUTO_INCREMENT, book_name VARCHAR(255), author_name VARCHAR(255), PRIMARY KEY (id))";

    public H2DatabaseConnector() {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.initDBScheme();
    }

    public Connection establishConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcUrl, userName, password);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void initDBScheme() {
        try (Connection connection = establishConnection();
             PreparedStatement statement = connection.prepareStatement(createDBSQL)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
