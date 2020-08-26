package openGL.filters;

public class Parameter {

    public final String name;
    public Object value;
    public final ParameterType type;

    public Parameter(String name, Object value, ParameterType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }
}
