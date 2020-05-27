import cartridge.Cartridge;
import cpu.Bus;
import cpu.Flags;
import graphics.Sprite;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWKeyCallback;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Demo extends JPanel {

    private static final int FRAME_DURATION = 1000000000/60;

    private static Bus console;
    private static Map<Integer, String> codeMap;
    private static boolean isKeyPressed = false;
    private static Thread emulation;
    private static boolean emulationRunning = true;

    private static long frameCount = 0;

    private static int ramPage = 0xFF;
    private static int selectedPalette = 0x00;

    public static void main(String[] args) throws IOException {
        console = new Bus();
        Cartridge cart = new Cartridge("smb.nes");
        console.insertCartridge(cart);
        codeMap = console.getCpu().disassemble(0x0000, 0xFFFF);
        console.getCpu().reset();

        JFrame frame = new JFrame("NES");
        JPanel screen = new Demo();
        screen.setDoubleBuffered(true);
        screen.setSize(1920, 1080);
        frame.add(screen);
        frame.setSize(1920, 1080);
        frame.setVisible(true);
        screen.requestFocus();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        emulation = new Thread(() -> {
            long next_frame = System.currentTimeMillis();
            while (true) {
                if (emulationRunning) {
                    if (System.nanoTime() > next_frame) {
                        next_frame = System.nanoTime() + FRAME_DURATION;
                        do {
                            console.clock();
                        } while (!console.getPpu().frameComplete);
                        console.getPpu().frameComplete = false;
                        frameCount++;
                        SwingUtilities.invokeLater(screen::repaint);
                    }
                }
            }
        });
        emulation.start();
        screen.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isKeyPressed) {
                    if (e.getKeyCode() == KeyEvent.VK_C) {
                        do { console.clock(); } while (!console.getCpu().complete());
                        do { console.clock(); } while (console.getCpu().complete());
                    }
                    if (e.getKeyCode() == KeyEvent.VK_V) {
                        for (int i = 0; i < 10; i++) {
                            do { console.clock(); } while (!console.getCpu().complete());
                            do { console.clock(); } while (console.getCpu().complete());
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_B) {
                        for (int i = 0; i < 50; i++) {
                            do { console.clock(); } while (!console.getCpu().complete());
                            do { console.clock(); } while (console.getCpu().complete());
                        }
                    }
                    if (e.getKeyCode() == KeyEvent.VK_F) {
                        do { console.clock(); } while (!console.getPpu().frameComplete);
                        do { console.clock(); } while (console.getCpu().complete());
                        console.getPpu().frameComplete = false;
                    }
                    if (e.getKeyCode() == KeyEvent. VK_SPACE)
                        emulationRunning = !emulationRunning;
                    if (e.getKeyCode() == KeyEvent. VK_R)
                        console.reset();
                    if (e.getKeyCode() == KeyEvent.VK_LEFT)
                        ramPage--;
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                        ramPage++;
                    if (e.getKeyCode() == KeyEvent.VK_Y)
                        ramPage += 0x10;
                    if (e.getKeyCode() == KeyEvent.VK_U)
                        ramPage -= 0x10;
                    if (e.getKeyCode() == KeyEvent.VK_P)
                        selectedPalette = (selectedPalette + 1) & 0x07;
                    if (ramPage < 0x00) ramPage += 0x100;
                    if (ramPage > 0xFF) ramPage -= 0x100;
                    isKeyPressed = true;
                    screen.repaint();
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN) console.controller[0] |= 0x04;
                if (e.getKeyCode() == KeyEvent.VK_UP) console.controller[0] |= 0x08;
                if (e.getKeyCode() == KeyEvent.VK_LEFT) console.controller[0] |= 0x02;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) console.controller[0] |= 0x01;
                if (e.getKeyCode() == KeyEvent.VK_S) console.controller[0] |= 0x10;
                if (e.getKeyCode() == KeyEvent.VK_A) console.controller[0] |= 0x20;
                if (e.getKeyCode() == KeyEvent.VK_Z) console.controller[0] |= 0x40;
                if (e.getKeyCode() == KeyEvent.VK_X) console.controller[0] |= 0x80;
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (isKeyPressed)
                    isKeyPressed = false;
                if (e.getKeyCode() == KeyEvent.VK_DOWN) console.controller[0] &= ~0x04;
                if (e.getKeyCode() == KeyEvent.VK_UP) console.controller[0] &= ~0x08;
                if (e.getKeyCode() == KeyEvent.VK_LEFT) console.controller[0] &= ~0x02;
                if (e.getKeyCode() == KeyEvent.VK_RIGHT) console.controller[0] &= ~0x01;
                if (e.getKeyCode() == KeyEvent.VK_S) console.controller[0] &= ~0x10;
                if (e.getKeyCode() == KeyEvent.VK_A) console.controller[0] &= ~0x20;
                if (e.getKeyCode() == KeyEvent.VK_Z) console.controller[0] &= ~0x40;
                if (e.getKeyCode() == KeyEvent.VK_X) console.controller[0] &= ~0x80;
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, 1920, 1080);
        drawSprite(30, 30, console.getPpu().getScreen(), g, 4);
        g.setFont(new Font("monospaced", Font.BOLD, 16));
        drawRam(1310, 678, ramPage << 8, 16, 16, g);
        g.setFont(new Font("monospaced", Font.BOLD, 14));
        drawCpu(1310, 41, g);
        drawCode(1310, 155, 27, g);
        drawOams(1070, 41, g, 64);
        drawSprite(1550, 30, console.getPpu().getPatternTable(0, selectedPalette), g, 2);
        drawSprite(1550, 310, console.getPpu().getPatternTable(1, selectedPalette), g, 2);
        drawPalette(1835, 50, g);
    }

    private static void drawPalette(int x, int y, Graphics g) {
        int swatchSize = 10;
        g.setColor(Color.RED);
        g.fillRect(x - 4, 46 + selectedPalette * swatchSize * 3 - 1, swatchSize * 4 + 8, swatchSize + 8);
        for (int p = 0; p < 8; p++) {
            for (int s = 0; s < 4; s++) {
                g.setColor(console.getPpu().getColorFromPalette(p, s));
                g.fillRect(x + s * swatchSize, y + p * swatchSize * 3, swatchSize, swatchSize);
            }
        }
    }

    private static void drawRam(int x, int y, int nAddr, int nRows, int nColumns, Graphics g) {
        g.setColor(Color.WHITE);
        int nRamX = x, nRamY = y;
        for (int row = 0; row < nRows; row++)
        {
            String sOffset = String.format("$%04X:", nAddr);
            for (int col = 0; col < nColumns; col++)
            {
                sOffset += " " +  String.format("%02X", console.cpuRead(nAddr, true));
                nAddr += 1;
            }
            g.drawString(sOffset, nRamX, nRamY);
            nRamY += 17;
        }
    }

    private static void drawCpu(int x, int y, Graphics g)  {
        g.setColor(Color.WHITE);
        g.drawString("STATUS:", x , y );
        if (console.getCpu().getFlag(Flags.N)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("N", x  + 64, y);
        if (console.getCpu().getFlag(Flags.V)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("V", x  + 80, y);
        if (console.getCpu().getFlag(Flags.U)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("-", x  + 96, y);
        if (console.getCpu().getFlag(Flags.B)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("B", x  + 112, y);
        if (console.getCpu().getFlag(Flags.D)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("D", x  + 128, y);
        if (console.getCpu().getFlag(Flags.I)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("I", x  + 144, y);
        if (console.getCpu().getFlag(Flags.Z)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("Z", x  + 160, y);
        if (console.getCpu().getFlag(Flags.C)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("C", x  + 178, y);
        g.setColor(Color.WHITE);
        g.drawString("PC: $" + String.format("%02X", console.getCpu().getPc()), x , y + 15);
        g.drawString("A: $" + String.format("%02X", console.getCpu().getA()) + "[" + console.getCpu().getA() + "]", x , y + 30);
        g.drawString("X: $" + String.format("%02X", console.getCpu().getX()) + "[" + console.getCpu().getX() + "]", x , y + 45);
        g.drawString("Y: $" + String.format("%02X", console.getCpu().getY()) + "[" + console.getCpu().getY() + "]", x , y + 60);
        g.drawString("Stack P: $" + String.format("%04X", console.getCpu().getStkp()), x , y + 75);
    }

    private static void drawCode(int x, int y, int nLines, Graphics g) {
        String currentLine = codeMap.get(console.getCpu().getPc());
        if (currentLine != null) {
            Queue<String> before = new LinkedList<>();
            Queue<String> after = new LinkedList<>();
            boolean currentLineFound = false;
            for (Map.Entry<Integer, String> line : codeMap.entrySet()) {
                if (!currentLineFound) {
                    if (line.getKey() == console.getCpu().getPc())
                        currentLineFound = true;
                    else
                        before.offer(line.getValue());
                    if (before.size() > nLines / 2)
                        before.poll();
                } else {
                    after.offer(line.getValue());
                    if (after.size() > nLines / 2)
                        break;
                }
            }
            int lineY = y;
            g.setColor(Color.WHITE);
            for (String line : before) {
                g.drawString(line, x, lineY);
                lineY += 15;
            }
            g.setColor(Color.CYAN);
            g.drawString(currentLine, x, lineY);
            lineY += 15;
            g.setColor(Color.WHITE);
            for (String line : after) {
                g.drawString(line, x, lineY);
                lineY += 15;
            }
        }
    }

    private static void drawSprite(int x, int y, Sprite sprite, Graphics g, int scale) {
        for (int i = 0; i < sprite.getWidth(); i++) {
            for (int j = 0; j < sprite.getHeight(); j++) {
                g.setColor(sprite.getPixel(i, j));
                g.fillRect(x + scale * i, y + scale * j, scale, scale);
            }
        }
    }

    private static void drawOams(int x, int y, Graphics g, int nb) {
        for (int i = 0; i < nb; i++) {
            String s = String.format("%02X:", i) + " (" + console.getPpu().oams[i].getX() + ", " + console.getPpu().oams[i].getY() + ") ID: " + String.format("%02X", console.getPpu().oams[i].getId()) + " AT: " + String.format("%02X", console.getPpu().oams[i].getAttribute());
            g.drawString(s, x, y + 15*i);
        }
    }
}
