package eucalyptus;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class Interpreter {
    List<FunctionCall> functions;
    Map<String, Object> environment = new HashMap<>();

    public Interpreter(List<FunctionCall> functions) {
        this.functions = functions;
    }

    public void interpret() {
        int lineNumber = 1;
        try {
            for (FunctionCall function : functions) {
                acceptFunction(function, new HashMap<>());
                lineNumber++;
            }
        } catch (Exception e) {
            System.out.println("Error on line " + lineNumber + ": " + e.getMessage());
        }
    }

    private Object acceptFunction(FunctionCall function, Map<String, Object> localEnvironment) {
        String name = function.getName();
        switch (name) {
            case "print":
                return visitPrint(function, localEnvironment);
            case "defFunction":
                return visitDefFunction(function, localEnvironment);
            default:
                throw new RuntimeException("Unknown function: " + name);
        }
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
        System.out.println(userFunction);
        environment.put(functionName, userFunction);

        return null;
    }

    private Object acceptStatement(Object statement, Map<String, Object> localEnvironment) {
        if (statement instanceof FunctionCall) {
            return acceptFunction((FunctionCall) statement, localEnvironment);
        } else if (statement instanceof Literal) {
            return ((Literal) statement).getValue();
        } else if (statement instanceof Variable) {
            String name = ((Variable) statement).getName();
            Object value = environment.get(name);
            if (value == null) {
                throw new RuntimeException("Variable '" + name + "' is not defined");
            }
            return value;
        }
        throw new RuntimeException("Unknown statement: " + statement);
    }
}