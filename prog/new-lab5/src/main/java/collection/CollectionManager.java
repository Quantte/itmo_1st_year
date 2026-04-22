package collection;

import model.Product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Owns and manages the in-memory {@link LinkedList} of {@link Product} objects.
 * Handles ID generation (auto-incrementing, never reused), all CRUD and query operations.
 */
public class CollectionManager {
    private static final Logger log = LoggerFactory.getLogger(CollectionManager.class);

    private final LinkedList<Product> products = new LinkedList<>();
    private int nextId = 1;
    private final Date initDate = new Date();

    /**
     * Replaces the collection with loaded data and recalculates nextId.
     *
     * @param loaded products loaded from storage
     */
    public void loadAll(LinkedList<Product> loaded) {
        products.clear();
        products.addAll(loaded);
        nextId = products.stream()
            .mapToInt(Product::getId)
            .max()
            .orElse(0) + 1;
    }

    /** @return collection type name */
    public String getType() { return "LinkedList"; }

    /** @return date the collection was initialized in memory */
    public Date getInitDate() { return initDate; }

    /** @return number of elements */
    public int size() { return products.size(); }

    /** @return the underlying LinkedList (not a copy — do not modify externally) */
    public LinkedList<Product> getAll() { return products; }

    /**
     * Adds a product, assigning an auto-generated id and current date.
     *
     * @param p product to add (id and creationDate will be overwritten)
     */
    public void add(Product p) {
        p.setId(nextId++);
        p.setCreationDate(new Date());
        products.add(p);
        log.debug("Added product id={} name='{}'", p.getId(), p.getName());
    }

    /**
     * Updates the product with the given id. Preserves original creationDate.
     *
     * @param id   id of the product to replace
     * @param p    new product data (id will be overwritten)
     * @return true if found and updated, false if no element has that id
     */
    public boolean update(int id, Product p) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId() == id) {
                p.setId(id);
                p.setCreationDate(products.get(i).getCreationDate());
                products.set(i, p);
                log.debug("Updated product id={}", id);
                return true;
            }
        }
        log.warn("Update failed: no product with id={}", id);
        return false;
    }

    /**
     * Removes the product with the given id.
     *
     * @param id id to remove
     * @return true if found and removed, false otherwise
     */
    public boolean removeById(int id) {
        boolean removed = products.removeIf(p -> p.getId() == id);
        if (removed) log.debug("Removed product id={}", id);
        else log.warn("Remove failed: no product with id={}", id);
        return removed;
    }

    /** Removes all products from the collection. */
    public void clear() {
        int size = products.size();
        products.clear();
        log.info("Collection cleared ({} elements removed)", size);
    }

    /**
     * Removes and returns the first element.
     *
     * @return the removed product, or empty if collection is empty
     */
    public Optional<Product> removeFirst() {
        if (products.isEmpty()) return Optional.empty();
        return Optional.of(products.removeFirst());
    }

    /**
     * Returns the largest product by natural order.
     *
     * @return max product, or empty if collection is empty
     */
    public Optional<Product> getMax() {
        return products.stream().max(Product::compareTo);
    }

    /**
     * Removes one product whose price equals the given value.
     *
     * @param price price to match
     * @return true if a match was found and removed, false otherwise
     */
    public boolean removeAnyByPrice(float price) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getPrice() != null
                    && Float.compare(products.get(i).getPrice(), price) == 0) {
                products.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Computes average price across all products.
     *
     * @return average price, or 0 if collection is empty
     */
    public double averageOfPrice() {
        return products.stream()
            .filter(p -> p.getPrice() != null)
            .mapToDouble(Product::getPrice)
            .average()
            .orElse(0.0);
    }

    /**
     * Returns a new list with all products sorted in descending natural order.
     *
     * @return sorted copy
     */
    public LinkedList<Product> getDescending() {
        LinkedList<Product> copy = new LinkedList<>(products);
        copy.sort(Collections.reverseOrder());
        return copy;
    }
}
