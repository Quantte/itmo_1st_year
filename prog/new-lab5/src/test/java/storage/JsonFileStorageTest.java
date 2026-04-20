package storage;

import model.Coordinates;
import model.Person;
import model.Product;
import model.UnitOfMeasure;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;

import static org.junit.jupiter.api.Assertions.*;

class JsonFileStorageTest {
    @TempDir
    Path tempDir;

    @Test
    void saveAndLoadRoundtrip() {
        File file = tempDir.resolve("products.json").toFile();
        JsonFileStorage storage = new JsonFileStorage(file.getAbsolutePath());

        LinkedList<Product> original = new LinkedList<>();
        Product p = new Product();
        p.setId(1);
        p.setName("Widget");
        p.setPrice(99.9f);
        p.setCoordinates(new Coordinates(12.5f, 100L));
        p.setPartNumber("WDG-001");
        p.setUnitOfMeasure(UnitOfMeasure.KILOGRAMS);
        p.setOwner(new Person("Alice", 60.0, null));
        original.add(p);

        storage.save(original);
        LinkedList<Product> loaded = storage.load();

        assertEquals(1, loaded.size());
        Product loaded0 = loaded.get(0);
        assertEquals(1, loaded0.getId());
        assertEquals("Widget", loaded0.getName());
        assertEquals(99.9f, loaded0.getPrice(), 0.001f);
        assertEquals(12.5f, loaded0.getCoordinates().getX(), 0.001f);
        assertEquals(100L, loaded0.getCoordinates().getY());
        assertEquals("WDG-001", loaded0.getPartNumber());
        assertEquals(UnitOfMeasure.KILOGRAMS, loaded0.getUnitOfMeasure());
        assertNotNull(loaded0.getOwner());
        assertEquals("Alice", loaded0.getOwner().getName());
    }

    @Test
    void loadReturnsEmptyListForMissingFile() {
        JsonFileStorage storage = new JsonFileStorage("/nonexistent/path/file.json");
        LinkedList<Product> result = storage.load();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void loadReturnsEmptyListForEmptyFile() throws IOException {
        File file = tempDir.resolve("empty.json").toFile();
        file.createNewFile();
        JsonFileStorage storage = new JsonFileStorage(file.getAbsolutePath());
        LinkedList<Product> result = storage.load();
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void saveThrowsStorageExceptionForUnwritablePath() {
        JsonFileStorage storage = new JsonFileStorage("/nonexistent/dir/file.json");
        assertThrows(StorageException.class, () -> storage.save(new LinkedList<>()));
    }
}
