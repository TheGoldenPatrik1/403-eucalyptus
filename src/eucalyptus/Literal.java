package eucalyptus;

import java.util.List;
import java.util.Map;

public class Literal {
    private Object value;

    public Literal(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String toString() {
        return value.toString();
    }

    public String getType() {
        if (isDict()) {
            return "Dict";
        }
        if (isList()) {
            return "List";
        }
        return value.getClass().getSimpleName();
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    public boolean isString() {
        return value instanceof String;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    public boolean isList() {
        return value instanceof List;
    }

    public boolean isDict() {
        return value instanceof Map;
    }
}