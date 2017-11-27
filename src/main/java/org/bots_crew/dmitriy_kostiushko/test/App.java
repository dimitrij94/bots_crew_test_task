package org.bots_crew.dmitriy_kostiushko.test;

import org.bots_crew.dmitriy_kostiushko.test.controllers.UserCommandsController;
import org.bots_crew.dmitriy_kostiushko.test.service.BookShelfService;
import org.bots_crew.dmitriy_kostiushko.test.service.H2DatabaseConnector;
import org.bots_crew.dmitriy_kostiushko.test.service.RealCommandLineService;

import java.io.Console;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Hello world!
 */
public class App {
    private static H2DatabaseConnector connector;

    private final static String greetingMessage = "Welcome to the BookShelf application.%n" +
            "To view books in your library use command: all books. %n" +
            "To add a new book to the library please use command:\"add authors name \"book name\"\" and follow instructions. %n" +
            "To delete book from the library use command \"remove book name\" and follow instructions %n" +
            "Thank you. %n";


    public static void main(String[] args) throws IOException {
        Console console = System.console();
        if (console == null) {
            System.out.println("Application must be started inside of the command line");
            return;
        }

        connector = new H2DatabaseConnector();
        Runtime.getRuntime().addShutdownHook(new Thread(App::closeApplication));

        BookShelfService bookShelfService = new BookShelfService(connector);

        RealCommandLineService commandLineService = new RealCommandLineService();
        commandLineService.setConsole(console);

        UserCommandsController controller = new UserCommandsController(bookShelfService, commandLineService);


        console.format(greetingMessage);
        controller.readCmd();
    }

    private static void closeApplication() {
        try {
            System.out.println("Connection closed");
            connector.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

/*    public static void testConverString() throws IOException {

        System.out.println("Default charset: " +
                Charset.defaultCharset().name());
        BufferedReader in =
                new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
        System.out.printf("Enter 'абвгд эюя': ");
        String line = in.readLine();
        String s = "абвгд эюя";
        byte[] sBytes = s.getBytes();
        System.out.println("strg bytes: " + Arrays.toString(sBytes));
        byte[] lineBytes = line.getBytes();
        System.out.println("line bytes: " + Arrays.toString(lineBytes));
        PrintStream out = new PrintStream(System.out, true, "UTF-8");
        out.print("--->" + s + "<----\n");
        out.print("--->" + line + "<----\n");
    }
*/
}
