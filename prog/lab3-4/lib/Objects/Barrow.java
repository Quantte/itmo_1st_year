package lib.Objects;

import lib.Utils.Direction;

public class Barrow {
    private final String name;
    private double speed;

    public Barrow(String name, double speed) {
        this.name = name;
        this.speed = speed;
    }

    public void move(Direction direction, double speed) {
        switch (direction) {
            case FRONT -> setSpeed(speed);
            case BACK -> setSpeed(-speed);
        }
        System.out.printf("Скорость объекта %s равна %.2f\n", this.name, getSpeed());
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void stop() {
        this.speed = 0;
        System.out.println(this.name + " остановилась");
    }

    public double getSpeed() {
        return this.speed;
    }

    @Override
    public String toString() {
        return this.name;
    }

}
