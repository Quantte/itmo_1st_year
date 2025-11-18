package lib.Objects;

public class Passage {
    private final boolean is_narrow;
    private final boolean contains_door;
    private Door contained_door = null;

    public Passage(boolean is_narrow) {
        this.is_narrow = is_narrow;
        this.contains_door = false;
    }

    public Passage(boolean is_narrow, Door door) {
        this.is_narrow = is_narrow;
        this.contains_door = true;
        this.contained_door = door;
    }

    public boolean isNarrow() {
        return this.is_narrow;
    }

    public boolean isContainsDoor() {
        return this.contains_door;
    }

    public Door get_contained_door() {
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
}
