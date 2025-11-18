package lib.Human;

import lib.Utils.Coordinates;
import lib.Utils.Utils.Direction;
import lib.Objects.Teeth;
import lib.Objects.Bottle;
import lib.Utils.NoBottleException;
import lib.Utils.TeethCondition;

import java.util.Objects;
import java.util.ArrayList;

public class Baron extends Human {

    private TeethCondition teethCondition;
    private Teeth teeth = new Teeth(null);
    private ArrayList<Bottle> bottles = new ArrayList<>();

    public Baron(String name, Coordinates coordinates) {
        super(name, coordinates);
    }
    public Baron(String name, Coordinates coordinates, Teeth teeth, ArrayList<Bottle> bottles) {
        super(name, coordinates);
        this.teeth = teeth;
        this.bottles = bottles;
    }

    @Override
    public void move(Direction dir, double dist) {
        switch (dir) {
            case FRONT -> {
                System.out.printf("%s moved front to %.2f\n", this.name, dist);
                this.coordinates.setX(this.coordinates.get_position()[0] + dist);
            }
            case BACK -> {
                System.out.printf("%s moved back to %.2f\n", this.name, dist);
                this.coordinates.setX(this.coordinates.get_position()[0] - dist);
            }
            case LEFT -> {
                System.out.printf("%s moved left to %.2f\n", this.name, dist);
                this.coordinates.setY(this.coordinates.get_position()[1] - dist);
            }
            case RIGHT -> {
                System.out.printf("%s moved right to %.2f\n", this.name, dist);
                this.coordinates.setY(this.coordinates.get_position()[1] + dist);
            }
        }
        System.out.printf("Current position is (%.2f; %.2f)\n",
                this.coordinates.get_position()[0],
                this.coordinates.get_position()[1]);
    }

    @Override
    public void move(Coordinates coordinates) {
        System.out.printf("%s moved from (%.2f;%.2f) to (%.2f; %.2f)\n",
                this.name,
                this.coordinates.get_position()[0],
                this.coordinates.get_position()[1],
                coordinates.get_position()[0],
                coordinates.get_position()[1]);
        this.coordinates.set_position(coordinates.get_position());
    }

    @Override
    public void feel(String feeling) {
        System.out.println(feeling);
    }

    @Override
    public void spot(Object obj) {
        System.out.println(this.name + " заметил объект " + obj.toString());
    }

    @Override
    public void spot(Object obj, Object obj1) {
        System.out.println(this.name + " заметил объект "
                + obj.toString() + " в " + obj1.toString());
    }

    public void stretchOutHands(Direction[] directions) {
        String message = this.name + " протягивает руки ";
        for (Direction d : directions) {
            message = message.concat(d.toString()).concat(" ");
        }
        System.out.println(message);
    }

    public void openBottle(Bottle bottle) throws NoBottleException {
        if (this.bottles.isEmpty()) {
            throw new NoBottleException();
        }


    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.coordinates);
    }

}
