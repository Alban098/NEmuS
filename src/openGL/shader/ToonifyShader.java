package openGL.shader;

import openGL.shader.uniform.UniformFloat;

public class ToonifyShader extends ShaderProgram {

    public UniformFloat edge_low = new UniformFloat("edge_low");
    public UniformFloat edge_high = new UniformFloat("edge_high");

    /**
     * Create a new Shader from 2 files
     *
     * @throws Exception If the shader couldn't be created
     */
    public ToonifyShader() throws Exception {
        super("shaders/vertex.glsl", "shaders/filters/toonify.glsl");
        super.storeAllUniformLocations(edge_low, edge_high);
    }
}
