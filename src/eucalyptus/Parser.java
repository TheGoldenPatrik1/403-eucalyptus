package eucalyptus;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;

public class Parser {
    private String input;

    public Parser(String input) {
        this.input = input;
    }

    public List<FunctionCall> parse() throws Exception {
        Queue<String> tokens = tokenize(this.input);
        List<FunctionCall> functions = new ArrayList<>();
        while (!tokens.isEmpty()) {
            functions.add(parseLine(tokens));
        }
        return functions;
    }

    private Queue<String> tokenize(String input) {
        Queue<String> tokens = new LinkedList<>();
        StringBuilder token = new StringBuilder();
        boolean inString = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Handle string literals
            if (c == '"') {
                if (inString) {
                    // End of string
                    token.append(c);
                    tokens.add(token.toString());
                    token.setLength(0);
                    inString = false;
                } else {
                    // Start of string
                    if (token.length() > 0) {
                        tokens.add(token.toString());
                        token.setLength(0);
                    }
                    token.append(c);
                    inString = true;
                }
                continue;
            }

            // If we are in a string, just keep appending characters
            if (inString) {
                token.append(c);
                continue;
            }

            // Handle parentheses
            if (c == '(' || c == ')' || c == ',' || c == '[' || c == ']') {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(String.valueOf(c)); // Add '(' or ')' or ','
            } else if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                } else if (c == '\n') {
                    tokens.add(String.valueOf(c)); // Explicit newline token
                }
            } else {
                token.append(c); // Build function name
            }
        }

        // Add any remaining token at the end
        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        return tokens;
    }

    private FunctionCall parseLine(Queue<String> tokens) throws Exception {
        String name = tokens.poll();
        if (name == null) {
            throw new Exception("Expected function name, but got nothing");
        }

        List<Object> parameters = new ArrayList<>();

        String token = tokens.poll();
        if (token == null || !token.equals("(")) {
            throw new Exception("Expected '(', but got " + (token == null ? "nothing" : "'" + token + "'"));
        }

        while (!tokens.isEmpty()) {
            if (tokens.peek().equals(")")) {
                break;
            }
            Object parameter = parseStatement(tokens);
            parameters.add(parameter);
            if (tokens.peek().equals(",")) {
                tokens.poll(); // consume ','
            }
        }

        token = tokens.poll();
        if (token == null || !token.equals(")")) {
            throw new Exception("Expected ')', but got " + (token == null ? "nothing" : "'" + token + "'"));
        }

        token = tokens.poll();
        if (token != null && !token.equals("\n")) {
            throw new Exception("Expected '\\n', but got '" + token + "'");
        }

        return new FunctionCall(name, parameters);
    }

    private Object parseStatement(Queue<String> tokens) throws Exception {
        String token = tokens.poll();
        if (token == null) {
            throw new Exception("Expected statement, but got nothing");
        }

        if (token.equals("[")) {
            // parse list
            List<Object> list = new ArrayList<>();
            while (!tokens.isEmpty()) {
                if (tokens.peek().equals("]")) {
                    tokens.poll();
                    return new Literal(list);
                }
                list.add(parseStatement(tokens));
                if (tokens.peek().equals(",")) {
                    tokens.poll(); // consume ','
                }
            }
            return new Literal(list);
        } else if (token.startsWith("\"") && token.endsWith("\"") && token.length() > 1) {
            // parse string
            String stringValue = token.substring(1, token.length() - 1);
            return new Literal(stringValue);
        } else {
            // it's either a literal, function call, or a variable
            if (tokens.peek().equals("(")) {
                // function call
                List<Object> arguments = new ArrayList<>();
                tokens.poll(); // consume '('
                while (!tokens.isEmpty()) {
                    if (tokens.peek().equals(")")) {
                        tokens.poll();
                        return new FunctionCall(token, arguments);
                    }
                    arguments.add(parseStatement(tokens));
                }
                return new FunctionCall(token, arguments);
            } else {
                if (token.equals("true") || token.equals("false")) {
                    // boolean literal
                    return new Literal(Boolean.parseBoolean(token));
                }
                // attempt to parse number
                try {
                    if (token.contains(".")) {
                        return new Literal(Double.parseDouble(token)); // Floating point number
                    } else {
                        return new Literal(Integer.parseInt(token)); // Integer
                    }
                } catch (NumberFormatException e) {
                    // if it's not a number, treat it as a variable
                    return new Variable(token);
                }
            }
        }
    }
}