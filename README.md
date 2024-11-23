# Eucalyptus

We are introducing a novel programming languange, *Eucalyptus*. In this repository, we are including an [overview](OVERVIEW.md) of our language, the design choices we made, and some example Eucalyptus programs; a [grammar](GRAMMAR.md) for the language; a basic implementation of the language using [Java](https://www.java.com/en/); and extensive tests for the implementation.

## The Team

- Malachi Crain (CS-403)
- Sawyer Kent (CS-503)

## Steps to Run

1. Install [Java](https://www.java.com/en/download/).
2. Execute `make` or `make run` for a REPL environment.
3. Alternately, execute `make run <input filepath>` to run a file of Eucalyptus code. Note that the Makefile will search the repository for the file, so there's no need to provide the full filepath.
4. If you are on Windows, you can compile and run the program manually using `javac` and `java eucalyptus.Eucalyptus [input filepath]`.

### Testing Plan

We have **60** test files, most with multiple tests, collectively covering every aspect of the Eucalyptus programming language that we implemented so far. The language implementation provided here is incomplete, but we have tests for every part of that implementation. They are divided into the following categories:

- **add** - 2 test files for the `add()` function
- **and** - 2 test files for the `and()` function
- **def** - 6 test files for the `def()` function
- **def_function** - 9 test files for the `defFunction()` function
- **eq** - 2 test files for the `eq()` function
- **for_each** - 3 test files for the `forEach()` function
- **get** - 4 test files for the `get()` function
- **if** - 3 test files for the `if()` function
- **inc** - 2 test files for the `inc()` function
- **len** - 2 test files for the `len()` function
- **lt** - 2 test files for the `lt()` function
- **mult** - 2 test files for the `mult()` function
- **or** - 2 test files for the `or()` function
- **parser** - 5 test files for the language's parser
- **print** - 3 test files for the `print()` function
- **recursion** - 2 test for recursion
- **return** - 3 test files for the `return()` function
- **sub** - 4 test files for the `sub()` function
- **while** - 2 test files for the `while()` function

### Sample Test Run

An example test run is provided in `sample_test_run.txt`. This file was automatically generated using `make test > sample_test_run.txt`.

### Test Harness

1. The `/tests` directory contains `.euc` files which our test harness executes.
2. For each file, the output of the `print()` statements is written to a corresponding `.txt` file in the `/output/actual` directory.
3. The test harness then compares this output file to a corresponding `.txt` file in the `/output/expected` directory to assert that the two files match exactly. The `expected` file's contents are manually created based on what the test out to output.
4. In the event that the `.euc` test file generates an error, it will output the error into the `/output/actual` file, allowing the test harness to anticipate, expect, and gracefully handle errors.

### Steps to Run Test Harness

1. Execute `make test`.
2. If you are on Windows, you can compile manually using `javac` and run the tests using `java eucalyptus.Eucalyptus test`.