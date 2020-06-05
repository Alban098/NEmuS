package core.apu.components;

public class LengthCounter {

    public int counter = 0x00;

    public void clock(boolean bEnable, boolean bHalt) {
        if (!bEnable)
            counter = 0;
        else {
            if (counter > 0 && !bHalt)
                counter--;
        }
    }
}
