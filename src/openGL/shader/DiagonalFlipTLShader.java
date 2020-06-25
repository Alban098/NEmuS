package openGL.shader;

public class DiagonalFlipTLShader extends ShaderProgram {

    /**
     * Create a new Shader from 2 files
     *
     * @throws Exception If the shader couldn't be created
     */
    public DiagonalFlipTLShader() throws Exception {
        super("shaders/d1_flip_vertex.glsl", "shaders/filters/no_filter.glsl");
    }
}
