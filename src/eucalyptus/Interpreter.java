package eucalyptus;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;

public class Interpreter {
    List<FunctionCall> functions;
    Map<String, Object> environment = new HashMap<>();
    String currentFunction;

    public Interpreter(List<FunctionCall> functions) {
        this.functions = functions;
    }

    public void interpret() throws Exception {
        int lineNumber = 1;
        try {
            for (FunctionCall function : functions) {
                acceptFunction(function, environment);
                lineNumber++;
            }
        } catch (Exception e) {
            if (currentFunction == null) {
                throw new Exception("Error on line " + lineNumber + ": " + e.getMessage());
            } else {
                throw new Exception("Error on line " + lineNumber + " while executing function '" + currentFunction + "': " + e.getMessage());
            }
        }
    }

    private Object acceptFunction(FunctionCall function, Map<String, Object> localEnvironment) {
        String name = function.getName();
        // alphabetized list of built-in functions
        switch (name) {
            case "def":
                return visitDef(function, localEnvironment);
            case "defFunction":
                return visitDefFunction(function, localEnvironment);
            case "print":
                return visitPrint(function, localEnvironment);
            default:
                return visitUserFunction(function, localEnvironment);
        }
    }

    private Object acceptStatement(Object statement, Map<String, Object> localEnvironment) {
        if (statement instanceof FunctionCall) {
            return acceptFunction((FunctionCall) statement, localEnvironment);
        } else if (statement instanceof Literal) {
            return ((Literal) statement).getValue();
        } else if (statement instanceof Variable) {
            String name = ((Variable) statement).getName();
            Object value = localEnvironment.get(name);
            if (value == null) {
                throw new RuntimeException("Variable '" + name + "' is not defined");
            }
            return value;
        }
        throw new RuntimeException("Unknown statement: " + statement);
    }

    private Object visitDef(FunctionCall function, Map<String, Object> localEnvironment) {
        List<Object> arguments = function.getArguments();
        if (arguments.size() != 2) {
            throw new RuntimeException("'def' function expects 2 arguments, got " + arguments.size());
        }
        if (!(arguments.get(0) instanceof Variable)) {
            throw new RuntimeException("First argument of 'def' function must be a variable");
        }

        String variableName = ((Variable) arguments.get(0)).getName();
        if (isScreamingSnakeCase(variableName) && localEnvironment.containsKey(variableName)) {
            throw new RuntimeException("Cannot reassign constant variable '" + variableName + "'");
        }

        if (!isScreamingSnakeCase(variableName) && !isSnakeCase(variableName)) {
            throw new RuntimeException("Variable name must be in snake_case if mutable or SCREAMING_SNAKE_CASE if constant");
        }
        Object value = acceptStatement(arguments.get(1), localEnvironment);
        localEnvironment.put(variableName, value);

        return null;
    }

    @SuppressWarnings("unchecked")
    private Object visitDefFunction(FunctionCall function, Map<String, Object> localEnvironment) {
        List<Object> arguments = function.getArguments();
        if (arguments.size() != 3) {
            throw new RuntimeException("'defFunction' function expects 3 arguments, got " + arguments.size());
        }
        if (!(arguments.get(0) instanceof Variable)) {
            throw new RuntimeException("First argument of 'defFunction' function must be a variable");
        }

        String functionName = ((Variable) arguments.get(0)).getName();
        if (!isCamelCase(functionName)) {
            throw new RuntimeException("Function name must be in camelCase");
        }
        
        List<Variable> parameters;
        if (arguments.get(1) instanceof Literal && ((Literal) arguments.get(1)).isList()) {
            parameters = (List<Variable>) ((Literal) arguments.get(1)).getValue();
        } else if (arguments.get(1) instanceof Variable) {
            parameters = List.of((Variable) arguments.get(1));
        } else {
            throw new RuntimeException("Second argument of 'defFunction' function must be a variable or list of variables");
        }

        List<FunctionCall> statements;
        if (arguments.get(2) instanceof Literal && ((Literal) arguments.get(2)).isList()) {
            statements = (List<FunctionCall>) ((Literal) arguments.get(2)).getValue();
        } else if (arguments.get(2) instanceof FunctionCall) {
            statements = List.of((FunctionCall) arguments.get(2));
        } else {
            throw new RuntimeException("Third argument of 'defFunction' function must be a function call or list of function calls");
        }

        Function userFunction = new Function(functionName, parameters, statements);
        localEnvironment.put(functionName, userFunction);

        return null;
    }

    private Object visitPrint(FunctionCall function, Map<String, Object> localEnvironment) {
        List<Object> arguments = function.getArguments();
        if (arguments.isEmpty()) {
            throw new RuntimeException("'print' function expects at least 1 argument, got " + arguments.size());
        }
        for (Object argument : arguments) {
            Object value = acceptStatement(argument, localEnvironment);
            System.out.println(value);
        }
        return null;
    }

    private Object visitUserFunction(FunctionCall function, Map<String, Object> localEnvironment) {
        String name = function.getName();
        Object functionValue = localEnvironment.get(name);
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
            throw new RuntimeException("Function '" + userFunction.getName() + "' expects " + parameters.size() + " parameters, but got " + arguments.size());
        }

        Map<String, Object> newLocalEnvironment = new HashMap<>(localEnvironment);
        for (int i = 0; i < parameters.size(); i++) {
            Variable parameter = parameters.get(i);
            Object argument = arguments.get(i);
            Object parameterValue = acceptStatement(argument, localEnvironment);
            newLocalEnvironment.put(parameter.getName(), parameterValue);
        }

        currentFunction = name;
        for (FunctionCall statement : statements) {
            acceptFunction(statement, newLocalEnvironment);
        }
        currentFunction = null;

        return null;
    }

    public static boolean isCamelCase(String input) {
        String camelCasePattern = "^[a-z][a-z0-9]*([A-Z][a-z0-9]*)*$";
        return Pattern.matches(camelCasePattern, input);
    }

    public static boolean isScreamingSnakeCase(String input) {
        String screamingSnakeCasePattern = "^[A-Z][A-Z0-9]*(_[A-Z][A-Z0-9]*)*$";
        return Pattern.matches(screamingSnakeCasePattern, input);
    }

    public static boolean isSnakeCase(String input) {
        String snakeCasePattern = "^[a-z][a-z0-9]*(_[a-z][a-z0-9]*)*$";
        return Pattern.matches(snakeCasePattern, input);
    }
}