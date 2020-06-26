package openGL.filters;

public class Parameter {

    public String name;
    public Object value;
    public ParameterType type;

    public Parameter(String name, Object value, ParameterType type) {
        this.name = name;
        this.value = value;
        this.type = type;
    }
}
