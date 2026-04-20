package model;

/**
 * 2D coordinates of a product location.
 */
public class Coordinates {
    /** X coordinate. Maximum value: 597. Not null. */
    private Float x;
    /** Y coordinate. Not null. */
    private Long y;

    public Coordinates() {}

    public Coordinates(Float x, Long y) {
        this.x = x;
        this.y = y;
    }

    /** @return x coordinate */
    public Float getX() { return x; }
    /** @param x x coordinate, max 597 */
    public void setX(Float x) { this.x = x; }
    /** @return y coordinate */
    public Long getY() { return y; }
    /** @param y y coordinate */
    public void setY(Long y) { this.y = y; }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }
}
