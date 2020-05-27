package openGL;

import graphics.Sprite;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {

    private int id;
    private int width;
    private int height;
    private ByteBuffer buf;
    private int[] pixels;
    private Sprite sprite;

    public Texture(Sprite sprite) {
        this.sprite = sprite;
        this.width = sprite.getWidth();
        this.height = sprite.getHeight();
        pixels = new int[width*height];
        sprite.getImage().getRGB(0, 0, width, height, pixels, 0, width);

        buf = BufferUtils.createByteBuffer(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y*width + x];
                buf.put((byte)((pixel >> 16) & 0xFF));
                buf.put((byte)((pixel >> 8) & 0xFF));
                buf.put((byte)((pixel) & 0xFF));
                buf.put((byte)((pixel >> 24) & 0xFF));
            }
        }
        buf.flip();
        id = glGenTextures();
        bind();
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, id);
    }

    public void update() {
        sprite.getImage().getRGB(0, 0, width, height, pixels, 0, width);

        buf = BufferUtils.createByteBuffer(width * height * 4);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = pixels[y*width + x];
                buf.put((byte)((pixel >> 16) & 0xFF));
                buf.put((byte)((pixel >> 8) & 0xFF));
                buf.put((byte)((pixel) & 0xFF));
                buf.put((byte)((pixel >> 24) & 0xFF));
            }
        }
        buf.flip();
        bind();
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buf);
    }

    public void delete() {
        glDeleteTextures(id);
    }
}
