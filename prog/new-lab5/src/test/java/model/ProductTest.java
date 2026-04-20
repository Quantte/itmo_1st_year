package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProductTest {
    @Test
    void compareToSortsByNameThenPrice() {
        Product a = makeProduct("Apple", 10f);
        Product b = makeProduct("Apple", 20f);
        Product c = makeProduct("Banana", 5f);
        assertTrue(a.compareTo(b) < 0, "same name: lower price first");
        assertTrue(b.compareTo(c) < 0, "Apple before Banana");
        assertTrue(c.compareTo(a) > 0, "Banana after Apple");
    }

    @Test
    void compareToNullReturnsPositive() {
        Product a = makeProduct("A", 1f);
        assertTrue(a.compareTo(null) > 0);
    }

    private Product makeProduct(String name, float price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setCoordinates(new Coordinates(1f, 1L));
        return p;
    }
}
