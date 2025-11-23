package lib.Human;

import java.util.Random;

import lib.Utils.*;
import lib.Objects.Teeth;
import lib.Objects.Bottle;


import java.util.Objects;
import java.util.ArrayList;

public class Baron extends Human {

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

    @Override
    public void see(Human human) {
        if (human instanceof Herceg) {
            System.out.println(this.name + " смотрит на Герцога");
        } else {
            System.out.println(this.name + " смотрит на " + human.name);
        }
    }

    public void stretchOutHands(Direction[] directions) {
        String message = this.name + " протягивает руки ";
        for (Direction d : directions) {
            message = message.concat(d.toString()).concat(" ");
        }
        System.out.println(message);
    }

    public void catchBottles(int num) {
        // Генерируем бутылки с разными напитками
        for (int i = 0; i < num; i++) {
            Drinks drink = Drinks.values()[new Random().nextInt(7)];
            this.bottles.add(new Bottle(drink.name()));
        }
        System.out.println(this.name + " схватил " + num + " бутылки(ок) с напитками");
    }

    public void drink() throws NoBottlesException {
        Bottle bottle = null;
        try {
            if (this.bottles.isEmpty()) {
                throw new NoBottlesException("There are no bottles left");
            }
            bottle = this.bottles.removeFirst();
            if (Math.random() > 0.5) {
                teeth.getStronger();
            }
            teeth.open(bottle);
            System.out.println(this.name + " опрокинул бутылку с " + bottle.getContent() + " себе в рот\n");
        } catch (NoBottlesException e) {
            System.out.println(this.name + ": " + e.getMessage());
        } catch (CannotOpenBottleException e) {
            System.out.println(this.name + ": " + e.getMessage());
            this.bottles.addLast(bottle);
        }
    }

    public void sitDown(String place) {
        System.out.printf("%s удобно сел на %s\n", this.name, place);
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
