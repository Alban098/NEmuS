package openGL.shader;

public class GaussianVerticalShader extends ShaderProgram {

    /**
     * Create a new Shader from 2 files
     *
     * @throws Exception If the shader couldn't be created
     */
    public GaussianVerticalShader() throws Exception {
        super("shaders/vertex.glsl", "shaders/filters/gaussian_vertical.glsl");
    }
}
