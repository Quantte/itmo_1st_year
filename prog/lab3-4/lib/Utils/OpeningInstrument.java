package lib.Utils;

import lib.Objects.Bottle;
import lib.Objects.Door;

public interface OpeningInstrument {
    void open(Bottle bottle) throws CannotOpenBottleException;
    void open(Door door);
}
