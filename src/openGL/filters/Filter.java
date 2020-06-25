package openGL.filters;

import openGL.shader.uniform.Uniform;
import openGL.shader.uniform.UniformFloat;
import openGL.shader.uniform.UniformInteger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public enum Filter {

    DIAGONAL_FLIP_TL("Diagonal Flip (Top Left)", "shaders/d1_flip_vertex.glsl", "shaders/filters/no_filter.glsl"),
    DIAGONAL_FLIP_TR("Diagonal Flip (Top Right)", "shaders/d2_flip_vertex.glsl", "shaders/filters/no_filter.glsl"),
    GAUSSIAN("Gaussian Blur", "shaders/vertex.glsl", "shaders/filters/gaussian.glsl", new UniformInteger("kernel_size", 5), new UniformFloat("dispersion", 0.8f)),
    HORIZONTAL_FLIP("Horizontal Flip", "shaders/h_flip_vertex.glsl", "shaders/filters/no_filter.glsl"),
    VERTICAL_FLIP("Vertical Flip", "shaders/v_flip_vertex.glsl", "shaders/filters/no_filter.glsl"),
    FISH_EYE("Fish Eye", "shaders/vertex.glsl", "shaders/filters/fish_eye.glsl"),
    TOONIFY("Toonify", "shaders/vertex.glsl", "shaders/filters/toonify.glsl", new UniformFloat("edge_low", 0.2f), new UniformFloat("edge_high", 5.0f));

    String name;
    String vertexFile;
    String fragmentFile;
    Map<String, Uniform> uniforms;

    Filter(String name, String vertex, String fragment, Uniform... uniforms) {
        this.name = name;
        this.vertexFile = vertex;
        this.fragmentFile = fragment;
        this.uniforms = new HashMap<>();
        for (Uniform uniform : uniforms)
            this.uniforms.put(uniform.getName(), uniform);
    }

    public Uniform getUniform(String name) {
        return uniforms.get(name);
    }

    public Collection<Uniform> getAllUniforms() {
        return uniforms.values();
    }

    @Override
    public String toString() {
        return name;
    }
}
