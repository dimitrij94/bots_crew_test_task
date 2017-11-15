package org.bots_crew.dmitriy_kostiushko.test.service;

import org.bots_crew.dmitriy_kostiushko.test.consts.TableColumnNames;
import org.bots_crew.dmitriy_kostiushko.test.enteties.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookShelfService {

    private final H2DatabaseConnector connector;
    private final String saveBookSQL = "INSERT INTO BookShelf (book_name, author_name) VALUES (?,?)";
    private final String findSQL = "SELECT * FROM BookShelf";
    private final String deleteSQL = "DELETE FROM BookShelf WHERE book_name=? AND author_name=?";
    private final String findByNameSQL = "SELECT * FROM BookShelf WHERE book_name=?";
    private final String findByNameAndAuthorSQL = "SELECT * FROM BookShelf WHERE book_name=? AND author_name=?";
    private final String deleteAllSQL = "DELETE FROM BookShelf";

    public BookShelfService(H2DatabaseConnector connector) {
        this.connector = connector;
    }

    public Book save(String bookName, String bookAuthor) {
        try (
                Connection connection = this.connector.establishConnection();
                PreparedStatement saveStatement = connection.prepareStatement(saveBookSQL)
        ) {
            saveStatement.setString(1, bookName);
            saveStatement.setString(2, bookAuthor);
            saveStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Book();
    }

    public ArrayList<Book> findAllBooks() {
        ArrayList<Book> books = null;
        try (
                Connection connection = this.connector.establishConnection();
                PreparedStatement findStatement = connection.prepareStatement(findSQL)
        ) {
            ResultSet foundBooks = findStatement.executeQuery();
            books = parseResultSet(foundBooks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public ArrayList<Book> findByName(String name) {
        ArrayList<Book> books = null;
        try (
                Connection connection = this.connector.establishConnection();
                PreparedStatement findByNameStatement = connection.prepareStatement(findByNameSQL)
        ) {
            findByNameStatement.setString(1, name);
            ResultSet foundBooks = findByNameStatement.executeQuery();
            books = parseResultSet(foundBooks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }


    private ArrayList<Book> parseResultSet(ResultSet foundBooks) throws SQLException {
        ArrayList<Book> books = null;
        if (foundBooks != null) {
            books = new ArrayList<>(10);
            while (foundBooks.next()) {
                int id = foundBooks.getInt(TableColumnNames.BookShelf.id.toString());
                String name = foundBooks.getString(TableColumnNames.BookShelf.book_name.toString());
                String author = foundBooks.getString(TableColumnNames.BookShelf.author_name.toString());
                books.add(new Book(id, name, author));
            }
        }
        return books;
    }


    public boolean deleteBook(String name, String author) {
        try (
                Connection connection = this.connector.establishConnection();
                PreparedStatement deleteStatement = connection.prepareStatement(deleteSQL)
        ) {
            deleteStatement.setString(1, name);
            deleteStatement.setString(2, author);
            return deleteStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Book> findByNameAndAuthor(String bookName, String authorsName) {
        try (
                Connection connection = this.connector.establishConnection();
                PreparedStatement findStatement = connection.prepareStatement(findByNameAndAuthorSQL)
        ) {
            findStatement.setString(1, bookName);
            findStatement.setString(2, authorsName);
            return parseResultSet(findStatement.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearTheDB() {
        try (Connection connection = this.connector.establishConnection();
             PreparedStatement deleteAllStatement = connection.prepareStatement(deleteAllSQL)
        ) {
            deleteAllStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
