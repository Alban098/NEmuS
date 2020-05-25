package graphics;

import java.awt.*;

public class Sprite {

    private Color[] pixels;
    private int width;
    private int height;

    public Sprite(int width, int height) {
        this.width = width;
        this.height = height;
        pixels = new Color[width * height];
        for (int i = 0; i < pixels.length; i++)
            pixels[i] = Color.BLACK;
    }

    public void setPixel(int x, int y, Color color) {
        if (x >= 0 && x < width && y >= 0 && y < height)
            pixels[y*width + x] = color;
    }

    public Color getPixel(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height)
            return pixels[y*width + x];
        return Color.BLACK;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
