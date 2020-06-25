package openGL.shader;

public class FishEyeShader extends ShaderProgram {

    /**
     * Create a new Shader from 2 files
     *
     * @throws Exception If the shader couldn't be created
     */
    public FishEyeShader() throws Exception {
        super("shaders/vertex.glsl", "shaders/filters/fish_eye.glsl");
    }
}
