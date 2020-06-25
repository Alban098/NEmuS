package openGL.shader;


public class VerticalFlipShader extends ShaderProgram {

    /**
     * Create a new Shader from 2 files
     *
     * @throws Exception If the shader couldn't be created
     */
    public VerticalFlipShader() throws Exception {
        super("shaders/v_flip_vertex.glsl", "shaders/filters/no_filter.glsl");
    }
}
