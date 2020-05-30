package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is used to read of file byte by byte
 * without having to keep trace of an offset in that file
 */
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

    /**
     * Read the next byte of the file
     * @return the next byte of the file
     */
    public int nextByte() {
        return file[currentIndex++] & 0x00FF;
    }

    /**
     * Read the X next bytes of the file
     * @param size the number of bytes to read
     * @return an array of int containing the bytes
     */
    public int[] readBytes(int size) {
        int[] buf = new int[size];
        for (int i = 0; i < size; i++)
            buf[i] = nextByte();
        return buf;
    }
}
