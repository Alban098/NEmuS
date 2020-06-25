package openGL.shader;

public class GaussianHorizontalShader extends ShaderProgram {

    /**
     * Create a new Shader from 2 files
     *
     * @throws Exception If the shader couldn't be created
     */
    public GaussianHorizontalShader() throws Exception {
        super("shaders/vertex.glsl", "shaders/filters/gaussian_horizontal.glsl");
    }
}
