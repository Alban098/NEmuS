package openGL;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;

/**
 * This class represent a Frame Buffer Object where we can render and from which we can get the rendered image as a texture
 */
public class Fbo {

    private final int width;
    private final int height;
    private int frameBuffer;
    private int texture;

    /**
     * Creates an FBO of a specified width and height
     *
     * @param width  the width of the FBO
     * @param height the height of the FBO
     */
    public Fbo(int width, int height) {
        this.width = width;
        this.height = height;
        initialiseFrameBuffer();
    }

    /**
     * Return the width of the buffer
     *
     * @return the width of the buffer
     */
    public int getWidth() {
        return width;
    }

    /**
     * Return the height of the buffer
     *
     * @return the height of the buffer
     */
    public int getHeight() {
        return height;
    }

    /**
     * Deletes the frame buffer and its attachments
     */
    public void cleanUp() {
        GL30.glDeleteFramebuffers(frameBuffer);
        GL11.glDeleteTextures(texture);
    }

    /**
     * Binds the frame buffer, setting it as the current render target. Anything
     * rendered after this will be rendered to this FBO
     */
    public void bindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
        GL11.glViewport(0, 0, width, height);
    }

    /**
     * Unbinds the frame buffer, setting the default frame buffer as the current
     * render target. Anything rendered after this will be rendered to the
     * screen
     */
    public void unbindFrameBuffer() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        GL11.glViewport(0, 0, width, height);
    }

    /**
     * Return The ID of the texture containing the colour buffer of the FBO
     *
     * @return The ID of the texture containing the colour buffer of the FBO
     */
    public int getTexture() {
        return texture;
    }


    /**
     * Creates the FBO along with a colour buffer texture attachment
     */
    private void initialiseFrameBuffer() {
        createFrameBuffer();
        createTextureAttachment();
        unbindFrameBuffer();
    }

    /**
     * Creates a new frame buffer object and sets the buffer to which drawing
     * will occur - colour attachment 0. This is the attachment where the colour
     * buffer texture is
     */
    private void createFrameBuffer() {
        frameBuffer = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
        GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
    }

    /**
     * Creates a texture and sets it as the colour buffer attachment for this
     * FBO
     */
    private void createTextureAttachment() {
        texture = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);
    }
}