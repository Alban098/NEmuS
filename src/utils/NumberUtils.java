package utils;

/**
 * Utility class for convenience
 */
public class NumberUtils {

    /**
     * Flip the 8 last bit of a number
     * Example (0b11010001) => (0b100001011)
     *
     * @param val the value to be flipped
     * @return the flipped value
     */
    public static int byteFlip(int val) {
        int tmp = 0x00;
        for (int i = 0; i < 8; i++) {
            if ((val & (0x1 << i)) != 0)
                tmp |= 0x80 >> i;
        }
        return tmp & 0xFF;
    }

    /**
     * Scale a value from one range to another
     * Example (10 in range (5; 15) will be mapped to 0.5 in range (0; 1)
     *
     * @param val         the value to scale (can be outside the range
     * @param rangeStart  the initial range start
     * @param rangeEnd    the initial range end
     * @param outputStart the final range start
     * @param outputEnd   the final range end
     * @return the scaled value
     */
    public static float map(float val, float rangeStart, float rangeEnd, float outputStart, float outputEnd) {
        return outputStart + ((val - rangeStart) / (rangeEnd - rangeStart)) * (outputEnd - outputStart);
    }
}
