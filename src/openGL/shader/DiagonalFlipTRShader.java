package openGL.shader;

public class DiagonalFlipTRShader extends ShaderProgram {

    /**
     * Create a new Shader from 2 files
     *
     * @throws Exception If the shader couldn't be created
     */
    public DiagonalFlipTRShader() throws Exception {
        super("shaders/d2_flip_vertex.glsl", "shaders/filters/no_filter.glsl");
    }
}
