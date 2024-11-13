# Eucalyptus

We are introducing a novel programming languange, *Eucalyptus*. In this repository, we are including an [overview](OVERVIEW.md) of our language and the design choices we made, a [grammar](GRAMMAR.md) for the language, a compilation of example Eucalyptus programs, a basic implementation of the language using [Java](https://www.java.com/en/), and tests for the implementation.

## The Team

- Malachi Crain (CS-403)
- Sawyer Kent (CS-503)

## Steps to Run

1. Install [Java](https://www.java.com/en/download/).
2. Execute `make` or `make run` for a REPL environment.
3. Alternately, execute `make run <input filepath>` to run a file of Eucalyptus code. Note that the Makefile will search the repository for the file, so there's no need to provide the full filepath.
4. If you are on Windows, you can compile and run the program manually using `javac` and `java eucalyptus.Eucalyptus [input filepath]`.