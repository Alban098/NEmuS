package openGL.filters;

public class FilterInstance {

    public final Filter filter;
    public final Parameter[] parameters;

    public FilterInstance(Filter filter, Parameter[] parameters) {
        this.filter = filter;
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return filter.toString();
    }
}

