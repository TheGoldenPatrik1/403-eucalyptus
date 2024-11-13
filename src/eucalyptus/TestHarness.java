package eucalyptus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.PrintStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

class TestHarness {
    public static final PrintStream out = System.out;
    private FileRunner fileRunner;

    private int successes = 0;
    private int fails = 0;
    private int errors = 0;

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";

    private static final String FAILED = RED + "FAILED" + RESET;
    private static final String PASSED = GREEN + "PASSED" + RESET;

    public TestHarness(FileRunner fileRunner) {
        this.fileRunner = fileRunner;
    }

    public void run() {
        long startTime = System.nanoTime();

        runTests();

        long endTime = System.nanoTime();
        double duration = Math.round(((endTime - startTime) / 1_000_000_000.0) * 100.0) / 100.0;

        String testResult = GREEN + "SUCCESS";
        if (errors > 0 || fails > 0) {
            testResult = RED + "FAILURE";
        }
        testResult += RESET;

        System.out.println("\nTests completed with result: " + testResult);
        System.out.println("Errors: " + errors);
        System.out.println("Successes: " + successes);
        System.out.println("Failures: " + fails);
        System.out.println("Duration: " + duration + "s");
    }

    private void runTests() {
        try {
            checkDirectory("../tests");
            checkDirectory("../output");
            checkDirectory("../output/actual");
            checkDirectory("../output/expected");
        } catch (Exception e) {
            errors++;
            return;
        }

        File testDirectory = new File("../tests");
        File[] filesList = testDirectory.listFiles();
        List<File> testList = new ArrayList<>();

        if (filesList != null && filesList.length > 0) {
            for (File file : filesList) {
                if (!file.isDirectory()) {
                    continue;
                }
                File[] subFiles = file.listFiles();
                if (subFiles != null && subFiles.length > 0) {
                    String outputFilePath = "../output/actual/" + file.getName();
                    File outputDirectory = new File(outputFilePath);
                    if (!outputDirectory.exists()) {
                        outputDirectory.mkdirs();
                    }
                    testList.addAll(Arrays.asList(subFiles));
                } else {
                    System.out.println("Error: " + file.getName() + " directory is empty");
                    errors++;
                }
            }

            System.out.println("Running " + testList.size() + " tests...\n");
            for (File file : testList) {
                runTest(file);
            }
        } else {
            System.out.println("Error: the tests directory is empty");
            errors++;
        }
    }

    private void checkDirectory(String path) throws Exception {
        File directory = new File(path);
        if (!directory.exists()) {
            System.out.println("Error: " + directory.getName() + " directory not found");
            throw new Exception();
        }
        if (!directory.isDirectory()) {
            System.out.println("Error: " + directory.getName() + " directory is not a directory");
            throw new Exception();
        }
    }

    private void runTest(File file) {
        String folder = file.getParentFile().getName();
        String testName = file.getName().replace(".euc", "");
        String outputFilePath = "../output/actual/" + folder + "/" + testName + ".txt";
        String expectedFilePath = "../output/expected/" + folder + "/" + testName + ".txt";
        testName = folder + "_" + testName;

        // Check if expected output file exists
        File expectedFile = new File(expectedFilePath);
        if (!expectedFile.exists()) {
            printResult(testName, FAILED + " unexpectedly: expected output file not found");
            fails++;
            return;
        }

        // Run the test
        try {
            File outputFile = new File(outputFilePath);
            PrintStream fileStream = new PrintStream(outputFile);
            System.setOut(fileStream);
            fileRunner.run(file.getPath());
            System.setOut(out);
            fileStream.close();
        } catch (Exception e) {
            logError(testName, "Runtime Exception: " + e.getMessage());
            fails++;
            return;
        }

        // Compare actual output with expected output
        try {
            String actualOutput = Files.readString(Paths.get(outputFilePath)).trim();
            String expectedOutput = Files.readString(Paths.get(expectedFilePath)).trim();
            if (actualOutput.equals(expectedOutput)) {
                printResult(testName, PASSED);
                successes++;
            } else {
                fails++;
                String[] actualLines = actualOutput.split("\n");
                String[] expectedLines = expectedOutput.split("\n");
                if (actualLines.length != expectedLines.length) {
                    logError(testName, "Expected " + expectedLines.length + " lines, but got " + actualLines.length);
                } else {
                    for (int i = 0; i < actualLines.length; i++) {
                        if (!actualLines[i].equals(expectedLines[i])) {
                            StringBuilder error = new StringBuilder();
                            error.append("Mismatch at line ").append(i + 1).append("\n");
                            error.append("  Expected: ").append(expectedLines[i]).append("\n");
                            error.append("  Actual:   ").append(actualLines[i]);
                            logError(testName, error.toString());
                        }
                    }
                }
            }
        } catch (IOException e) {
            logError(testName, "IOException: " + e.getMessage());
            fails++;
        }
    }

    private void printResult(String testName, String result) {
        System.out.println("Test '" + testName + "' ... " + result);
    }

    private void logError(String testName, String message) {
        printResult(testName, FAILED);
        System.out.println("  " + message);
    }
}