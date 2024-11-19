package eucalyptus;

import java.util.LinkedList;
import java.util.Queue;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Parser {
    private String input;
    private static final String TRAILING_COMMA_IN_FUNCTION = "Trailing comma in function call";
    private static final String TRAILING_COMMA_IN_LIST = "Trailing comma in list";
    private static final String TRAILING_COMMA_IN_DICT = "Trailing comma in dict";

    public Parser(String input) {
        this.input = input;
    }

    public List<FunctionCall> parse() throws Exception {
        Queue<String> tokens = tokenize(this.input);
        List<FunctionCall> functions = new ArrayList<>();
        int lineNumber = 1;
        try {
            while (!tokens.isEmpty()) {
                functions.add(parseLine(tokens));
                lineNumber++;
            }
        } catch (Exception e) {
            throw new Exception("ParseError on line " + lineNumber + ": " + e.getMessage());
        }
        return functions;
    }

    private Queue<String> tokenize(String input) {
        Queue<String> tokens = new LinkedList<>();
        StringBuilder token = new StringBuilder();
        boolean inString = false;
        char quote = 0;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);

            // Handle string literals
            if (c == '"' || c == '\'') {
                if (inString) {
                    if (c == quote) {
                        // End of string
                        token.append(c);
                        tokens.add(token.toString());
                        token.setLength(0);
                        inString = false;
                        quote = 0;
                    } else {
                        // Escaped quote
                        token.append(c);
                    }
                } else {
                    // Start of string
                    if (token.length() > 0) {
                        tokens.add(token.toString());
                        token.setLength(0);
                    }
                    token.append(c);
                    inString = true;
                    quote = c;
                }
                continue;
            }

            // If we are in a string, just keep appending characters
            if (inString) {
                token.append(c);
                continue;
            }

            // Handle other tokens
            List<Character> specialChars = List.of('(', ')', ',', '[', ']', ':', '{', '}');
            if (specialChars.contains(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
                tokens.add(String.valueOf(c)); // Add '(' or ')' or ','
            } else if (Character.isWhitespace(c)) {
                if (token.length() > 0) {
                    tokens.add(token.toString());
                    token.setLength(0);
                }
            } else {
                token.append(c); // Build function name
            }
        }

        // Add any remaining token at the end
        if (token.length() > 0) {
            tokens.add(token.toString());
        }

        if (inString) {
            throw new RuntimeException("Unterminated string literal; expected closing " + quote);
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

        boolean trailingComma = false;
        while (!tokens.isEmpty()) {
            if (tokens.peek().equals(")")) {
                if (trailingComma) {
                    throw new Exception(TRAILING_COMMA_IN_FUNCTION);
                }
                break;
            }
            trailingComma = false;
            Object parameter = parseStatement(tokens);
            parameters.add(parameter);
            if (tokens.peek().equals(",")) {
                tokens.poll(); // consume ','
                trailingComma = true;
            }
        }

        token = tokens.poll();
        if (token == null || !token.equals(")")) {
            throw new Exception("Expected ')', but got " + (token == null ? "nothing" : "'" + token + "'"));
        }

        if (trailingComma) {
            throw new Exception(TRAILING_COMMA_IN_FUNCTION);
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
            boolean trailingComma = false;
            while (!tokens.isEmpty()) {
                if (tokens.peek().equals("]")) {
                    if (trailingComma) {
                        throw new Exception(TRAILING_COMMA_IN_LIST);
                    }
                    tokens.poll();
                    return new Literal(list);
                }
                trailingComma = false;
                list.add(parseStatement(tokens));
                if (tokens.peek().equals(",")) {
                    tokens.poll(); // consume ','
                    trailingComma = true;
                }
            }
            if (trailingComma) {
                throw new Exception(TRAILING_COMMA_IN_LIST);
            }
            return new Literal(list);
        } else if (token.equals("{")) {
            // parse dict
            Map<String, Object> dict = new HashMap<>();
            boolean trailingComma = false;
            while (!tokens.isEmpty()) {
                if (tokens.peek().equals("}")) {
                    if (trailingComma) {
                        throw new Exception(TRAILING_COMMA_IN_DICT);
                    }
                    tokens.poll();
                    return new Literal(dict);
                }
                Object key = parseStatement(tokens);
                if (!(key instanceof Literal)) {
                    throw new Exception("Expected literal as key in dict, but got " + key);
                }
                Object keyValue = ((Literal) key).getValue();
                if (!(keyValue instanceof String)) {
                    throw new Exception("Expected string as key in dict, but got " + keyValue);
                }
                String keyString = (String) keyValue;
                String colon = tokens.poll();
                if (colon == null || !colon.equals(":")) {
                    throw new Exception("Expected ':', but got " + (colon == null ? "nothing" : "'" + colon + "'"));
                }
                Object value = parseStatement(tokens);
                dict.put(keyString, value);
                trailingComma = false;
                if (tokens.peek().equals(",")) {
                    tokens.poll(); // consume ','
                    trailingComma = true;
                }
            }
            return null;
        } else if (
            ((token.startsWith("\"") && token.endsWith("\"")) || (token.startsWith("'") && token.endsWith("'")))
            && token.length() > 1
        ) {
            // parse string
            String stringValue = token.substring(1, token.length() - 1);
            return new Literal(stringValue);
        } else {
            // it's either a literal, function call, or a variable
            if (tokens.peek().equals("(")) {
                // function call
                List<Object> arguments = new ArrayList<>();
                tokens.poll(); // consume '('
                boolean trailingComma = false;
                while (!tokens.isEmpty()) {
                    if (tokens.peek().equals(")")) {
                        if (trailingComma) {
                            throw new Exception(TRAILING_COMMA_IN_FUNCTION);
                        }
                        tokens.poll();
                        return new FunctionCall(token, arguments);
                    }
                    trailingComma = false;
                    arguments.add(parseStatement(tokens));
                    if (tokens.peek().equals(",")) {
                        tokens.poll(); // consume ','
                        trailingComma = true;
                    }
                }
                if (trailingComma) {
                    throw new Exception(TRAILING_COMMA_IN_FUNCTION);
                }
                return new FunctionCall(token, arguments);
            } else {
                if (token.equals("true") || token.equals("false")) {
                    // boolean literal
                    return new Literal(Boolean.parseBoolean(token));
                }
                if (token.equals("null")) {
                    // null literal
                    return new Literal(null);
                }
                // attempt to parse number
                try {
                    if (token.contains(".")) {
                        return new Literal(Double.parseDouble(token)); // Floating point number
                    } else {
                        return new Literal(Integer.parseInt(token)); // Integer
                    }
                } catch (NumberFormatException e) {
                    // it's a variable
                    return new Variable(token);
                }
            }
        }
    }
}