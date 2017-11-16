package org.bots_crew.dmitriy_kostiushko.test.controllers;

import junit.framework.TestCase;
import org.bots_crew.dmitriy_kostiushko.test.enteties.Book;
import org.bots_crew.dmitriy_kostiushko.test.service.BookShelfService;
import org.bots_crew.dmitriy_kostiushko.test.service.H2DatabaseConnector;
import org.bots_crew.dmitriy_kostiushko.test.service.MockCommandLineService;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserCommandsControllerTest extends TestCase {
    private UserCommandsController controller;
    private MockCommandLineService mockCommandLineService;
    private BookShelfService service;

    public void setUp() throws Exception {
        super.setUp();
        H2DatabaseConnector connector = new H2DatabaseConnector();
        service = new BookShelfService(connector);
        mockCommandLineService = new MockCommandLineService();
        controller = new UserCommandsController(service, mockCommandLineService);
        this.service.clearTheDB();
    }


    public void testAddBookCommand() throws Exception {

        String commandFormatter = "add %s \"%s\"";
        String[] testBooks = {"War and peace", "война и мир", "Війна та мир"};
        String[] testAuthors = {"Leonid Tolstoy", "Лев Толстой", ""};

        for (int i = 0; i < testBooks.length; i++) {
            this.mockCommandLineService.queueTheCommand(String.format(commandFormatter, testAuthors[i], testBooks[i]));
        }

        //the last command will break the readCmd cycle
        this.mockCommandLineService.queueTheCommand("exit");
        this.controller.readCmd();

        for (int i = 0; i < testBooks.length; i++) {
            String testAuthor = testAuthors[i];
            testAuthor = testAuthor.equals("") ? "Unknown" : testAuthor;
            List<Book> foundBooks = this.service.findByNameAndAuthor(testBooks[i], testAuthor);
            assertFalse(foundBooks.isEmpty());
        }
        this.service.clearTheDB();
    }

    public void testRemoveBookCommand() throws Exception {
        String commandFormatter = "remove %s";
        String[] testBooks = {"War and peace", "война и мир", "Війна та мир"};
        String[] testAuthors = {"Leonid Tolstoy", "Лев Толстой", ""};
        service.save("War and peace", "Unknown");

        for (int i = 0; i < testBooks.length; i++) {
            service.save(testBooks[i], testAuthors[i]);
            this.mockCommandLineService.queueTheCommand(String.format(commandFormatter, testBooks[i]));
            if (i == 0) this.mockCommandLineService.queueTheCommand("1");
        }

        //the last command will break the readCmd cycle
        this.mockCommandLineService.queueTheCommand("exit");
        this.controller.readCmd();


        this.service.clearTheDB();

    }

    public void testRemoveBookPattern() {
        Pattern removeBookPattern = this.controller.getDeleteCommandCompiler();

        String commandFormat = "remove %s";
        String[] testBooks = {"the hate you give", "Round the Ireland with the fridge", "Множественные умы билли миллигана"};
        for (String book : testBooks) {
            String formattedCmd = String.format(commandFormat, book);
            Matcher matcher = removeBookPattern.matcher(formattedCmd);
            assertTrue(matcher.matches());
            String matchedBookName = matcher.group("book");
            assertTrue(Objects.equals(matchedBookName, book));
        }
    }

    public void testAddBookPattern() {
        Pattern addBookPattern = controller.getNewBookCommandCompiler();

        String commandFormat = "add %s \"%s\"";
        String[] testBooks = {"War and peace", "война и мир", "Війна та мир"};
        String[] testAuthors = {"Leonid Tolstoy", "Лев Толстой", "Unknown"};

        for (int i = 0; i < testBooks.length; i++) {
            String matchingCommand = String.format(commandFormat, testAuthors[i], testBooks[i]);
            Matcher matcher1 = addBookPattern.matcher(matchingCommand);

            assertTrue(matchingCommand + " did not match", matcher1.matches());

            String matchedAuthor = matcher1.group("author");
            String matchedBook = matcher1.group("book");

            assertTrue("book name did not match", Objects.equals(matchedBook, testBooks[i]));
            assertTrue(String.format("author name did not match %s!=%s", testAuthors[i], matchedAuthor),
                    Objects.equals(matchedAuthor, testAuthors[i]));
        }

    }

    private String simulateConsoleInput(String data) {
        InputStream stdin = System.in;
        try {
            System.setIn(new ByteArrayInputStream(data.getBytes()));
            Scanner scanner = new Scanner(System.in);
            return scanner.nextLine();
        } finally {
            System.setIn(stdin);
        }
    }


}