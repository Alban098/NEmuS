package openGL.shader;

public class HorizontalFlipShader extends ShaderProgram {

    /**
     * Create a new Shader from 2 files
     *
     * @throws Exception If the shader couldn't be created
     */
    public HorizontalFlipShader() throws Exception {
        super("shaders/h_flip_vertex.glsl", "shaders/filters/no_filter.glsl");
    }
}
