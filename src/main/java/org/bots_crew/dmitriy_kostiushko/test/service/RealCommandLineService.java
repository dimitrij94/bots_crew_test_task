package org.bots_crew.dmitriy_kostiushko.test.service;

import java.io.Console;
import java.util.Scanner;

public class RealCommandLineService implements CommandLineInterface {
    private Console console;

    @Override
    public String getUserCommand() {
        Scanner in = new Scanner(System.in);
        return in.nextLine();
    }

    @Override
    public void format(String s, Object... args) {
        console.format(s, args);
    }

    public Console getConsole() {
        return console;
    }

    public void setConsole(Console console) {
        this.console = console;
    }
}
