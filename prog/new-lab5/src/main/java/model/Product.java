package model;

import java.util.Date;

/**
 * A product in the collection.
 * Natural ordering: by name ascending, then by price ascending.
 */
public class Product implements Comparable<Product> {
    /** Not null. Greater than 0. Unique. Auto-generated. */
    private Integer id;
    /** Not null. Not empty. */
    private String name;
    /** Not null. */
    private Coordinates coordinates;
    /** Not null. Auto-generated. */
    private Date creationDate;
    /** Not null. Greater than 0. */
    private Float price;
    /** May be null. */
    private String partNumber;
    /** May be null. */
    private UnitOfMeasure unitOfMeasure;
    /** May be null. */
    private Person owner;

    public Product() {}

    /** @return id */
    public Integer getId() { return id; }
    /** @param id product id, must be > 0 and unique */
    public void setId(Integer id) { this.id = id; }

    /** @return name */
    public String getName() { return name; }
    /** @param name product name, not null and not empty */
    public void setName(String name) { this.name = name; }

    /** @return coordinates */
    public Coordinates getCoordinates() { return coordinates; }
    /** @param coordinates product coordinates, not null */
    public void setCoordinates(Coordinates coordinates) { this.coordinates = coordinates; }

    /** @return creation date */
    public Date getCreationDate() { return creationDate; }
    /** @param creationDate auto-set on add, not null */
    public void setCreationDate(Date creationDate) { this.creationDate = creationDate; }

    /** @return price */
    public Float getPrice() { return price; }
    /** @param price product price, must be > 0, not null */
    public void setPrice(Float price) { this.price = price; }

    /** @return part number, nullable */
    public String getPartNumber() { return partNumber; }
    /** @param partNumber part number, nullable */
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    /** @return unit of measure, nullable */
    public UnitOfMeasure getUnitOfMeasure() { return unitOfMeasure; }
    /** @param unitOfMeasure unit of measure, nullable */
    public void setUnitOfMeasure(UnitOfMeasure unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    /** @return owner, nullable */
    public Person getOwner() { return owner; }
    /** @param owner product owner, nullable */
    public void setOwner(Person owner) { this.owner = owner; }

    @Override
    public int compareTo(Product other) {
        if (other == null) return 1;
        int nameCompare = this.name.compareTo(other.name);
        if (nameCompare != 0) return nameCompare;
        float thisPrice = this.price != null ? this.price : 0f;
        float otherPrice = other.price != null ? other.price : 0f;
        return Float.compare(thisPrice, otherPrice);
    }

    @Override
    public String toString() {
        return "Product{id=" + id
            + ", name='" + name + "'"
            + ", coordinates=" + coordinates
            + ", creationDate=" + creationDate
            + ", price=" + price
            + ", partNumber='" + partNumber + "'"
            + ", unitOfMeasure=" + unitOfMeasure
            + ", owner=" + owner + "}";
    }
}
