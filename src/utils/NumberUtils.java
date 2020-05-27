package utils;

public class NumberUtils {

    public static int byteFlip(int val) {
        int tmp = 0x00;
        for (int i = 0; i < 8; i++) {
            if ((val & (0x1 << i)) != 0)
                tmp |= 0x80 >> i;
        }
        return tmp;
    }

    public static float map(float val, float oldRangeS, float oldRangeE, float start, float end) {
        return start + ((oldRangeE - val) / (oldRangeE - oldRangeS)) * (end - start);
    }
}
