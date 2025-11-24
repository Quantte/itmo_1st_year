package lib.Objects;

import java.util.Objects;

public class Door {
    private final boolean is_small;

    public Door(boolean is_small) {
        this.is_small = is_small;
    }

    public boolean getSize() {
        return this.is_small;
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
