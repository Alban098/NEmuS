package gui.lwjgui.windows;

import javafx.scene.paint.Color;

public class Tile {

    public final Color[] colors;
    public int tile = 0x00;
    public int addr = 0x0000;
    public int x = 0x00;
    public int y = 0x00;
    public int attribute = 0x00;
    public int palette = 0x00;

    public Tile(boolean doubleHeight) {
        if (doubleHeight)
            colors = new Color[128];
        else
            colors = new Color[64];
    }
}
