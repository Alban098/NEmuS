package openGL;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * This class represent a texture that can be loaded into VRAM and used from OpenGL rendering
 */
public class Texture {

    public final int id;
    private final int width;
    private final int height;
    private BufferedImage img;

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
     * Create a new Texture from a BufferedImage
     * any modification to the source BufferedImage can be pushed to VRAM using texture.update()
     *
     * @param image the image to convert into a texture
     */
    public Texture(BufferedImage image) {
        img = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        //Get the pixels
        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);
        //Fill in the Buffer
        ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y * width + x];
                buf.put((byte) ((pixel >> 16) & 0xFF));
                buf.put((byte) ((pixel >> 8) & 0xFF));
                buf.put((byte) ((pixel) & 0xFF));
                buf.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buf.flip();
        //Generate the texture
        id = glGenTextures();
        bind();
        //Set the filtering mode
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
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
     * Push the current state of the BufferedImage into VRAM
     * if the texture was created from one
     */
    public void update() {
        if (img != null) {
            //Get the pixels
            int[] pixels = new int[width * height];
            img.getRGB(0, 0, width, height, pixels, 0, width);
            //Fill in the buffer
            ByteBuffer buf = BufferUtils.createByteBuffer(width * height * 4);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = pixels[y * width + x];
                    buf.put((byte) ((pixel >> 16) & 0xFF));
                    buf.put((byte) ((pixel >> 8) & 0xFF));
                    buf.put((byte) ((pixel) & 0xFF));
                    buf.put((byte) ((pixel >> 24) & 0xFF));
                }
            }
            //Loading the buffer into VRAM
            buf.flip();
            bind();
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
        }
    }

    /**
     * Return the Texture as a BufferedImage
     * if it was created using one
     *
     * @return The texture as a BufferedImage or null
     */
    public BufferedImage getImg() {
        return img;
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

    public void cleanUp() {
        glDeleteTextures(id);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
