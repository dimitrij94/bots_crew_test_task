package org.bots_crew.dmitriy_kostiushko.test.service;

import junit.framework.TestCase;
import org.bots_crew.dmitriy_kostiushko.test.enteties.Book;

import java.util.List;

public class BookShelfServiceTest extends TestCase {

    private H2DatabaseConnector connector = new H2DatabaseConnector();
    private BookShelfService service = new BookShelfService(connector);

    private final String testBookName = "Essays in love";
    private final String testAuthorsName = "Alain de Botton";

    private final String testBookName2 = "Essays in love";
    private final String testAuthorsName2 = "Unknown";

    private final Book testBook = new Book(testBookName, testAuthorsName);
    private final Book testBook2 = new Book(testBookName2, testAuthorsName2);

    public void setUp() throws Exception {
        super.setUp();
        this.service.clearTheDB();
    }

    public void tearDown() throws Exception {
    }

    public void testSave() throws Exception {
        this.service.save(testBookName, testAuthorsName);
        List<Book> books = service.findByNameAndAuthor(testBookName, testAuthorsName);
        assertFalse(books.isEmpty());
        Book savedBook = books.get(0);
        assertEquals(savedBook.getBookName(), testBook.getBookName());
        assertEquals(savedBook.getAuthorName(), testBook.getAuthorName());
        this.service.deleteBook(testBookName, testAuthorsName);
    }

    public void testFindAllBooks() throws Exception {
        this.service.save(testBookName, testAuthorsName);
        this.service.save(testBookName2, testAuthorsName2);
        List<Book> books = this.service.findAllBooks();
        assertFalse(books.isEmpty());
        assertTrue(books.size() == 2);
        this.service.deleteBook(testBookName, testAuthorsName);
        this.service.deleteBook(testBookName2, testAuthorsName2);
    }

    public void testFindByName() throws Exception {
        this.service.save(testBookName, testAuthorsName);
        this.service.save(testBookName2, testAuthorsName2);
        List<Book> books = this.service.findByName(this.testBookName);
        assertFalse(books.isEmpty());
        assertTrue(books.size() == 2);
        this.service.deleteBook(testBookName, testAuthorsName);
        this.service.deleteBook(testBookName2, testAuthorsName2);
    }

    public void testDeleteBook() throws Exception {
        this.service.save(testBookName, testAuthorsName);
        this.service.deleteBook(testBookName, testAuthorsName);
        assertTrue(service.findByName(testBookName).isEmpty());
    }

    public void testFindByNameAndAuthor() throws Exception {
        this.service.save(testBookName, testAuthorsName);
        List<Book> books = this.service.findByNameAndAuthor(testBookName, testAuthorsName);
        assertFalse(books.isEmpty());
        Book foundBook = books.get(0);
        assertEquals(foundBook.getAuthorName(), testBook.getAuthorName());
        assertEquals(foundBook.getBookName(), testBook.getBookName());
        this.service.deleteBook(testBookName, testAuthorsName);
    }

}