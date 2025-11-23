package lib.Human;

import lib.Objects.Passage;
import lib.Utils.Direction;
import lib.Utils.Coordinates;

import java.util.Objects;

public class Herceg extends Human{
    public Herceg(String name, Coordinates coordinates) {
        super(name, coordinates);
    }

    @Override
    public void move(Direction dir, double dist) {
        switch (dir) {
            case FRONT -> {
                System.out.printf("%s moved front to %.2f\t", this.name, dist);
                this.coordinates.setX(this.coordinates.get_position()[0] + dist);
            }
            case BACK -> {
                System.out.printf("%s moved back to %.2f\t", this.name, dist);
                this.coordinates.setX(this.coordinates.get_position()[0] - dist);

            }
            case LEFT -> {
                System.out.printf("%s moved left to %.2f\t", this.name, dist);
                this.coordinates.setY(this.coordinates.get_position()[1] - dist);
            }
            case RIGHT -> {
                System.out.printf("%s moved right to %.2f\t", this.name, dist);
                this.coordinates.setY(this.coordinates.get_position()[1] + dist);
            }
        }
        System.out.printf("Current position is (%.2f; %.2f)\n",
                this.coordinates.get_position()[0], this.coordinates.get_position()[1]);
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
        System.out.println(this.name + " почувствовал, что " + feeling);
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

    @Override
    public void see(Human human) {
        if (human instanceof Baron) {
            System.out.println(this.name + " смотрит на Барона");
        } else {
            System.out.println(this.name + " смотрит на " + human.name);
        }
    }

    public void move_in(Passage passage) {
        System.out.println(this.name + " углубился в " + passage);
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
