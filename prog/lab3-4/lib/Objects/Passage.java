package lib.Objects;

import java.util.Objects;

public class Passage {
    private final boolean is_narrow;
    private final boolean contains_door;
    private Door contained_door;

    public Passage(boolean is_narrow) {
        this.is_narrow = is_narrow;
        this.contains_door = false;
    }

    public Passage(boolean is_narrow, Door door) {
        this.is_narrow = is_narrow;
        this.contains_door = true;
        this.contained_door = door;
    }

    public Door getContainedDoor() {
        if (contained_door != null) {
            return this.contained_door;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return this.is_narrow ? "узкий проход" : "проход";
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.is_narrow, this.contains_door, this.contained_door);
    }
}
