package openGL.filters;

public class FilterInstance {

    public Filter filter;
    public Parameter[] parameters;

    public FilterInstance(Filter filter, Parameter[] parameters) {
        this.filter = filter;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return filter.toString();
    }
}

