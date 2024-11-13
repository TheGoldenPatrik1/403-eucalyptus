package eucalyptus;

import java.util.List;

public class Function {
    private String name;
    private List<Variable> parameters;
    private List<FunctionCall> statements;

    public Function(String name, List<Variable> parameters, List<FunctionCall> statements) {
        this.name = name;
        this.parameters = parameters;
        this.statements = statements;
    }

    public String getName() {
        return name;
    }

    public List<Variable> getParameters() {
        return parameters;
    }

    public List<FunctionCall> getStatements() {
        return statements;
    }

    public String toString() {
        return name + "(" + parameters + ") {" + statements + "}";
    }
}