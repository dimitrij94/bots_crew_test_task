package org.bots_crew.dmitriy_kostiushko.test;

import org.bots_crew.dmitriy_kostiushko.test.controllers.UserCommandsController;
import org.bots_crew.dmitriy_kostiushko.test.service.BookShelfService;
import org.bots_crew.dmitriy_kostiushko.test.service.H2DatabaseConnector;

import java.io.Console;

/**
 * Hello world!
 */
public class App {


    private final static String greetingMessage = "Welcome to the BookShelf application.%n" +
            "To view books in your library use command: all books. %n" +
            "To add a new book to the library please use command new book and follow instructions. %n" +
            "To delete book from the library use command remove book and follow instructions %n" +
            "Thank you. %n";

    public static void main(String[] args) {

        H2DatabaseConnector connector = new H2DatabaseConnector();
        BookShelfService bookShelfService = new BookShelfService(connector);
        UserCommandsController controller = new UserCommandsController(bookShelfService);
        Console console = System.console();

        if (console == null) {
            System.out.println("Application must be started inside of the command line");
            System.exit(1);
        }

        console.format(greetingMessage);
        controller.readCmd(console);
    }
}
