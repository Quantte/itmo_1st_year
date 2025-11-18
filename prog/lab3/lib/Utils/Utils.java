package lib.Utils;

public class Utils {
    public enum Direction {
        LEFT,
        RIGHT,
        FRONT,
        BACK,
    }

    public enum Materials {
        WOOD(1),
        IRON(5),
        STEEL(10),
        DIAMOND(100);

        private final int durability;

        Materials(int durability) {
            this.durability = durability;
        }

        public int getDurability() {
            return this.durability;
        }
    }
}

