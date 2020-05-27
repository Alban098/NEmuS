package graphics;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite {

    private BufferedImage img;
    private int width;
    private int height;

    public Sprite(int width, int height) {
        this.width = width;
        this.height = height;
        img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }

    public void setPixel(int x, int y, Color color) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            img.setRGB(x, y, color.getRGB());
        }
    }

    public Color getPixel(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height)
            return new Color(img.getRGB(x, y));
        return Color.BLACK;
    }

    public BufferedImage getImage() {
        return img;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
