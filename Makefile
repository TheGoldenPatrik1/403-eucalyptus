# Define the source file and classpath
SRC_DIR = src/eucalyptus
CLASS = eucalyptus.Eucalyptus
BUILD_DIR = bin

# Find all .java files in the src directory and its subdirectories
SRC = $(shell find $(SRC_DIR) -name '*.java')

# Extract all arguments after the target
ARGS = $(wordlist 2, $(words $(MAKECMDGOALS)), $(MAKECMDGOALS))

default: run

compile: $(SRC)
	@echo "Compiling $(words $(SRC)) files in $(SRC_DIR)..."
	@javac -d $(BUILD_DIR) $(SRC)

# Default target: compile the Java program and run it with command line arguments
run: compile
	@if [ -n "$(ARGS)" ]; then \
		FILE_PATH=$$(find . -name $(ARGS)); \
		if [ -z "$$FILE_PATH" ]; then \
			echo "Error: file '$(ARGS)' not found!"; \
			exit 1; \
		else \
			echo "Running $(CLASS) on $$FILE_PATH..."; \
			cd bin && (trap '' INT; java $(CLASS) .$$FILE_PATH); \
		fi \
	else \
		echo "Running $(CLASS)..."; \
		cd bin && (trap '' INT; java $(CLASS)); \
	fi

# Allow passing command-line arguments to the Makefile
.PHONY: run

# Test target: compile the Java program and run its tests
test: compile
	@cd bin && java $(CLASS) test

# Clean up compiled class files
clean:
	@echo "Cleaning up..."
	@rm -rf $(BUILD_DIR)
	@echo "Done."

# Help command
help:
	@echo "Usage: make [target] [ARGS]"
	@echo "Targets:"
	@echo "  run          Run the program with optional filename that the program will attempt to locate and run"
	@echo "  compile      Compile the program"
	@echo "  test         Run the program's tests"
	@echo "  clean        Remove compiled class files"
	@echo "  help         Display this help message"

# Prevent make from interpreting arguments as targets
%:
	@: