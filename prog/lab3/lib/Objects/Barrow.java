package lib.Objects;

import lib.Utils.Utils.Direction;

public class Barrow {
    private double speed;

    public Barrow(double speed) {
        this.speed = speed;
    }

    public void move(Direction direction, double speed) {
        switch (direction) {
            case FRONT -> setSpeed(speed);
            case BACK -> setSpeed(-speed);
        }
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void stop() {
        this.speed = 0;
    }

    public double getSpeed() {
        return this.speed;
    }

    @Override
    public String toString() {
        return "Тачка";
    }

}
