package storage;

import model.Product;

import java.util.LinkedList;

/**
 * Persistence abstraction. Swap implementations to change the storage backend.
 * Current implementation: {@link JsonFileStorage}.
 * Future implementation: PostgresStorage (same interface, no other code changes).
 */
public interface Storage {
    /**
     * Loads all products. Returns an empty list on any failure (prints a warning).
     *
     * @return loaded products, never null
     */
    LinkedList<Product> load();

    /**
     * Saves all products.
     *
     * @param collection the products to save
     * @throws StorageException if the write fails
     */
    void save(LinkedList<Product> collection);
}
