package lib.Objects;

import java.util.Objects;

public class Door {
    private final boolean is_small;
    private boolean is_opened = false;
    public Door(boolean is_small) {
        this.is_small = is_small;
    }

    public boolean getSize() {
        return this.is_small;
    }

    public void openDoor() {
        this.is_opened = true;
    }

    public boolean isOpened() {
        return this.is_opened;
    }

    @Override
    public String toString() {
        return this.is_small ? "Маленькая дверь" : "Дверь";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.is_small);
    }
}
