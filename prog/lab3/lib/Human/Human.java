package lib.Human;

import lib.Utils.Utils.Direction;
import lib.Utils.Coordinates;


public abstract class Human {
    protected String name;
    protected Coordinates coordinates;

    public Human(String name, Coordinates coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }
    public void move(Direction dir, double dist) {

    }
    public void move(Coordinates coordinates) {

    }

    public void feel(String feeling) {

    }

    public void spot(Object obj) {

    }
    public void spot(Object obj, Object obj1) {

    }
}
