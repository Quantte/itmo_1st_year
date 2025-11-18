import lib.Human.Herceg;
import lib.Human.Baron;
import lib.Objects.Door;
import lib.Objects.Passage;
import lib.Utils.Coordinates;
import lib.Utils.Utils.Direction;

public class Main {
    public static void main(String[] args) {
        Door small_door = new Door(true);
        Passage narrow_passage = new Passage(true, small_door);

        Herceg herceg = new Herceg("Герцог", new Coordinates(10, 10));
        Baron baron = new Baron("Барон", new Coordinates(0, 0));

        herceg.move(Direction.FRONT, 20);
        System.out.println(herceg);

        herceg.move(new Coordinates(5.343, 6.432));
        herceg.feel("Тачка катится медленнее");
        herceg.spot(small_door, narrow_passage);

        System.out.println();
    }
}