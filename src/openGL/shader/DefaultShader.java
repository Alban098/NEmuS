package openGL.shader;

public class DefaultShader extends ShaderProgram {

    /**
     * Create a new Shader from 2 files
     *
     * @throws Exception If the shader couldn't be created
     */
    public DefaultShader() throws Exception {
        super("shaders/vertex.glsl", "shaders/filters/no_filter.glsl");
    }
}
