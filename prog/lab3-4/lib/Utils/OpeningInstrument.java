package lib.Utils;

import lib.Objects.Bottle;

public interface OpeningInstrument {
    void open(Bottle bottle) throws CannotOpenBottleException;
}
