package openGL;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

public class Quad {

    public int vaoId;
    private int vboId;

    public Quad(float[] vertices) {

        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        memFree(verticesBuffer);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render(int width, int height) {
        glViewport(0, 0, width, height);
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glActiveTexture(GL_TEXTURE0);
        glEnable(GL11.GL_DEPTH_TEST);

        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(0);

        glDrawArrays(GL_QUADS, 0, 4);

        glDisableVertexAttribArray(0);
        glBindVertexArray(0);

        glDisable(GL11.GL_DEPTH_TEST);
    }

    public void cleanUp() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }
}
