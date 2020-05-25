import cpu.Bus;
import cpu.Flags;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Demo extends Canvas {

    private static Bus console;
    private static Map<Integer, String> codeMap;
    private static boolean isKeyPressed = false;

    public static void main(String[] args) {
        console = new Bus();

        console.loadStringByte("A2 0A 8E 00 00 A2 03 8E 01 00 AC 00 00 A9 00 18 6D 01 00 88 D0 FA 8D 02 00 EA EA EA", 0x8000);
        console.getRam()[0xFFFC] = 0x00;
        console.getRam()[0xFFFD] = 0x80;
        codeMap = console.getCpu().disassemble(0x0000, 0xFFFF);
        console.getCpu().reset();

        JFrame frame = new JFrame("NES");
        Canvas screen = new Demo();
        screen.setSize(1400, 800);
        frame.add(screen);
        frame.pack();
        frame.setVisible(true);
        screen.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (!isKeyPressed) {
                    if (e.getKeyCode() == 32)
                        do {
                            console.getCpu().clock();
                        }
                        while (!console.getCpu().complete());
                    if (e.getKeyCode() == 82)
                        console.getCpu().reset();
                    if (e.getKeyCode() == 73)
                        console.getCpu().irq();
                    if (e.getKeyCode() == 78)
                        console.getCpu().nmi();
                    isKeyPressed = true;
                    screen.repaint();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (isKeyPressed)
                    isKeyPressed = false;
            }
        });
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.setFont(new Font("monospaced", Font.BOLD, 18));
        g.fillRect(0, 0, 1400, 800);
        drawRam(5, 20, 0x0000, 16, 16, g);
        drawRam(5, 400, 0x8000, 16, 16, g);
        drawCpu(800, 20, g);
        drawCode(800, 170, 26, g);

    }

    private static void drawRam(int x, int y, int nAddr, int nRows, int nColumns, Graphics g) {
        g.setColor(Color.WHITE);
        int nRamX = x, nRamY = y;
        for (int row = 0; row < nRows; row++)
        {
            String sOffset = String.format("$%04X:", nAddr);
            for (int col = 0; col < nColumns; col++)
            {
                sOffset += " " +  String.format("%02X", console.read(nAddr, true));
                nAddr += 1;
            }
            g.drawString(sOffset, nRamX, nRamY);
            nRamY += 19;
        }
    }

    private static void drawCpu(int x, int y, Graphics g)  {
        g.drawString("STATUS:", x , y );
        if (console.getCpu().getFlag(Flags.N)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("N", x  + 84, y);
        if (console.getCpu().getFlag(Flags.V)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("V", x  + 100, y);
        if (console.getCpu().getFlag(Flags.U)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("-", x  + 116, y);
        if (console.getCpu().getFlag(Flags.B)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("B", x  + 132, y);
        if (console.getCpu().getFlag(Flags.D)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("D", x  + 148, y);
        if (console.getCpu().getFlag(Flags.I)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("I", x  + 164, y);
        if (console.getCpu().getFlag(Flags.Z)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("Z", x  + 180, y);
        if (console.getCpu().getFlag(Flags.C)) g.setColor(Color.GREEN); else g.setColor(Color.RED);
        g.drawString("C", x  + 198, y);
        g.setColor(Color.WHITE);
        g.drawString("PC: $" + String.format("%02X", console.getCpu().getPc()), x , y + 19);
        g.drawString("A: $" + String.format("%02X", console.getCpu().getA()) + "[" + console.getCpu().getA() + "]", x , y + 38);
        g.drawString("X: $" + String.format("%02X", console.getCpu().getX()) + "[" + console.getCpu().getX() + "]", x , y + 57);
        g.drawString("Y: $" + String.format("%02X", console.getCpu().getY()) + "[" + console.getCpu().getY() + "]", x , y + 76);
        g.drawString("Stack P: $" + String.format("%04X", console.getCpu().getStkp()), x , y + 95);
    }

    private static void drawCode(int x, int y, int nLines, Graphics g) {
        String currentLine = codeMap.get(console.getCpu().getPc());
        Queue<String> before = new LinkedList<>();
        Queue<String> after = new LinkedList<>();
        boolean currentLineFound = false;
        for (Map.Entry<Integer, String> line : codeMap.entrySet()) {
            if (!currentLineFound) {
                if (line.getKey() == console.getCpu().getPc())
                    currentLineFound = true;
                else
                    before.offer(line.getValue());
                if (before.size() > nLines/2)
                    before.poll();
            } else {
                after.offer(line.getValue());
                if (after.size() > nLines/2)
                    break;
            }
        }
        int lineY = y;
        g.setColor(Color.WHITE);
        for (String line : before) {
            g.drawString(line, x, lineY);
            lineY += 19;
        }
        g.setColor(Color.CYAN);
        g.drawString(currentLine, x, lineY);
        lineY += 19;
        g.setColor(Color.WHITE);
        for (String line : after) {
            g.drawString(line, x, lineY);
            lineY += 19;
        }
    }
}
