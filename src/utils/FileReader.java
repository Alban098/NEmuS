package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileReader {

    private int currentIndex;
    private byte[] file;

    public FileReader(String filename) {
        try {
            file = Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        currentIndex = 0;
    }

    public int nextByte() {
        return file[currentIndex++] & 0x00FF;
    }

    public int[] readBytes(int size) {
        int[] buf = new int[size];
        for (int i = 0; i < size; i++)
            buf[i] = nextByte();
        return buf;
    }
}
