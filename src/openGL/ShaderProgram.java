package openGL;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

public class ShaderProgram {

    public final int programId;

    private int vertexShaderId;
    private int fragmentShaderId;

    public ShaderProgram(String vertex, String fragment) throws Exception {
        programId = glCreateProgram();
        if (programId == 0)
            throw new Exception("Could not create Shader");
        createVertexShader(vertex);
        createFragmentShader(fragment);
        link();
    }

    private void createVertexShader(String filename) throws Exception {
        String code = Files.readString(Paths.get(filename));
        vertexShaderId = createShader(code, GL_VERTEX_SHADER);
    }

    private void createFragmentShader(String filename) throws Exception {
        String code = Files.readString(Paths.get(filename));
        fragmentShaderId = createShader(code, GL_FRAGMENT_SHADER);
    }

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

    public void link() throws Exception {
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

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + glGetProgramInfoLog(programId, 1024));
        }

    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanUp() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}