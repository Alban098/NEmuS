package openGL.filters;

import openGL.shader.uniform.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum Filter {

    DIAGONAL_FLIP_TL("Diagonal Flip (Top Left)", "shaders/d1_flip_vertex.glsl", "shaders/filters/no_filter.glsl", "Flip the screen on the diagonal going from the top left to the bottom right"),
    DIAGONAL_FLIP_TR("Diagonal Flip (Top Right)", "shaders/d2_flip_vertex.glsl", "shaders/filters/no_filter.glsl", "Flip the screen on the diagonal going from the top right to the bottom left"),
    GAUSSIAN_HORIZONTAL("Horizontal Gaussian Blur", "shaders/vertex.glsl", "shaders/filters/gaussian_horizontal.glsl", "Apply a horizontal gaussian blur"),
    GAUSSIAN_VERTICAL("Vertical Gaussian Blur", "shaders/vertex.glsl", "shaders/filters/gaussian_vertical.glsl", "Apply a vertical gaussian blur"),
    HORIZONTAL_FLIP("Horizontal Flip", "shaders/h_flip_vertex.glsl", "shaders/filters/no_filter.glsl", "Flip the screen vertically"),
    VERTICAL_FLIP("Vertical Flip", "shaders/v_flip_vertex.glsl", "shaders/filters/no_filter.glsl", "Flip the screen horizontally"),
    FISH_EYE("Fish Eye", "shaders/vertex.glsl", "shaders/filters/fish_eye.glsl", "Apply a fish-eye effect at the center of the screen", new UniformFloat("strength", 1.5f)),
    EDGES("Edge Detector", "shaders/vertex.glsl", "shaders/filters/edge.glsl", "Detect and show the edges present on screen"),
    GRAYSCALE("Grayscale", "shaders/vertex.glsl", "shaders/filters/grayscale.glsl", "Render the screen as a grayscale image"),
    CROSS_STICHING("Cross Stiching", "shaders/vertex.glsl", "shaders/filters/cross_stiching.glsl", "Replaces pixels by crosses of specified size", new UniformFloat("cross_size", 6f), new UniformBoolean("invert", true)),
    TOONIFY("Toonify", "shaders/vertex.glsl", "shaders/filters/toonify.glsl", "", new UniformFloat("edge_low", 0.2f), new UniformFloat("edge_high", 5.0f));

    String name;
    String vertexFile;
    String fragmentFile;
    String description;
    Map<String, Uniform> uniforms;

    Filter(String name, String vertex, String fragment, String description, Uniform... uniforms) {
        this.name = name;
        this.vertexFile = vertex;
        this.fragmentFile = fragment;
        this.uniforms = new HashMap<>();
        this.description = description;
        for (Uniform uniform : uniforms)
            this.uniforms.put(uniform.getName(), uniform);
    }

    public Uniform getUniform(String name) {
        return uniforms.get(name);
    }

    public Collection<Uniform> getAllUniforms() {
        return uniforms.values();
    }

    public String getDescription() {
        return description;
    }

    public Parameter[] getDefaultParameters() {
        Parameter[] parameters = new Parameter[uniforms.size()];
        int i = 0;
        for (Uniform uniform : uniforms.values()) {
            ParameterType type = null;
            if (uniform instanceof UniformBoolean) type = ParameterType.BOOLEAN;
            else if (uniform instanceof UniformInteger) type = ParameterType.INTEGER;
            else if (uniform instanceof UniformFloat) type = ParameterType.FLOAT;
            else if (uniform instanceof UniformVec2) type = ParameterType.VEC2;
            else if (uniform instanceof UniformVec3) type = ParameterType.VEC3;
            else if (uniform instanceof UniformVec4) type = ParameterType.VEC4;
            else if (uniform instanceof UniformMat2) type = ParameterType.MAT2;
            else if (uniform instanceof UniformMat3) type = ParameterType.MAT3;
            else if (uniform instanceof UniformMat4) type = ParameterType.MAT4;
            parameters[i] = new Parameter(uniform.getName(), uniform.getDefault(), type);
            i++;
        }
        return parameters;
    }

    @Override
    public String toString() {
        return name;
    }
}
