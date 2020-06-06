package openGL;

import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Quad {

    public int vaoId;
    private int vbo_vertices;
    private int vbo_textureCoords;

    public Quad(float[] vertices, float[] textureCoords, Collection<ShaderProgram> shaders) {

        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();
        FloatBuffer textureCoordsBuffer = MemoryUtil.memAllocFloat(textureCoords.length);
        textureCoordsBuffer.put(textureCoords).flip();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vbo_vertices = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_vertices);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        memFree(verticesBuffer);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

        vbo_textureCoords = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo_textureCoords);
        glBufferData(GL_ARRAY_BUFFER, textureCoordsBuffer, GL_STATIC_DRAW);
        memFree(textureCoordsBuffer);
        glVertexAttribPointer(1, 2, GL_FLOAT, true, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        for (ShaderProgram shader : shaders) {
            int loc = glGetUniformLocation(shader.programId, "tex");
            GL20.glUniform1i(loc, 0);
        }
    }

    public void render(ShaderProgram shader, Texture texture) {
        shader.bind();
        glActiveTexture(GL_TEXTURE0);
        texture.bind();

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawArrays(GL_QUADS, 0, 4);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.unbind();
    }

    public void cleanUp() {
        glDeleteBuffers(vbo_vertices);
        glDeleteBuffers(vbo_textureCoords);
        glDeleteVertexArrays(vaoId);
    }
}
