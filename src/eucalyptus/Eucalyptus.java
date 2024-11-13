package eucalyptus;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Paths;

@FunctionalInterface
interface FileRunner {
    void run(String filePath);
}

public class Eucalyptus {
    public static void main(String[] args) {
        if (args.length > 0) {
            String argument = args[0];
            if (argument.equalsIgnoreCase("test")) {
                runTests();
            } else {
                runFile(argument);
            }
        } else {
            runPrompt();
        }
    }

    private static void runFile(String filename) {
        try {
            String content = Files.readString(Paths.get(filename));
            run(content);
        } catch (IOException e) {
            System.out.println("Error reading file: " + filename);
        }
    }

    private static void runPrompt() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter input (type 'exit' to stop):");
        System.out.print("> ");
        String userInput;
        while (!(userInput = scanner.nextLine()).equalsIgnoreCase("exit")) {
            run(userInput);
            System.out.print("> ");
        }
        scanner.close();
    }

    private static void run(String input) {
        try {
            Parser parser = new Parser(input);
            List<FunctionCall> functions = parser.parse();
            Interpreter interpreter = new Interpreter(functions);
            interpreter.interpret();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void runTests() {
        TestHarness testHarness = new TestHarness(Eucalyptus::runFile);
        testHarness.run();
    }
}