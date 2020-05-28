package openGL;

import graphics.Sprite;
import org.lwjgl.BufferUtils;
import org.w3c.dom.Text;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

public class Texture {

    private int id;
    private int width;
    private int height;
    private ByteBuffer buf;
    private int[] pixels;
    private BufferedImage img;

    public Texture(Sprite sprite) {
        this(sprite.getImage());
    }

    public Texture(BufferedImage image) {
        img = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        pixels = new int[width*height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

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
        img.getRGB(0, 0, width, height, pixels, 0, width);

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

    public BufferedImage getImg() {
        return img;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
