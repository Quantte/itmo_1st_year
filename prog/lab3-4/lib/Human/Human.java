package lib.Human;

import lib.Utils.Direction;
import lib.Utils.Coordinates;


public abstract class Human {
    protected String name;
    protected Coordinates coordinates;

    public Human(String name, Coordinates coordinates) {
        this.name = name;
        this.coordinates = coordinates;
    }

    public abstract void move(Direction dir, double dist);
    public abstract void move(Coordinates coordinates);

    public abstract void feel(String feeling);
    public abstract void spot(Object obj);
    public abstract void spot(Object obj, Object obj1);
    public abstract void see(Human human);
}
