package openGL.shader;

import openGL.shader.uniform.Uniform;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

/**
 * This class represents a Shader Program containing a Vertex Shader and a Fragment Shader
 */
public class ShaderProgram {

    private final int programId;

    private int vertexShaderId;
    private int fragmentShaderId;

    /**
     * Create a new Shader from 2 files
     *
     * @param vertex   the vertex shader file
     * @param fragment the fragment shader file
     * @throws Exception If the shader couldn't be created
     */
    public ShaderProgram(String vertex, String fragment) throws Exception {
        programId = glCreateProgram();
        if (programId == 0)
            throw new Exception("Could not create Shader");
        createVertexShader(vertex);
        createFragmentShader(fragment);
        link();
    }

    /**
     * Initialize the vertex shader from a file
     *
     * @param filename the name of the file
     * @throws Exception If the file couldn't be read
     */
    private void createVertexShader(String filename) throws Exception {
        String code = Files.readString(Paths.get(filename));
        vertexShaderId = createShader(code, GL_VERTEX_SHADER);
    }

    /**
     * Initialize the fragment shader from a file
     *
     * @param filename the name of the file
     * @throws Exception If the file couldn't be read
     */
    private void createFragmentShader(String filename) throws Exception {
        String code = Files.readString(Paths.get(filename));
        fragmentShaderId = createShader(code, GL_FRAGMENT_SHADER);
    }

    /**
     * Create a new Shader from source code
     *
     * @param shaderCode the shader source code
     * @param shaderType the type of shader (Vertex or Fragment)
     * @return the id of the shader
     * @throws Exception If the shader couldn't be created or compiled
     */
    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling Shader code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);
        glBindFragDataLocation(programId, 0, "fragColor");

        return shaderId;
    }

    /**
     * Link the vertex and fragment shaders to the program
     *
     * @throws Exception When the shader couldn't be linked
     */
    private void link() throws Exception {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }
        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        storeAllUniformLocations();

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    /**
     * Bind the shader to be user for rendering
     */
    public void bind() {
        glUseProgram(programId);
    }

    /**
     * Unbind the shader
     */
    public void unbind() {
        glUseProgram(0);
    }

    /**
     * Allocate the memory on the GPU's RAM for all the Uniforms variables of this shader
     */
    public void storeAllUniformLocations(Uniform ... uniforms){
        for(Uniform uniform : uniforms){
            uniform.storeUniformLocation(programId);
        }
    }

    /**
     * Allocate the memory on the GPU's RAM for all the Uniforms variables of this shader
     */
    public ShaderProgram storeAllUniformLocations(Collection<Uniform> uniforms){
        for(Uniform uniform : uniforms){
            uniform.storeUniformLocation(programId);
        }
        return this;
    }

    /**
     * Remove the shader from memory
     */
    public void cleanUp() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}