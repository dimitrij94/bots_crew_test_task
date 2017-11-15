package org.bots_crew.dmitriy_kostiushko.test.controllers;

import junit.framework.TestCase;
import org.bots_crew.dmitriy_kostiushko.test.service.BookShelfService;
import org.bots_crew.dmitriy_kostiushko.test.service.H2DatabaseConnector;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserCommandsControllerTest extends TestCase {
    UserCommandsController controller;

    public void setUp() throws Exception {
        super.setUp();
        H2DatabaseConnector connector = new H2DatabaseConnector();
        BookShelfService service = new BookShelfService(connector);
        controller = new UserCommandsController(service);
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

    public void testConverString() {

        String testingString = simulateConsoleInput("Щось не йде ця..");
        byte[] in = testingString.getBytes(StandardCharsets.UTF_8);
        String out = new String(in, StandardCharsets.UTF_8);
        assertTrue(String.format("%s != %s", testingString, out), Objects.equals(testingString, out));
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
}