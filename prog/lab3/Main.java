import lib.Human.Herceg;
import lib.Human.Baron;
import lib.Objects.Door;
import lib.Objects.Passage;
import lib.Objects.Teeth;
import lib.Utils.*;
import lib.Objects.Barrow;
import lib.Objects.Bottle;

import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {
        Door small_door = new Door(true);
        Passage narrow_passage = new Passage(true, small_door);
        Barrow barrow = new Barrow("Тачка", 10);

        Herceg herceg = new Herceg("Герцог", new Coordinates(10, 10));

        Baron baron = new Baron(
                "Барон",
                new Coordinates(0, 0),
                new Teeth(new TeethCondition(Materials.STEEL, 10)),
                new ArrayList<Bottle>()
        );

        herceg.feel("Тачка катится медленнее и медленнее");
        herceg.feel("можно наконец остановиться");
        herceg.spot(narrow_passage);
        herceg.spot(small_door, narrow_passage);

        baron.sitDown("земле");
        baron.stretchOutHands(new Direction[]{Direction.LEFT, Direction.RIGHT});

        for (int i = 0; i < 5; i++) {
            if (i % 2 == 0) {
                baron.catchBottles(2);
            } else {
                baron.catchBottles(3);
                baron.drink();
            }
            baron.drink();
            baron.drink();
        }

        baron.drink();

        herceg.see(baron);
        herceg.moveIn(narrow_passage);

    }
}