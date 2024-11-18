package eucalyptus;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

public class Interpreter {
    List<FunctionCall> functions;
    Environment env = new Environment();
    String currentFunction;
    Set<String> reservedFunctions;

    public Interpreter(List<FunctionCall> functions) {
        this.functions = functions;

        Path filePath = Paths.get("../src/reserved_functions.txt");
        try {
            List<String> reservedFunctionList = Files.readAllLines(filePath);
            reservedFunctions = new HashSet<>(reservedFunctionList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void interpret() throws Exception {
        int lineNumber = 1;
        try {
            for (FunctionCall function : functions) {
                acceptFunction(function);
                lineNumber++;
            }
        } catch (Exception e) {
            if (currentFunction == null) {
                throw new Exception("Error on line " + lineNumber + ": " + e.getMessage());
            } else {
                throw new Exception("Error on line " + lineNumber + " while executing function '" + currentFunction
                        + "': " + e.getMessage());
            }
        }
    }

    private Object acceptFunction(FunctionCall function) {
        String name = function.getName();
        // alphabetized list of built-in functions
        // each built-in function should also be listed in reserved_functions.txt
        switch (name) {
            case "add":
                return visitAdd(function);
            case "def":
                return visitDef(function);
            case "defFunction":
                return visitDefFunction(function);
            case "eq":
                return visitEqFunction(function);
            case "forEach":
                return visitForEach(function);
            case "if":
                return visitIf(function);
            case "lt":
                return visitLess(function);
            case "mult":
                return visitMult(function);
            case "print":
                return visitPrint(function);
            case "return":
                return visitReturn(function);
            default:
                return visitUserFunction(function);
        }
    }

    @SuppressWarnings("unchecked")
    private Object acceptStatement(Object statement) {
        if (statement instanceof FunctionCall) {
            return acceptFunction((FunctionCall) statement);
        } else if (statement instanceof Literal) {
            Literal literalStatement = (Literal) statement;
            if (literalStatement.isList()) {
                List<Object> list = new ArrayList<>();
                for (Object item : (List<Object>) literalStatement.getValue()) {
                    Object value = acceptStatement(item);
                    if (value != null) {
                        list.add(value);
                    }
                }
                return list;
            } else {
                return literalStatement.getValue();
            }
        } else if (statement instanceof Variable) {
            String name = ((Variable) statement).getName();
            Object value = env.getVariable(name);
            if (value == null) {
                throw new RuntimeException("Variable '" + name + "' is not defined");
            }
            return value;
        }
        throw new RuntimeException("Unknown statement: " + statement);
    }

    @SuppressWarnings("unchecked")
    private Object visitAdd(FunctionCall function) {
        List<Object> arguments = function.getArguments();

        if (arguments.size() < 2) {
            throw new RuntimeException("'add' function expects at least 2 arguments, got " + arguments.size());
        }

        Object result = acceptStatement(arguments.get(0));

        for (int i = 1; i < arguments.size(); i++) {
            Object next = acceptStatement(arguments.get(i));

            if (result instanceof Integer && next instanceof Integer) {
                result = (int) result + (int) next;
            } else if (result instanceof Number && next instanceof Number) {
                result = ((Number) result).doubleValue() + ((Number) next).doubleValue();
            } else if (result instanceof String && next instanceof String) {
                result = (String) result + (String) next;
            } else if (result instanceof List && next instanceof List) {
                List<Object> newResult = new ArrayList<>((List<Object>) result);
                newResult.addAll((List<Object>) next);
                result = newResult;
            } else {
                String resultName = result.getClass().getSimpleName().replace("ArrayList", "List");
                String nextName = next.getClass().getSimpleName().replace("ArrayList", "List");
                throw new RuntimeException("Cannot add " + resultName + " and " + nextName);
            }
        }

        return result;
    }

    private Object visitDef(FunctionCall function) {
        List<Object> arguments = function.getArguments();
        if (arguments.size() != 2) {
            throw new RuntimeException("'def' function expects 2 arguments, got " + arguments.size());
        }
        if (!(arguments.get(0) instanceof Variable)) {
            throw new RuntimeException("First argument of 'def' function must be a variable");
        }

        String variableName = ((Variable) arguments.get(0)).getName();
        if (reservedFunctions.contains(variableName)) {
            throw new RuntimeException("Cannot define variable with reserved name '" + variableName + "'");
        }
        if (isScreamingSnakeCase(variableName) && env.hasVariable(variableName)) {
            throw new RuntimeException("Cannot reassign constant variable '" + variableName + "'");
        }
        if (!isScreamingSnakeCase(variableName) && !isSnakeCase(variableName)) {
            throw new RuntimeException(
                    "Variable name must be in snake_case if mutable or SCREAMING_SNAKE_CASE if constant");
        }

        Object value = acceptStatement(arguments.get(1));
        env.setVariable(variableName, value);

        return null;
    }

    @SuppressWarnings("unchecked")
    private Object visitDefFunction(FunctionCall function) {
        List<Object> arguments = function.getArguments();
        if (arguments.size() != 3) {
            throw new RuntimeException("'defFunction' function expects 3 arguments, got " + arguments.size());
        }
        if (!(arguments.get(0) instanceof Variable)) {
            throw new RuntimeException("First argument of 'defFunction' function must be a variable");
        }

        String functionName = ((Variable) arguments.get(0)).getName();
        if (reservedFunctions.contains(functionName)) {
            throw new RuntimeException("Cannot define function with reserved name '" + functionName + "'");
        }
        if (!isCamelCase(functionName)) {
            throw new RuntimeException("Function name must be in camelCase");
        }

        List<Variable> parameters;
        if (arguments.get(1) instanceof Literal && ((Literal) arguments.get(1)).isList()) {
            parameters = (List<Variable>) ((Literal) arguments.get(1)).getValue();
        } else if (arguments.get(1) instanceof Variable) {
            parameters = List.of((Variable) arguments.get(1));
        } else {
            throw new RuntimeException(
                    "Second argument of 'defFunction' function must be a variable or list of variables");
        }

        List<FunctionCall> statements = extractFunctionCalls(arguments.get(2), "Third", "defFunction");

        Function userFunction = new Function(functionName, parameters, statements);
        env.setVariable(functionName, userFunction);

        return null;
    }

    private Object visitEqFunction(FunctionCall function) {

        List<Object> arguments = function.getArguments();

        if (arguments.size() != 2) {
            throw new RuntimeException("'eq' function expects exactly 2 arguments, got " + arguments.size());
        }

        Object first = acceptStatement(arguments.get(0));
        Object second = acceptStatement(arguments.get(1));

        return first.equals(second);
    }

    @SuppressWarnings("unchecked")
    private Object visitForEach(FunctionCall function) {
        List<Object> arguments = function.getArguments();
        if (arguments.size() != 3) {
            throw new RuntimeException("'forEach' function expects 3 arguments, got " + arguments.size());
        }
        if (!(arguments.get(0) instanceof Variable)) {
            throw new RuntimeException("First argument of 'forEach' function must be a variable");
        }

        final String LIST_ERROR_MESSAGE = "Second argument of 'forEach' function must be a list";
        Object value = null;
        if (arguments.get(1) instanceof Literal) {
            Literal listLiteral = (Literal) arguments.get(1);
            if (!listLiteral.isList()) {
                throw new RuntimeException(LIST_ERROR_MESSAGE);
            }
            value = acceptStatement(listLiteral);
        } else if (arguments.get(1) instanceof Variable) {
            String variableName = ((Variable) arguments.get(1)).getName();
            value = env.getVariable(variableName);
            if (value == null) {
                throw new RuntimeException("Variable '" + variableName + "' is not defined");
            }
            if (!(value instanceof List)) {
                throw new RuntimeException(LIST_ERROR_MESSAGE);
            }
        } else {
            throw new RuntimeException(LIST_ERROR_MESSAGE);
        }

        List<FunctionCall> statements = extractFunctionCalls(arguments.get(2), "Third", "forEach");

        String variableName = ((Variable) arguments.get(0)).getName();
        if (reservedFunctions.contains(variableName)) {
            throw new RuntimeException("Cannot define variable with reserved name '" + variableName + "'");
        }
        if (!isSnakeCase(variableName)) {
            throw new RuntimeException("Variable name must be in snake_case");
        }

        env.enterScope();
        for (Object item : (List<Object>) value) {
            env.setVariable(variableName, item);
            for (FunctionCall statement : statements) {
                Object result = acceptFunction(statement);
                if (result != null) {
                    env.exitScope();
                    return result;
                }
            }
        }
        env.exitScope();

        return null;
    }

    private Object visitIf(FunctionCall function) {
        List<Object> arguments = function.getArguments();

        if (arguments.size() != 3) {
            throw new RuntimeException("'if' function expects exactly 3 arguments, got " + arguments.size());
        }

        Object conditional = acceptStatement(arguments.get(0));

        if ((Boolean) conditional) {
            return acceptStatement(arguments.get(1));
        }
        return acceptStatement(arguments.get(2));
    }

    private Object visitLess(FunctionCall function) {
        List<Object> arguments = function.getArguments();

        if (arguments.size() != 2) {
            throw new RuntimeException("'eq' function expects exactly 2 arguments, got " + arguments.size());
        }

        Object first = acceptStatement(arguments.get(0));
        Object second = acceptStatement(arguments.get(1));

        return first.toString().compareTo(second.toString()) < 0;
    }

    private Object visitMult(FunctionCall function) {

        List<Object> arguments = function.getArguments();

        if (arguments.size() < 2) {
            throw new RuntimeException("'mult' function expects at least 2 arguments, got " + arguments.size());
        }

        Object result = acceptStatement(arguments.get(0));

        for (int i = 1; i < arguments.size(); i++) {
            Object next = acceptStatement(arguments.get(i));

            if (result instanceof Integer && next instanceof Integer) {
                result = (int) result * (int) next;
            } else if (result instanceof Number && next instanceof Number) {
                result = ((Number) result).doubleValue() * ((Number) next).doubleValue();
            } else {
                String resultName = result.getClass().getSimpleName().replace("ArrayList", "List");
                String nextName = next.getClass().getSimpleName().replace("ArrayList", "List");
                throw new RuntimeException("Cannot multiply " + resultName + " and " + nextName);
            }
        }

        return result;
    }

    private Object visitPrint(FunctionCall function) {
        List<Object> arguments = function.getArguments();
        if (arguments.isEmpty()) {
            throw new RuntimeException("'print' function expects at least 1 argument, got " + arguments.size());
        }
        for (Object argument : arguments) {
            Object value = acceptStatement(argument);
            if (value != null) {
                System.out.println(value);
            }
        }
        return null;
    }

    private Object visitReturn(FunctionCall function) {
        List<Object> arguments = function.getArguments();
        if (arguments.size() != 1) {
            throw new RuntimeException("'return' function expects 1 argument, got " + arguments.size());
        }
        return acceptStatement(arguments.get(0));
    }

    private Object visitUserFunction(FunctionCall function) {
        String name = function.getName();
        Object functionValue = env.getVariable(name);
        if (functionValue == null) {
            throw new RuntimeException("Function '" + name + "' is not defined");
        } else if (!(functionValue instanceof Function)) {
            throw new RuntimeException("Cannot call variable '" + name + "' as it is not a function");
        }
        Function userFunction = (Function) functionValue;

        List<Variable> parameters = userFunction.getParameters();
        List<FunctionCall> statements = userFunction.getStatements();
        List<Object> arguments = function.getArguments();

        if (parameters.size() != arguments.size()) {
            throw new RuntimeException("Function '" + userFunction.getName() + "' expects " + parameters.size()
                    + " parameters, but got " + arguments.size());
        }

        env.enterScope();

        for (int i = 0; i < parameters.size(); i++) {
            Variable parameter = parameters.get(i);
            Object argument = arguments.get(i);
            Object parameterValue = acceptStatement(argument);
            env.setVariable(parameter.getName(), parameterValue);
        }

        currentFunction = name;
        for (FunctionCall statement : statements) {
            Object value = acceptFunction(statement);
            if (value != null) {
                env.exitScope();
                currentFunction = null;
                return value;
            }
        }
        currentFunction = null;

        env.exitScope();
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<FunctionCall> extractFunctionCalls(Object arg, String argNumber, String functionName) {
        List<FunctionCall> functionCalls;
        final String ERROR_MESSAGE = argNumber + " argument of '" + functionName
                + "' function must be a list of function calls";
        if (arg instanceof Literal && ((Literal) arg).isList()) {
            functionCalls = (List<FunctionCall>) ((Literal) arg).getValue();
        } else if (arg instanceof FunctionCall) {
            functionCalls = List.of((FunctionCall) arg);
        } else {
            throw new RuntimeException(ERROR_MESSAGE);
        }
        return functionCalls;
    }

    private static boolean isCamelCase(String input) {
        String camelCasePattern = "^[a-z][a-z0-9]*([A-Z][a-z0-9]*)*$";
        return Pattern.matches(camelCasePattern, input);
    }

    private static boolean isScreamingSnakeCase(String input) {
        String screamingSnakeCasePattern = "^[A-Z][A-Z0-9]*(_[A-Z][A-Z0-9]*)*$";
        return Pattern.matches(screamingSnakeCasePattern, input);
    }

    private static boolean isSnakeCase(String input) {
        String snakeCasePattern = "^[a-z][a-z0-9]*(_[a-z][a-z0-9]*)*$";
        return Pattern.matches(snakeCasePattern, input);
    }
}