package lib.Objects;

public class Door {
    private final boolean is_small;

    public Door(boolean is_small) {
        this.is_small = is_small;
    }

    public boolean get_size() {
        return this.is_small;
    }

    @Override
    public String toString() {
        return this.is_small ? "Маленькая дверь" : "Дверь";
    }
}
