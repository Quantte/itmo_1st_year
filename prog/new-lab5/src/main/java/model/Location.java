package model;

/**
 * 3D location of a person.
 */
public class Location {
    private double x;
    /** Not null. */
    private Integer y;
    private int z;

    public Location() {}

    public Location(double x, Integer y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /** @return x coordinate */
    public double getX() { return x; }
    /** @param x x coordinate */
    public void setX(double x) { this.x = x; }
    /** @return y coordinate, not null */
    public Integer getY() { return y; }
    /** @param y y coordinate */
    public void setY(Integer y) { this.y = y; }
    /** @return z coordinate */
    public int getZ() { return z; }
    /** @param z z coordinate */
    public void setZ(int z) { this.z = z; }

    @Override
    public String toString() {
        return "Location{x=" + x + ", y=" + y + ", z=" + z + "}";
    }
}
