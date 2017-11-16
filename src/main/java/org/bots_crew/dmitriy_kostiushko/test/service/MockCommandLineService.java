package org.bots_crew.dmitriy_kostiushko.test.service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class MockCommandLineService implements CommandLineInterface {
    private Queue<String> nextMessages = new ArrayBlockingQueue<>(10);

    @Override
    public String getUserCommand() {
        return nextMessages.poll();
    }

    @Override
    public void format(String s, Object... args) {
        System.out.println(String.format(s, args));
    }

    public void queueTheCommand(String command) {
        nextMessages.add(command);
    }

    public Queue<String> getNextMessages() {
        return nextMessages;
    }

}
