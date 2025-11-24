package lib.Utils;

public final class Coordinates {
    private double x;
    private double y;

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double[] get_position() {
        return new double[]{this.x, this.y};
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }

    public void set_position(double x, double y) {
        setX(x);
        setY(y);
    }
    public void set_position(double[] coordinates) {
        setX(coordinates[0]);
        setY(coordinates[1]);
    }
}
