package org.bots_crew.dmitriy_kostiushko.test;

import org.bots_crew.dmitriy_kostiushko.test.controllers.UserCommandsController;
import org.bots_crew.dmitriy_kostiushko.test.service.BookShelfService;
import org.bots_crew.dmitriy_kostiushko.test.service.H2DatabaseConnector;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        H2DatabaseConnector connector = new H2DatabaseConnector();
        BookShelfService bookShelfService = new BookShelfService(connector);
        UserCommandsController controller = new UserCommandsController(bookShelfService);

    }
}
