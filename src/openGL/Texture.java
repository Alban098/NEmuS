package openGL;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class represent a texture that can be loaded into VRAM and used from OpenGL rendering
 */
public class Texture {

    private final int id;
    private final int width;
    private final int height;

    /**
     * Create a Texture of specified size and fill it using a ByteBuffer
     *
     * @param width  the width of the Texture
     * @param height the height of the Texture
     * @param buf    the buffer containing the pixel values
     */
    public Texture(int width, int height, ByteBuffer buf) {
        this.width = width;
        this.height = height;
        //Generate the texture
        id = glGenTextures();
        bind();
        //Set the filtering mode
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        //Load the buffer in VRAM
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
    }

    /**
     * Bind the texture to be used for rendering / data storing
     */
    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    /**
     * Get the width of the texture
     *
     * @return the texture's width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the height of the texture
     *
     * @return the texture's height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Load a byte buffer in the texture
     *
     * @param buf the buffer to load
     */
    public void load(ByteBuffer buf) {
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
    }

    /**
     * Delete the texture from memory
     */
    public void cleanUp() {
        glDeleteTextures(id);
    }

    /**
     * Unbind the current texture
     */
    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
