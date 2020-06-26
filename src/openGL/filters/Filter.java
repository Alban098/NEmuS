package openGL.filters;

import openGL.shader.uniform.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum Filter {

    DIAGONAL_FLIP_TL("Diagonal Flip (Top Left)", "shaders/d1_flip_vertex.glsl", "shaders/filters/no_filter.glsl", ""),
    DIAGONAL_FLIP_TR("Diagonal Flip (Top Right)", "shaders/d2_flip_vertex.glsl", "shaders/filters/no_filter.glsl", ""),
    GAUSSIAN_HORIZONTAL("Horizontal Gaussian Blur", "shaders/vertex.glsl", "shaders/filters/gaussian_horizontal.glsl", ""),
    GAUSSIAN_VERTICAL("Vertical Gaussian Blur", "shaders/vertex.glsl", "shaders/filters/gaussian_vertical.glsl", ""),
    HORIZONTAL_FLIP("Horizontal Flip", "shaders/h_flip_vertex.glsl", "shaders/filters/no_filter.glsl", ""),
    VERTICAL_FLIP("Vertical Flip", "shaders/v_flip_vertex.glsl", "shaders/filters/no_filter.glsl", ""),
    FISH_EYE("Fish Eye", "shaders/vertex.glsl", "shaders/filters/fish_eye.glsl", ""),
    EDGES("Edge Detector", "shaders/vertex.glsl", "shaders/filters/edge.glsl", ""),
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
            if (uniform instanceof UniformInteger) type = ParameterType.INTEGER;
            else if (uniform instanceof UniformFloat) type = ParameterType.FLOAT;
            else if (uniform instanceof UniformVec2) type = ParameterType.VEC2;
            else if (uniform instanceof UniformVec3) type = ParameterType.VEC3;
            else if (uniform instanceof UniformVec4) type = ParameterType.VEC4;
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
