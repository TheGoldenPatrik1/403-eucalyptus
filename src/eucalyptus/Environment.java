package eucalyptus;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Environment {
    private Deque<Map<String, Object>> scopes;
    private PrintWriter debug;

    public Environment() {
        scopes = new LinkedList<>();
        scopes.push(new HashMap<>());
        try {
            debug = new PrintWriter("/Users/sawyerkent/Documents/CompSci/CS503/403-eucalyptus/src/Debugger.txt");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Enter a new scope
    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    // Exit the current scope
    public void exitScope() {
        if (scopes.size() > 1) {
            scopes.pop();
        } else {
            throw new IllegalStateException("Cannot exit global scope.");
        }
    }

    // Define or update a variable in the current scope
    public void setVariable(String name, Object value) {
        if (!hasVariable(name)) {
            String text = "Created " + name + " with value " + value;
            debug.println(text);
        } else {
            String text = "Changed " + name + " to the value " + value;
            debug.println(text);
        }
        scopes.peek().put(name, value);
    }

    // Retrieve a variable from the current or outer scopes
    public Object getVariable(String name) {
        for (Map<String, Object> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }

    // Check if a variable exists in any scope
    public boolean hasVariable(String name) {
        for (Map<String, Object> scope : scopes) {
            if (scope.containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public void closeDebugger() {
        debug.close();
    }
}
