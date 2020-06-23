package utils;

import exceptions.InvalidFileException;

import java.io.EOFException;
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

    public FileReader(String filename) throws InvalidFileException {
        try {
            file = Files.readAllBytes(Paths.get(filename));
        } catch (IOException e) {
            throw new InvalidFileException("Unable to open file \"" + filename + "\"");
        }
        currentIndex = 0;
    }

    /**
     * Read the next byte of the file
     *
     * @return the next byte of the file
     * @throws EOFException If we try to read outside the file
     */
    public byte nextByte() throws EOFException {
        if (currentIndex < file.length)
            return file[currentIndex++];
        throw new EOFException("No bytes left to read");
    }

    /**
     * Read the X next bytes of the file
     *
     * @param size the number of bytes to read
     * @return an array of int containing the bytes
     * @throws EOFException If we try to read outside the file
     */
    public byte[] readBytes(int size) throws EOFException {
        byte[] buf = new byte[size];
        for (int i = 0; i < size; i++)
            buf[i] = nextByte();
        return buf;
    }
}
