package org.bots_crew.dmitriy_kostiushko.test.controllers;

import org.bots_crew.dmitriy_kostiushko.test.dto.UserCommand;
import org.bots_crew.dmitriy_kostiushko.test.enteties.Book;
import org.bots_crew.dmitriy_kostiushko.test.service.BookShelfService;

import java.io.Console;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserCommandsController {
    private Console console;
    private BookShelfService bookShelfService;
    private UserCommand[] userCommands = new UserCommand[5];

    private final String greetingMessage = "Welcome to the BookShelf application.%n" +
            "To view books in your library use command: all books. %n" +
            "To add a new book to the library please use command new book and follow instructions. %n" +
            "To delete book from the library use command remove book and follow instructions %n" +
            "Thank you. %n";

    private Pattern singleDigitPattern = Pattern.compile("^(?<num>\\d+)$");

    private Pattern deleteCommandCompiler =
            Pattern.compile("remove\\s*(?<book>[\\w\\s.-_]+)", Pattern.CASE_INSENSITIVE);

    private Pattern newBookCommandCompiler =
            Pattern.compile("add\\s*(?<author>[\\w\\s.-_]+)?\\s*(\"(?<book>[\\d\\w\\s_.-]+)\")?", Pattern.CASE_INSENSITIVE);

    private Pattern help =
            Pattern.compile("help", Pattern.CASE_INSENSITIVE);

    private Pattern viewAllBooksCompiler =
            Pattern.compile("all books", Pattern.CASE_INSENSITIVE);

    private Pattern helpCommandCompiler =
            Pattern.compile("help", Pattern.CASE_INSENSITIVE);

    private Pattern exitCommandCompiler =
            Pattern.compile("exit", Pattern.CASE_INSENSITIVE);

    private final String confirmationMessage = "(y/n)";

    public UserCommandsController(BookShelfService bookShelfService) {
        this.bookShelfService = bookShelfService;
        fillComandsHashTable();
        console = System.console();
        if (console == null) {
            System.out.println("Application must be started inside of the command line");
            System.exit(1);
        } else {
            console.format(greetingMessage);
            this.readCmd();
        }
    }

    private void fillComandsHashTable() {
        userCommands[0] = new UserCommand(viewAllBooksCompiler, this::responseToViewAll);
        userCommands[1] = new UserCommand(newBookCommandCompiler, this::responseToNewBook);
        userCommands[2] = new UserCommand(deleteCommandCompiler, this::responseToDelBook);
        userCommands[3] = new UserCommand(exitCommandCompiler, this::exitFromProgram);
        userCommands[4] = new UserCommand(helpCommandCompiler, this::responseToHelp);
    }


    private void readCmd() {
        Scanner consoleInScanner = new Scanner(System.in);
        String command = consoleInScanner.nextLine();
        boolean matchFound = false;
        for (UserCommand userCommand : userCommands) {
            Pattern pattern = userCommand.getCommandPattern();
            Matcher matcher = pattern.matcher(command);
            if (matcher.matches()) {
                matchFound = true;
                userCommand.getCommandResponse().responceToTheCmd(matcher);
            }
        }
        if (!matchFound) {
            console.format("Sorry i cannot understand the command, please try again.%n");
            this.responseToHelp(null);
        }
        readCmd();
    }

    private void responseToHelp(Matcher matcher) {

        console.format("Please select one of the following commands:%n");

        console.format("1. \"Add\" command is used to add books to the library.%n");
        console.format("%40s%n", "add author_name \"Book name\"");

        console.format("2. All books command is used to view all books inside of the library.%n");
        console.format("%40s%n", "all books");

        console.format("3. Remove command is used to delete books from the library.%n");
        console.format("%40s%n", "remove book_name");

        console.format("4. Type exit to exit from the program.%n");

    }

    private void responseToViewAll(Matcher matcher) {
        ArrayList<Book> foundBooks = this.bookShelfService.findAllBooks();
        if (foundBooks.isEmpty())
            console.format("Unfortunately there are no books in library yet, please use \"add\" command to fix that.%n");
        else {
            for (Book book : foundBooks) {
                console.format("book: \"%s\", author: %s%n", book.getBookName(), book.getAuthorName());
            }
        }
    }

    private void responseToNewBook(Matcher matcher) {
        String bookName = matcher.group("book");
        String authorsName = matcher.group("author");

        boolean authorsNameIsEmptyOrNull = authorsName == null || authorsName.isEmpty();
        boolean bookNameIsEmptyOrNull = bookName == null || bookName.isEmpty();

        if (bookNameIsEmptyOrNull) {
            bookName = "Unknown";
        }

        if (authorsNameIsEmptyOrNull) {
            authorsName = "Unknown";
        }
        console.format("Adding a book name:%s; author:%s%n", bookName, authorsName);

        if (bookNameIsEmptyOrNull && authorsNameIsEmptyOrNull) {
            console.format(
                    "Please follow the next pattern: add authors_name \"the name of a book\" %n" +
                            "Type \"help\" to see how to use each command%n");
            return;
        }
        if (bookNameIsEmptyOrNull && !authorsNameIsEmptyOrNull) {
            console.format("Please separate name of the book inside of quotation marks, like this:%n " +
                    "authors_name \"the name of a book\"%n" +
                    "Type \"help\" to see how to use each command%n");
            return;
        }
        ArrayList<Book> existingBooks = this.bookShelfService.findByNameAndAuthor(bookName, authorsName);
        if (!existingBooks.isEmpty()) {
            console.format("Book of the author %s \" %s \" already exists in the library%n", authorsName, bookName);
            return;
        }
        this.bookShelfService.save(bookName, authorsName);
        console.format("Book \"%s\" of author %s was added to the library %n", bookName, authorsName);
    }

    private void responseToDelBook(Matcher matcher) {
        String bookName = matcher.group("book");

        console.format("Deleting the book %s%n", bookName);
        ArrayList<Book> foundBooks = this.bookShelfService.findByName(bookName);
        if (foundBooks.isEmpty())
            console.format("No books were found with name %s.%n", bookName);
        else if (foundBooks.size() > 1) {
            console.format("Please enter the number of the book you would like to delete.%n");
            for (int i = 0; i < foundBooks.size(); i++) {
                Book book = foundBooks.get(i);
                console.format("%d: author: %s \"%s\" %n", i + 1, book.getAuthorName(), book.getBookName());
            }
            int selectedNumber = readArrayIndex(foundBooks.size());
            Book selectedBook = foundBooks.get(selectedNumber - 1);
            String selectedBookName = selectedBook.getBookName();
            String selectedBookAuthor = selectedBook.getAuthorName();
            this.bookShelfService.deleteBook(selectedBookName, selectedBookAuthor);
            console.format("Book %s of author %s was removed. %n", selectedBookName, selectedBookAuthor);
        }
    }

    private int readArrayIndex(int maxIndex) {
        int selectedNumber = getSingleNumInput();
        if (selectedNumber < 0 || selectedNumber > maxIndex) {
            console.format("Please select one of the numbers.%n");
            selectedNumber = readArrayIndex(maxIndex);
        }
        return selectedNumber;
    }

    private int getSingleNumInput() {
        String numInput = console.readLine();
        Matcher numMathcer = singleDigitPattern.matcher(numInput);
        if (numMathcer.matches()) {
            return Integer.valueOf(numMathcer.group("num"));
        } else {
            console.format("This does not look like a number. Please try again.%n");
            return getSingleNumInput();
        }
    }

    private int getSingleNumInput(Predicate<Integer> test, String errorMessage) {
        String numInput = console.readLine();
        Matcher numMathcer = singleDigitPattern.matcher(numInput);
        boolean matchesCondition = false;
        int matchedNumber = 0;
        while (!matchesCondition) {
            if (numMathcer.matches()) {
                int enteredNumber = Integer.valueOf(numMathcer.group("num"));
                matchesCondition = test.test(enteredNumber);
                if (!matchesCondition) {
                    console.format(errorMessage);
                }
                matchedNumber = enteredNumber;
            } else {
                console.format("This does not look like a number. Please try again.%n");
            }
        }
        return matchedNumber;
    }

    private boolean getConsoleConfirmation(String message) {
        console.format(message);
        console.format(confirmationMessage);
        String answer = console.readLine();
        if (answer.equals("y")) return true;
        else if (answer.equals("n")) return false;
        else return getConsoleConfirmation(message);
    }

    private void exitFromProgram(Matcher matcher) {
        console.format("Bye =)%n");
        System.exit(1);
    }
}
