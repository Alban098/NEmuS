package openGL;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.memFree;

/**
 * This class represents a Quad where we can render a texture
 * This Quad fill up the entire screen
 */
public class Quad {

    private static final float[] vertices = {-1, -1, 1, -1, 1, 1, -1, 1};

    private final int vaoId;
    private final int vboId;

    /**
     * Create a new Quad
     */
    public Quad() {
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

    /**
     * Render the Quad, the desired Shader and Texture should hav been bound previously
     * This quad will fill up the screen
     *
     * @param width  the width of the rendering target
     * @param height the height of the rendered target
     */
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

    /**
     * Delete all VAOs and VBOs used by the quad
     */
    public void cleanUp() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }
}
