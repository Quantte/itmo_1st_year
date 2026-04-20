package collection;

import model.Coordinates;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class CollectionManagerTest {
    private CollectionManager manager;

    @BeforeEach
    void setUp() { manager = new CollectionManager(); }

    @Test
    void addAssignsAutoIncrementingIds() {
        Product p1 = product("A", 1f);
        Product p2 = product("B", 2f);
        manager.add(p1);
        manager.add(p2);
        assertEquals(1, p1.getId());
        assertEquals(2, p2.getId());
    }

    @Test
    void addSetsCreationDate() {
        Product p = product("A", 1f);
        manager.add(p);
        assertNotNull(p.getCreationDate());
    }

    @Test
    void loadAllSetsNextIdFromMaxExistingId() {
        LinkedList<Product> loaded = new LinkedList<>();
        Product p = product("X", 1f);
        p.setId(5);
        loaded.add(p);
        manager.loadAll(loaded);
        Product newProduct = product("Y", 1f);
        manager.add(newProduct);
        assertEquals(6, newProduct.getId());
    }

    @Test
    void updateReplacesProductPreservingCreationDate() {
        Product original = product("A", 1f);
        manager.add(original);
        java.util.Date originalDate = original.getCreationDate();

        Product updated = product("B", 99f);
        assertTrue(manager.update(original.getId(), updated));
        assertEquals("B", manager.getAll().get(0).getName());
        assertEquals(originalDate, manager.getAll().get(0).getCreationDate());
    }

    @Test
    void updateReturnsFalseForMissingId() {
        assertFalse(manager.update(999, product("X", 1f)));
    }

    @Test
    void removeByIdReturnsTrueAndRemoves() {
        Product p = product("A", 1f);
        manager.add(p);
        assertTrue(manager.removeById(p.getId()));
        assertEquals(0, manager.size());
    }

    @Test
    void removeByIdReturnsFalseForMissingId() {
        assertFalse(manager.removeById(999));
    }

    @Test
    void clearRemovesAll() {
        manager.add(product("A", 1f));
        manager.add(product("B", 2f));
        manager.clear();
        assertEquals(0, manager.size());
    }

    @Test
    void removeFirstReturnsAndRemovesFirst() {
        Product p1 = product("A", 1f);
        Product p2 = product("B", 2f);
        manager.add(p1);
        manager.add(p2);
        var removed = manager.removeFirst();
        assertTrue(removed.isPresent());
        assertEquals(p1.getId(), removed.get().getId());
        assertEquals(1, manager.size());
    }

    @Test
    void removeFirstOnEmptyReturnsEmpty() {
        assertTrue(manager.removeFirst().isEmpty());
    }

    @Test
    void getMaxReturnsLargestByNaturalOrder() {
        Product a = product("Apple", 10f);
        Product b = product("Banana", 5f);
        manager.add(a);
        manager.add(b);
        var max = manager.getMax();
        assertTrue(max.isPresent());
        assertEquals("Banana", max.get().getName());
    }

    @Test
    void removeAnyByPriceRemovesOneMatch() {
        manager.add(product("A", 10f));
        manager.add(product("B", 10f));
        assertTrue(manager.removeAnyByPrice(10f));
        assertEquals(1, manager.size());
    }

    @Test
    void removeAnyByPriceReturnsFalseIfNoMatch() {
        manager.add(product("A", 5f));
        assertFalse(manager.removeAnyByPrice(99f));
    }

    @Test
    void averageOfPriceComputesCorrectly() {
        manager.add(product("A", 10f));
        manager.add(product("B", 20f));
        assertEquals(15.0, manager.averageOfPrice(), 0.001);
    }

    @Test
    void averageOfPriceReturnsZeroForEmptyCollection() {
        assertEquals(0.0, manager.averageOfPrice(), 0.001);
    }

    @Test
    void getDescendingReturnsSortedDescending() {
        manager.add(product("Apple", 10f));
        manager.add(product("Banana", 5f));
        manager.add(product("Apple", 5f));
        var desc = manager.getDescending();
        assertEquals("Banana", desc.get(0).getName());
        assertEquals(10f, desc.get(1).getPrice());
        assertEquals(5f, desc.get(2).getPrice());
    }

    private Product product(String name, float price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setCoordinates(new Coordinates(1f, 1L));
        return p;
    }
}
