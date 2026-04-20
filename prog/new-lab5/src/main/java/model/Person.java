package model;

/**
 * Owner of a product.
 */
public class Person {
    /** Owner name. Not null, not empty. */
    private String name;
    /** Weight in kg. Must be greater than 0. */
    private double weight;
    /** Location of the person. May be null. */
    private Location location;

    public Person() {}

    public Person(String name, double weight, Location location) {
        this.name = name;
        this.weight = weight;
        this.location = location;
    }

    /** @return owner name */
    public String getName() { return name; }
    /** @param name owner name, not null and not empty */
    public void setName(String name) { this.name = name; }
    /** @return weight */
    public double getWeight() { return weight; }
    /** @param weight weight, must be > 0 */
    public void setWeight(double weight) { this.weight = weight; }
    /** @return location, nullable */
    public Location getLocation() { return location; }
    /** @param location location, nullable */
    public void setLocation(Location location) { this.location = location; }

    @Override
    public String toString() {
        return "Person{name='" + name + "', weight=" + weight + ", location=" + location + "}";
    }
}
