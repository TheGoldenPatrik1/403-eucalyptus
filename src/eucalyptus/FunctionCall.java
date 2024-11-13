package eucalyptus;

import java.util.List;

public class FunctionCall {
    private String name;
    private List<Object> arguments;

    public FunctionCall(String name, List<Object> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public String toString() {
        return name + "(" + arguments + ")";
    }
}