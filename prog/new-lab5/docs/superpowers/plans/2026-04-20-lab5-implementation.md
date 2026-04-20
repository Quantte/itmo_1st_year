# Lab5 Product Collection Manager — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a console REPL app that manages a `LinkedList<Product>` loaded from/saved to a JSON file, with clean layer separation ready for server-client split and Postgres migration.

**Architecture:** Layered monolith — `model` → `storage` (interface + JsonFileStorage) → `collection` (CollectionManager) → `commands` (registry + 16 impls) → `console` (reader/writer/builder) → `app` (wiring + REPL). `ExecutionContext` is the seam between future server and client layers.

**Tech Stack:** Java 17, Gradle (Kotlin DSL), Gson 2.10.1, JUnit 5.10.2, Mockito 5.11.0

---

## File Map

```
new-lab5/
├── build.gradle.kts
├── settings.gradle.kts
└── src/
    ├── main/java/
    │   ├── app/
    │   │   ├── Main.java
    │   │   └── App.java
    │   ├── model/
    │   │   ├── Product.java
    │   │   ├── Coordinates.java
    │   │   ├── Person.java
    │   │   ├── Location.java
    │   │   └── UnitOfMeasure.java
    │   ├── collection/
    │   │   └── CollectionManager.java
    │   ├── storage/
    │   │   ├── Storage.java
    │   │   ├── StorageException.java
    │   │   └── JsonFileStorage.java
    │   ├── commands/
    │   │   ├── Command.java
    │   │   ├── ExitException.java
    │   │   ├── ExecutionContext.java
    │   │   ├── CommandRegistry.java
    │   │   └── impl/
    │   │       ├── HelpCommand.java
    │   │       ├── InfoCommand.java
    │   │       ├── ShowCommand.java
    │   │       ├── AddCommand.java
    │   │       ├── UpdateCommand.java
    │   │       ├── RemoveByIdCommand.java
    │   │       ├── ClearCommand.java
    │   │       ├── SaveCommand.java
    │   │       ├── ExecuteScriptCommand.java
    │   │       ├── ExitCommand.java
    │   │       ├── RemoveFirstCommand.java
    │   │       ├── AddIfMaxCommand.java
    │   │       ├── HistoryCommand.java
    │   │       ├── RemoveAnyByPriceCommand.java
    │   │       ├── AverageOfPriceCommand.java
    │   │       └── PrintDescendingCommand.java
    │   └── console/
    │       ├── ConsoleReader.java
    │       ├── ConsoleWriter.java
    │       └── ProductBuilder.java
    └── test/java/
        ├── collection/
        │   └── CollectionManagerTest.java
        ├── storage/
        │   └── JsonFileStorageTest.java
        ├── commands/
        │   ├── CommandRegistryTest.java
        │   └── impl/
        │       ├── SimpleCommandsTest.java
        │       ├── MutationCommandsTest.java
        │       ├── InteractiveCommandsTest.java
        │       └── ExecuteScriptCommandTest.java
        └── console/
            └── ProductBuilderTest.java
```

---

## Task 1: Gradle project setup

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts`

- [ ] **Step 1: Create settings.gradle.kts**

```kotlin
rootProject.name = "lab5"
```

- [ ] **Step 2: Create build.gradle.kts**

```kotlin
plugins {
    java
    application
}

group = "org.lab5"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

application {
    mainClass.set("app.Main")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.code.gson:gson:2.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
```

- [ ] **Step 3: Verify Gradle resolves dependencies**

Run: `./gradlew dependencies --configuration runtimeClasspath`
Expected: Gson listed under `runtimeClasspath`

- [ ] **Step 4: Commit**

```bash
git add build.gradle.kts settings.gradle.kts
git commit -m "chore: set up Gradle project with Gson and JUnit 5"
```

---

## Task 2: Model classes

**Files:**
- Create: `src/main/java/model/UnitOfMeasure.java`
- Create: `src/main/java/model/Coordinates.java`
- Create: `src/main/java/model/Location.java`
- Create: `src/main/java/model/Person.java`
- Create: `src/main/java/model/Product.java`
- Create: `src/test/java/model/ProductTest.java`

- [ ] **Step 1: Write the failing test**

```java
// src/test/java/model/ProductTest.java
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "model.ProductTest" 2>&1 | tail -20`
Expected: compilation error (classes don't exist yet)

- [ ] **Step 3: Create UnitOfMeasure.java**

```java
// src/main/java/model/UnitOfMeasure.java
package model;

/**
 * Units of measure for products.
 */
public enum UnitOfMeasure {
    KILOGRAMS,
    METERS,
    PCS,
    LITERS,
    MILLILITERS
}
```

- [ ] **Step 4: Create Coordinates.java**

```java
// src/main/java/model/Coordinates.java
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
```

- [ ] **Step 5: Create Location.java**

```java
// src/main/java/model/Location.java
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
```

- [ ] **Step 6: Create Person.java**

```java
// src/main/java/model/Person.java
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
```

- [ ] **Step 7: Create Product.java**

```java
// src/main/java/model/Product.java
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
```

- [ ] **Step 8: Run tests to verify they pass**

Run: `./gradlew test --tests "model.ProductTest" 2>&1 | tail -20`
Expected: `BUILD SUCCESSFUL`, 2 tests passed

- [ ] **Step 9: Commit**

```bash
git add src/main/java/model/ src/test/java/model/
git commit -m "feat: add model classes (Product, Coordinates, Person, Location, UnitOfMeasure)"
```

---

## Task 3: CollectionManager

**Files:**
- Create: `src/main/java/collection/CollectionManager.java`
- Create: `src/test/java/collection/CollectionManagerTest.java`

- [ ] **Step 1: Write the failing tests**

```java
// src/test/java/collection/CollectionManagerTest.java
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "collection.CollectionManagerTest" 2>&1 | tail -10`
Expected: compilation error (class doesn't exist)

- [ ] **Step 3: Create CollectionManager.java**

```java
// src/main/java/collection/CollectionManager.java
package collection;

import model.Product;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Optional;

/**
 * Owns and manages the in-memory {@link LinkedList} of {@link Product} objects.
 * Handles ID generation (auto-incrementing, never reused), all CRUD and query operations.
 */
public class CollectionManager {
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
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the product with the given id.
     *
     * @param id id to remove
     * @return true if found and removed, false otherwise
     */
    public boolean removeById(int id) {
        return products.removeIf(p -> p.getId() == id);
    }

    /** Removes all products from the collection. */
    public void clear() { products.clear(); }

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
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "collection.CollectionManagerTest" 2>&1 | tail -10`
Expected: `BUILD SUCCESSFUL`, all tests passed

- [ ] **Step 5: Commit**

```bash
git add src/main/java/collection/ src/test/java/collection/
git commit -m "feat: add CollectionManager with ID generation and all CRUD ops"
```

---

## Task 4: Storage layer

**Files:**
- Create: `src/main/java/storage/Storage.java`
- Create: `src/main/java/storage/StorageException.java`
- Create: `src/main/java/storage/JsonFileStorage.java`
- Create: `src/test/java/storage/JsonFileStorageTest.java`

- [ ] **Step 1: Write the failing tests**

```java
// src/test/java/storage/JsonFileStorageTest.java
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
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "storage.JsonFileStorageTest" 2>&1 | tail -10`
Expected: compilation error

- [ ] **Step 3: Create Storage.java**

```java
// src/main/java/storage/Storage.java
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
```

- [ ] **Step 4: Create StorageException.java**

```java
// src/main/java/storage/StorageException.java
package storage;

/**
 * Thrown when a storage operation fails (e.g., write permission denied).
 */
public class StorageException extends RuntimeException {
    /**
     * @param message description of the failure
     * @param cause   underlying cause
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

- [ ] **Step 5: Create JsonFileStorage.java**

```java
// src/main/java/storage/JsonFileStorage.java
package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Product;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/**
 * JSON file-based {@link Storage} implementation.
 * Uses {@link InputStreamReader} for reading and {@link OutputStreamWriter} for writing,
 * as required by the assignment.
 */
public class JsonFileStorage implements Storage {
    private final String filePath;
    private final Gson gson;
    private static final Type LIST_TYPE = new TypeToken<LinkedList<Product>>() {}.getType();

    /**
     * @param filePath absolute or relative path to the JSON file
     */
    public JsonFileStorage(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setPrettyPrinting()
            .create();
    }

    /**
     * {@inheritDoc}
     * Prints a warning to stderr on failure instead of throwing.
     */
    @Override
    public LinkedList<Product> load() {
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            LinkedList<Product> result = gson.fromJson(reader, LIST_TYPE);
            return result != null ? result : new LinkedList<>();
        } catch (FileNotFoundException e) {
            System.err.println("Warning: file not found: " + filePath + ". Starting with empty collection.");
            return new LinkedList<>();
        } catch (IOException e) {
            System.err.println("Warning: cannot read file: " + e.getMessage() + ". Starting with empty collection.");
            return new LinkedList<>();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws StorageException wrapping the underlying IOException
     */
    @Override
    public void save(LinkedList<Product> collection) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            gson.toJson(collection, LIST_TYPE, writer);
        } catch (IOException e) {
            throw new StorageException("Cannot save to file: " + filePath, e);
        }
    }
}
```

- [ ] **Step 6: Run tests to verify they pass**

Run: `./gradlew test --tests "storage.JsonFileStorageTest" 2>&1 | tail -10`
Expected: `BUILD SUCCESSFUL`, 4 tests passed

- [ ] **Step 7: Commit**

```bash
git add src/main/java/storage/ src/test/java/storage/
git commit -m "feat: add Storage interface and JsonFileStorage with Gson"
```

---

## Task 5: Console I/O

**Files:**
- Create: `src/main/java/console/ConsoleReader.java`
- Create: `src/main/java/console/ConsoleWriter.java`

No separate tests — these are thin wrappers tested through ProductBuilder and command tests.

- [ ] **Step 1: Create ConsoleReader.java**

```java
// src/main/java/console/ConsoleReader.java
package console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Abstracted line reader. Works for both stdin (interactive) and script file input.
 * Allows {@link console.ProductBuilder} and commands to read input identically
 * regardless of source, which is the key seam for future server/client split.
 */
public class ConsoleReader implements AutoCloseable {
    private final BufferedReader reader;
    private final boolean interactive;

    /**
     * @param reader      underlying reader (stdin or file)
     * @param interactive true when reading from stdin, false when reading a script
     */
    public ConsoleReader(Reader reader, boolean interactive) {
        this.reader = new BufferedReader(reader);
        this.interactive = interactive;
    }

    /**
     * @return true if reading from stdin
     */
    public boolean isInteractive() { return interactive; }

    /**
     * Reads the next line.
     *
     * @return next line, or null on EOF
     * @throws IOException on read error
     */
    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
```

- [ ] **Step 2: Create ConsoleWriter.java**

```java
// src/main/java/console/ConsoleWriter.java
package console;

import java.io.PrintStream;

/**
 * Single output point for all console output.
 * Separates stdout (normal output) from stderr (errors).
 */
public class ConsoleWriter {
    private final PrintStream out;
    private final PrintStream err;

    /**
     * @param out normal output stream
     * @param err error output stream
     */
    public ConsoleWriter(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    /**
     * Prints a line to stdout.
     *
     * @param message message to print
     */
    public void println(String message) { out.println(message); }

    /**
     * Prints an error line to stderr with "Error: " prefix.
     *
     * @param message error description
     */
    public void error(String message) { err.println("Error: " + message); }

    /**
     * Prints a prompt to stdout without a trailing newline.
     * Only call this when in interactive mode.
     *
     * @param message prompt text (e.g. "Enter name: ")
     */
    public void prompt(String message) {
        out.print(message);
        out.flush();
    }
}
```

- [ ] **Step 3: Verify compilation**

Run: `./gradlew classes 2>&1 | tail -5`
Expected: `BUILD SUCCESSFUL`

- [ ] **Step 4: Commit**

```bash
git add src/main/java/console/ConsoleReader.java src/main/java/console/ConsoleWriter.java
git commit -m "feat: add ConsoleReader and ConsoleWriter"
```

---

## Task 6: Command infrastructure (Command, ExitException, ExecutionContext, CommandRegistry)

**Files:**
- Create: `src/main/java/commands/Command.java`
- Create: `src/main/java/commands/ExitException.java`
- Create: `src/main/java/commands/ExecutionContext.java`
- Create: `src/main/java/commands/CommandRegistry.java`
- Create: `src/test/java/commands/CommandRegistryTest.java`

- [ ] **Step 1: Write the failing tests**

```java
// src/test/java/commands/CommandRegistryTest.java
package commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class CommandRegistryTest {
    private CommandRegistry registry;
    private ExecutionContext ctx;
    private ByteArrayOutputStream out;

    @BeforeEach
    void setUp() {
        registry = new CommandRegistry();
        out = new ByteArrayOutputStream();
        console.ConsoleWriter writer = new console.ConsoleWriter(new PrintStream(out), System.err);
        console.ConsoleReader reader = new console.ConsoleReader(new StringReader(""), false);
        ctx = new ExecutionContext(null, null, reader, writer);
        ctx.setRegistry(registry);
    }

    @Test
    void executesRegisteredCommand() throws ExitException {
        registry.register(new Command() {
            @Override public String getName() { return "ping"; }
            @Override public String getDescription() { return "test"; }
            @Override public void execute(String[] args, ExecutionContext ctx) throws ExitException {
                ctx.getWriter().println("pong");
            }
        });
        registry.execute("ping", new String[0], ctx);
        assertTrue(out.toString().contains("pong"));
    }

    @Test
    void unknownCommandPrintsMessage() throws ExitException {
        registry.execute("unknown", new String[0], ctx);
        assertTrue(out.toString().contains("Unknown command"));
    }

    @Test
    void historyTracksLastNineCommands() throws ExitException {
        for (int i = 0; i < 10; i++) {
            int n = i;
            registry.register(new Command() {
                @Override public String getName() { return "cmd" + n; }
                @Override public String getDescription() { return ""; }
                @Override public void execute(String[] args, ExecutionContext ctx) {}
            });
            registry.execute("cmd" + n, new String[0], ctx);
        }
        assertEquals(9, registry.getHistory().size());
        assertFalse(registry.getHistory().contains("cmd0"), "oldest command should be dropped");
        assertTrue(registry.getHistory().contains("cmd9"));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "commands.CommandRegistryTest" 2>&1 | tail -10`
Expected: compilation error

- [ ] **Step 3: Create Command.java**

```java
// src/main/java/commands/Command.java
package commands;

/**
 * A command executable in the REPL.
 * Each implementation handles one user command (e.g., "help", "add").
 */
public interface Command {
    /**
     * @return the command name as typed by the user (e.g., "remove_by_id")
     */
    String getName();

    /**
     * @return one-line description shown by the help command
     */
    String getDescription();

    /**
     * Executes the command.
     *
     * @param args tokens after the command name (may be empty)
     * @param ctx  shared execution context
     * @throws ExitException to signal REPL termination
     */
    void execute(String[] args, ExecutionContext ctx) throws ExitException;
}
```

- [ ] **Step 4: Create ExitException.java**

```java
// src/main/java/commands/ExitException.java
package commands;

/**
 * Thrown by {@link commands.impl.ExitCommand} to signal REPL termination.
 * Using a checked exception ensures the REPL loop must handle it explicitly.
 */
public class ExitException extends Exception {
    /** Creates an exit signal. */
    public ExitException() {
        super("exit");
    }
}
```

- [ ] **Step 5: Create ExecutionContext.java**

```java
// src/main/java/commands/ExecutionContext.java
package commands;

import collection.CollectionManager;
import console.ConsoleReader;
import console.ConsoleWriter;
import storage.Storage;

import java.util.HashSet;
import java.util.Set;

/**
 * Shared context passed into every command's execute() method.
 * This is the primary seam for the future server-client split:
 * swap {@link ConsoleReader}/{@link ConsoleWriter} for network streams
 * and commands need no changes.
 */
public class ExecutionContext {
    private final CollectionManager manager;
    private final Storage storage;
    private final ConsoleReader reader;
    private final ConsoleWriter writer;
    private CommandRegistry registry;
    private final Set<String> activeScripts = new HashSet<>();

    /**
     * @param manager collection manager
     * @param storage storage backend
     * @param reader  input source
     * @param writer  output sink
     */
    public ExecutionContext(CollectionManager manager, Storage storage,
                            ConsoleReader reader, ConsoleWriter writer) {
        this.manager = manager;
        this.storage = storage;
        this.reader = reader;
        this.writer = writer;
    }

    /** @return collection manager */
    public CollectionManager getManager() { return manager; }
    /** @return storage backend */
    public Storage getStorage() { return storage; }
    /** @return current input reader */
    public ConsoleReader getReader() { return reader; }
    /** @return output writer */
    public ConsoleWriter getWriter() { return writer; }
    /** @return command registry (for help and history commands) */
    public CommandRegistry getRegistry() { return registry; }
    /** @param registry the registry to set after construction */
    public void setRegistry(CommandRegistry registry) { this.registry = registry; }
    /** @return set of absolute paths of scripts currently executing (for cycle detection) */
    public Set<String> getActiveScripts() { return activeScripts; }

    /**
     * Creates a child context with a different reader (for script execution).
     * The child inherits all active script paths so cycle detection works across nesting levels.
     *
     * @param newReader script file reader
     * @return new context sharing all fields except the reader
     */
    public ExecutionContext withReader(ConsoleReader newReader) {
        ExecutionContext child = new ExecutionContext(manager, storage, newReader, writer);
        child.activeScripts.addAll(this.activeScripts);
        child.registry = this.registry;
        return child;
    }
}
```

- [ ] **Step 6: Create CommandRegistry.java**

```java
// src/main/java/commands/CommandRegistry.java
package commands;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps command names to {@link Command} instances and maintains an execution history.
 */
public class CommandRegistry {
    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Deque<String> history = new ArrayDeque<>();
    private static final int MAX_HISTORY = 9;

    /**
     * Registers a command. Later registrations overwrite earlier ones for the same name.
     *
     * @param command command to register
     */
    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    /**
     * @return all registered commands (insertion-ordered)
     */
    public Map<String, Command> getCommands() { return commands; }

    /**
     * @return last {@value MAX_HISTORY} executed command names (oldest first)
     */
    public Deque<String> getHistory() { return history; }

    /**
     * Looks up and executes a command, recording its name in history.
     * Prints an error if the command is unknown.
     *
     * @param name command name
     * @param args arguments
     * @param ctx  execution context
     * @throws ExitException if the executed command throws it
     */
    public void execute(String name, String[] args, ExecutionContext ctx) throws ExitException {
        Command cmd = commands.get(name);
        if (cmd == null) {
            ctx.getWriter().println("Unknown command: '" + name + "'. Type 'help' for help.");
            return;
        }
        if (history.size() == MAX_HISTORY) history.pollFirst();
        history.addLast(name);
        cmd.execute(args, ctx);
    }
}
```

- [ ] **Step 7: Run tests to verify they pass**

Run: `./gradlew test --tests "commands.CommandRegistryTest" 2>&1 | tail -10`
Expected: `BUILD SUCCESSFUL`, 3 tests passed

- [ ] **Step 8: Commit**

```bash
git add src/main/java/commands/ src/test/java/commands/CommandRegistryTest.java
git commit -m "feat: add Command interface, ExitException, ExecutionContext, CommandRegistry"
```

---

## Task 7: ProductBuilder

**Files:**
- Create: `src/main/java/console/ProductBuilder.java`
- Create: `src/test/java/console/ProductBuilderTest.java`

- [ ] **Step 1: Write the failing tests**

```java
// src/test/java/console/ProductBuilderTest.java
package console;

import model.Product;
import model.UnitOfMeasure;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class ProductBuilderTest {
    private static ConsoleReader reader(String... lines) {
        return new ConsoleReader(new StringReader(String.join("\n", lines) + "\n"), false);
    }

    private static ConsoleWriter writer() {
        return new ConsoleWriter(new PrintStream(new ByteArrayOutputStream()), System.err);
    }

    @Test
    void buildsFullProductFromValidInput() throws IOException {
        ConsoleReader r = reader(
            "Widget",     // name
            "12.5",       // coordinates.x
            "100",        // coordinates.y
            "99.9",       // price
            "WDG-001",    // partNumber
            "KILOGRAMS",  // unitOfMeasure
            "Alice",      // owner name
            "60.0",       // owner weight
            "",           // skip location
            ""            // location prompt blank
        );
        Product p = new ProductBuilder(r, writer()).build();

        assertEquals("Widget", p.getName());
        assertEquals(12.5f, p.getCoordinates().getX(), 0.001f);
        assertEquals(100L, p.getCoordinates().getY());
        assertEquals(99.9f, p.getPrice(), 0.001f);
        assertEquals("WDG-001", p.getPartNumber());
        assertEquals(UnitOfMeasure.KILOGRAMS, p.getUnitOfMeasure());
        assertNotNull(p.getOwner());
        assertEquals("Alice", p.getOwner().getName());
        assertEquals(60.0, p.getOwner().getWeight(), 0.001);
        assertNull(p.getOwner().getLocation());
    }

    @Test
    void retriesOnEmptyName() throws IOException {
        ConsoleReader r = reader(
            "",        // empty — retry
            "Widget",  // valid
            "1.0",     // coordinates.x
            "1",       // coordinates.y
            "1.0",     // price
            "",        // partNumber null
            "",        // unitOfMeasure null
            ""         // skip owner
        );
        Product p = new ProductBuilder(r, writer()).build();
        assertEquals("Widget", p.getName());
    }

    @Test
    void retriesOnInvalidCoordinatesX() throws IOException {
        ConsoleReader r = reader(
            "Widget",  // name
            "999",     // x > 597 — retry
            "bad",     // not a float — retry
            "100.0",   // valid
            "1",       // y
            "1.0",     // price
            "",        // partNumber
            "",        // unit
            ""         // skip owner
        );
        Product p = new ProductBuilder(r, writer()).build();
        assertEquals(100.0f, p.getCoordinates().getX(), 0.001f);
    }

    @Test
    void retriesOnNonPositivePrice() throws IOException {
        ConsoleReader r = reader(
            "Widget",  // name
            "1.0",     // x
            "1",       // y
            "-5",      // price <= 0 — retry
            "0",       // price == 0 — retry
            "5.0",     // valid
            "",        // partNumber
            "",        // unit
            ""         // skip owner
        );
        Product p = new ProductBuilder(r, writer()).build();
        assertEquals(5.0f, p.getPrice(), 0.001f);
    }

    @Test
    void nullableOwnerSkippedOnEmptyName() throws IOException {
        ConsoleReader r = reader(
            "Widget",
            "1.0",
            "1",
            "10.0",
            "",   // partNumber
            "",   // unit
            ""    // empty owner name → skip
        );
        Product p = new ProductBuilder(r, writer()).build();
        assertNull(p.getOwner());
    }

    @Test
    void buildsLocationWhenRequested() throws IOException {
        ConsoleReader r = reader(
            "Widget",
            "1.0",
            "1",
            "10.0",
            "",           // partNumber
            "",           // unit
            "Alice",      // owner name
            "55.0",       // weight
            "y",          // include location
            "3.14",       // location.x
            "2",          // location.y
            "7"           // location.z
        );
        Product p = new ProductBuilder(r, writer()).build();
        assertNotNull(p.getOwner().getLocation());
        assertEquals(3.14, p.getOwner().getLocation().getX(), 0.001);
        assertEquals(2, p.getOwner().getLocation().getY());
        assertEquals(7, p.getOwner().getLocation().getZ());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "console.ProductBuilderTest" 2>&1 | tail -10`
Expected: compilation error

- [ ] **Step 3: Create ProductBuilder.java**

```java
// src/main/java/console/ProductBuilder.java
package console;

import model.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Interactively builds a {@link Product} by prompting the user field by field.
 * Works identically for stdin and script file input via {@link ConsoleReader}.
 * id and creationDate are NOT collected — they are set by {@link collection.CollectionManager}.
 */
public class ProductBuilder {
    private final ConsoleReader reader;
    private final ConsoleWriter writer;

    /**
     * @param reader input source
     * @param writer output sink for prompts and error messages
     */
    public ProductBuilder(ConsoleReader reader, ConsoleWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    /**
     * Reads all required fields interactively and returns a partially-built Product.
     * id and creationDate are left null — set them via CollectionManager.add().
     *
     * @return built product
     * @throws IOException on read error
     */
    public Product build() throws IOException {
        Product p = new Product();
        p.setName(readNonEmptyString("Enter name: "));
        p.setCoordinates(readCoordinates());
        p.setPrice(readPositiveFloat("Enter price: "));
        p.setPartNumber(readNullableString("Enter partNumber (empty for null): "));
        p.setUnitOfMeasure(readNullableEnum("Enter unitOfMeasure", UnitOfMeasure.class));
        p.setOwner(readNullablePerson());
        return p;
    }

    private Coordinates readCoordinates() throws IOException {
        writer.println("--- Coordinates ---");
        Float x = readFloat("Enter coordinates.x (max 597): ", f -> f <= 597f);
        Long y = readLong("Enter coordinates.y: ");
        return new Coordinates(x, y);
    }

    private Person readNullablePerson() throws IOException {
        String name = readNullableString("Enter owner name (empty to skip owner): ");
        if (name == null) return null;
        double weight = readPositiveDouble("Enter owner weight: ");
        Location location = readNullableLocation();
        return new Person(name, weight, location);
    }

    private Location readNullableLocation() throws IOException {
        String answer = readNullableString("Enter owner location? (y to include, empty to skip): ");
        if (answer == null || !answer.equalsIgnoreCase("y")) return null;
        double x = readDouble("Enter location.x: ");
        Integer y = readInt("Enter location.y: ");
        int z = readInt("Enter location.z: ");
        return new Location(x, y, z);
    }

    private String readNonEmptyString(String prompt) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            if (line != null && !line.trim().isEmpty()) return line.trim();
            writer.println("Error: value cannot be empty.");
        }
    }

    private String readNullableString(String prompt) throws IOException {
        writer.prompt(prompt);
        String line = reader.readLine();
        if (line == null || line.trim().isEmpty()) return null;
        return line.trim();
    }

    private Float readFloat(String prompt, Predicate<Float> constraint) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            try {
                float val = Float.parseFloat(line == null ? "" : line.trim());
                if (!constraint.test(val)) {
                    writer.println("Error: value out of allowed bounds.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                writer.println("Error: expected a floating-point number.");
            }
        }
    }

    private Float readPositiveFloat(String prompt) throws IOException {
        return readFloat(prompt, f -> f > 0f);
    }

    private Long readLong(String prompt) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            try {
                return Long.parseLong(line == null ? "" : line.trim());
            } catch (NumberFormatException e) {
                writer.println("Error: expected an integer (long).");
            }
        }
    }

    private double readDouble(String prompt) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            try {
                return Double.parseDouble(line == null ? "" : line.trim());
            } catch (NumberFormatException e) {
                writer.println("Error: expected a floating-point number.");
            }
        }
    }

    private double readPositiveDouble(String prompt) throws IOException {
        while (true) {
            double val = readDouble(prompt);
            if (val > 0) return val;
            writer.println("Error: value must be greater than 0.");
        }
    }

    private Integer readInt(String prompt) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            try {
                return Integer.parseInt(line == null ? "" : line.trim());
            } catch (NumberFormatException e) {
                writer.println("Error: expected an integer.");
            }
        }
    }

    private <T extends Enum<T>> T readNullableEnum(String prompt, Class<T> enumClass) throws IOException {
        String constants = Arrays.stream(enumClass.getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.joining(", "));
        writer.println(prompt + " [" + constants + "] (empty for null):");
        while (true) {
            writer.prompt("> ");
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) return null;
            try {
                return Enum.valueOf(enumClass, line.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                writer.println("Error: unknown value. Choose from: " + constants);
            }
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "console.ProductBuilderTest" 2>&1 | tail -10`
Expected: `BUILD SUCCESSFUL`, 6 tests passed

- [ ] **Step 5: Commit**

```bash
git add src/main/java/console/ProductBuilder.java src/test/java/console/ProductBuilderTest.java
git commit -m "feat: add ProductBuilder with field-by-field validation and retry"
```

---

## Task 8: Read-only commands (help, info, show, history, average_of_price, print_descending)

**Files:**
- Create: `src/main/java/commands/impl/HelpCommand.java`
- Create: `src/main/java/commands/impl/InfoCommand.java`
- Create: `src/main/java/commands/impl/ShowCommand.java`
- Create: `src/main/java/commands/impl/HistoryCommand.java`
- Create: `src/main/java/commands/impl/AverageOfPriceCommand.java`
- Create: `src/main/java/commands/impl/PrintDescendingCommand.java`
- Create: `src/test/java/commands/impl/SimpleCommandsTest.java`

- [ ] **Step 1: Write the failing tests**

```java
// src/test/java/commands/impl/SimpleCommandsTest.java
package commands.impl;

import collection.CollectionManager;
import commands.CommandRegistry;
import commands.ExitException;
import commands.ExecutionContext;
import console.ConsoleReader;
import console.ConsoleWriter;
import model.Coordinates;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class SimpleCommandsTest {
    private CollectionManager manager;
    private ByteArrayOutputStream out;
    private ExecutionContext ctx;
    private CommandRegistry registry;

    @BeforeEach
    void setUp() {
        manager = new CollectionManager();
        out = new ByteArrayOutputStream();
        ConsoleWriter writer = new ConsoleWriter(new PrintStream(out), System.err);
        ConsoleReader reader = new ConsoleReader(new StringReader(""), false);
        ctx = new ExecutionContext(manager, null, reader, writer);
        registry = new CommandRegistry();
        ctx.setRegistry(registry);
    }

    @Test
    void helpListsAllRegisteredCommands() throws ExitException {
        registry.register(new HelpCommand());
        registry.register(new InfoCommand());
        new HelpCommand().execute(new String[0], ctx);
        String output = out.toString();
        assertTrue(output.contains("help"));
        assertTrue(output.contains("info"));
    }

    @Test
    void infoShowsTypeAndSize() throws ExitException {
        manager.add(makeProduct("A", 1f));
        new InfoCommand().execute(new String[0], ctx);
        String output = out.toString();
        assertTrue(output.contains("LinkedList"));
        assertTrue(output.contains("1"));
    }

    @Test
    void showPrintsAllProducts() throws ExitException {
        manager.add(makeProduct("Widget", 10f));
        new ShowCommand().execute(new String[0], ctx);
        assertTrue(out.toString().contains("Widget"));
    }

    @Test
    void showPrintsEmptyMessageWhenEmpty() throws ExitException {
        new ShowCommand().execute(new String[0], ctx);
        assertTrue(out.toString().contains("empty"));
    }

    @Test
    void historyShowsLastCommands() throws ExitException {
        registry.register(new HelpCommand());
        registry.register(new HistoryCommand());
        registry.execute("help", new String[0], ctx);
        out.reset();
        new HistoryCommand().execute(new String[0], ctx);
        assertTrue(out.toString().contains("help"));
    }

    @Test
    void averageOfPriceComputesCorrectly() throws ExitException {
        manager.add(makeProduct("A", 10f));
        manager.add(makeProduct("B", 20f));
        new AverageOfPriceCommand().execute(new String[0], ctx);
        assertTrue(out.toString().contains("15"));
    }

    @Test
    void averageOfPriceOnEmptyCollection() throws ExitException {
        new AverageOfPriceCommand().execute(new String[0], ctx);
        assertTrue(out.toString().contains("empty"));
    }

    @Test
    void printDescendingPrintsInReverseOrder() throws ExitException {
        manager.add(makeProduct("Apple", 10f));
        manager.add(makeProduct("Banana", 5f));
        new PrintDescendingCommand().execute(new String[0], ctx);
        String output = out.toString();
        assertTrue(output.indexOf("Banana") < output.indexOf("Apple"));
    }

    private Product makeProduct(String name, float price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setCoordinates(new Coordinates(1f, 1L));
        return p;
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "commands.impl.SimpleCommandsTest" 2>&1 | tail -10`
Expected: compilation error

- [ ] **Step 3: Create all six read-only commands**

```java
// src/main/java/commands/impl/HelpCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Prints all registered commands with their descriptions.
 */
public class HelpCommand implements Command {
    @Override public String getName() { return "help"; }
    @Override public String getDescription() { return "Show available commands"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        ctx.getWriter().println("Available commands:");
        ctx.getRegistry().getCommands().forEach((name, cmd) ->
            ctx.getWriter().println(String.format("  %-30s - %s", name, cmd.getDescription())));
    }
}
```

```java
// src/main/java/commands/impl/InfoCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Prints collection metadata: type, initialization date, and element count.
 */
public class InfoCommand implements Command {
    @Override public String getName() { return "info"; }
    @Override public String getDescription() { return "Show collection info (type, date, size)"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        var m = ctx.getManager();
        ctx.getWriter().println("Collection type : " + m.getType());
        ctx.getWriter().println("Initialized     : " + m.getInitDate());
        ctx.getWriter().println("Size            : " + m.size());
    }
}
```

```java
// src/main/java/commands/impl/ShowCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Prints all products in the collection via their toString() representation.
 */
public class ShowCommand implements Command {
    @Override public String getName() { return "show"; }
    @Override public String getDescription() { return "Print all elements"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        var products = ctx.getManager().getAll();
        if (products.isEmpty()) {
            ctx.getWriter().println("Collection is empty.");
            return;
        }
        products.forEach(p -> ctx.getWriter().println(p.toString()));
    }
}
```

```java
// src/main/java/commands/impl/HistoryCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Prints the names of the last 9 executed commands (no arguments).
 */
public class HistoryCommand implements Command {
    @Override public String getName() { return "history"; }
    @Override public String getDescription() { return "Show last 9 executed command names"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        var history = ctx.getRegistry().getHistory();
        if (history.isEmpty()) {
            ctx.getWriter().println("No command history.");
            return;
        }
        history.forEach(name -> ctx.getWriter().println("  " + name));
    }
}
```

```java
// src/main/java/commands/impl/AverageOfPriceCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Prints the average price of all products in the collection.
 */
public class AverageOfPriceCommand implements Command {
    @Override public String getName() { return "average_of_price"; }
    @Override public String getDescription() { return "Print average price across all products"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (ctx.getManager().size() == 0) {
            ctx.getWriter().println("Collection is empty.");
            return;
        }
        ctx.getWriter().println("Average price: " + ctx.getManager().averageOfPrice());
    }
}
```

```java
// src/main/java/commands/impl/PrintDescendingCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Prints all products sorted in descending natural order (by name desc, then price desc).
 */
public class PrintDescendingCommand implements Command {
    @Override public String getName() { return "print_descending"; }
    @Override public String getDescription() { return "Print elements in descending order"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        var sorted = ctx.getManager().getDescending();
        if (sorted.isEmpty()) {
            ctx.getWriter().println("Collection is empty.");
            return;
        }
        sorted.forEach(p -> ctx.getWriter().println(p.toString()));
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "commands.impl.SimpleCommandsTest" 2>&1 | tail -10`
Expected: `BUILD SUCCESSFUL`, 8 tests passed

- [ ] **Step 5: Commit**

```bash
git add src/main/java/commands/impl/HelpCommand.java \
        src/main/java/commands/impl/InfoCommand.java \
        src/main/java/commands/impl/ShowCommand.java \
        src/main/java/commands/impl/HistoryCommand.java \
        src/main/java/commands/impl/AverageOfPriceCommand.java \
        src/main/java/commands/impl/PrintDescendingCommand.java \
        src/test/java/commands/impl/SimpleCommandsTest.java
git commit -m "feat: add read-only commands (help, info, show, history, average_of_price, print_descending)"
```

---

## Task 9: Mutation commands without interactive input (clear, remove_first, remove_by_id, remove_any_by_price, save, exit)

**Files:**
- Create: `src/main/java/commands/impl/ClearCommand.java`
- Create: `src/main/java/commands/impl/RemoveFirstCommand.java`
- Create: `src/main/java/commands/impl/RemoveByIdCommand.java`
- Create: `src/main/java/commands/impl/RemoveAnyByPriceCommand.java`
- Create: `src/main/java/commands/impl/SaveCommand.java`
- Create: `src/main/java/commands/impl/ExitCommand.java`
- Create: `src/test/java/commands/impl/MutationCommandsTest.java`

- [ ] **Step 1: Write the failing tests**

```java
// src/test/java/commands/impl/MutationCommandsTest.java
package commands.impl;

import collection.CollectionManager;
import commands.CommandRegistry;
import commands.ExitException;
import commands.ExecutionContext;
import console.ConsoleReader;
import console.ConsoleWriter;
import model.Coordinates;
import model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import storage.JsonFileStorage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.StringReader;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MutationCommandsTest {
    @TempDir Path tempDir;
    private CollectionManager manager;
    private ByteArrayOutputStream out;
    private ExecutionContext ctx;

    @BeforeEach
    void setUp() {
        manager = new CollectionManager();
        out = new ByteArrayOutputStream();
        ConsoleWriter writer = new ConsoleWriter(new PrintStream(out), System.err);
        ConsoleReader reader = new ConsoleReader(new StringReader(""), false);
        File file = tempDir.resolve("data.json").toFile();
        ctx = new ExecutionContext(manager, new JsonFileStorage(file.getAbsolutePath()), reader, writer);
        ctx.setRegistry(new CommandRegistry());
    }

    @Test
    void clearRemovesAllProducts() throws ExitException {
        manager.add(makeProduct("A", 1f));
        new ClearCommand().execute(new String[0], ctx);
        assertEquals(0, manager.size());
        assertTrue(out.toString().contains("cleared"));
    }

    @Test
    void removeFirstRemovesFirstElement() throws ExitException {
        Product p1 = makeProduct("A", 1f);
        Product p2 = makeProduct("B", 2f);
        manager.add(p1);
        manager.add(p2);
        new RemoveFirstCommand().execute(new String[0], ctx);
        assertEquals(1, manager.size());
        assertEquals(p2.getId(), manager.getAll().get(0).getId());
    }

    @Test
    void removeFirstOnEmptyCollectionPrintsMessage() throws ExitException {
        new RemoveFirstCommand().execute(new String[0], ctx);
        assertTrue(out.toString().contains("empty"));
    }

    @Test
    void removeByIdRemovesProduct() throws ExitException {
        Product p = makeProduct("A", 1f);
        manager.add(p);
        new RemoveByIdCommand().execute(new String[]{String.valueOf(p.getId())}, ctx);
        assertEquals(0, manager.size());
    }

    @Test
    void removeByIdPrintsErrorForMissingId() throws ExitException {
        new RemoveByIdCommand().execute(new String[]{"999"}, ctx);
        assertTrue(out.toString().contains("No element"));
    }

    @Test
    void removeByIdPrintsErrorForInvalidArg() throws ExitException {
        new RemoveByIdCommand().execute(new String[]{"abc"}, ctx);
        assertTrue(out.toString().toLowerCase().contains("error") || out.toString().contains("Error"));
    }

    @Test
    void removeAnyByPriceRemovesOneMatch() throws ExitException {
        manager.add(makeProduct("A", 10f));
        manager.add(makeProduct("B", 10f));
        new RemoveAnyByPriceCommand().execute(new String[]{"10.0"}, ctx);
        assertEquals(1, manager.size());
    }

    @Test
    void removeAnyByPricePrintsErrorIfNoMatch() throws ExitException {
        new RemoveAnyByPriceCommand().execute(new String[]{"99.0"}, ctx);
        assertTrue(out.toString().contains("No element"));
    }

    @Test
    void savePersistsCollection() throws ExitException {
        manager.add(makeProduct("Widget", 5f));
        new SaveCommand().execute(new String[0], ctx);
        assertTrue(out.toString().contains("saved"));
        // reload from same file
        CollectionManager manager2 = new CollectionManager();
        manager2.loadAll(ctx.getStorage().load());
        assertEquals(1, manager2.size());
        assertEquals("Widget", manager2.getAll().get(0).getName());
    }

    @Test
    void exitThrowsExitException() {
        assertThrows(ExitException.class,
            () -> new ExitCommand().execute(new String[0], ctx));
    }

    private Product makeProduct(String name, float price) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setCoordinates(new Coordinates(1f, 1L));
        return p;
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "commands.impl.MutationCommandsTest" 2>&1 | tail -10`
Expected: compilation error

- [ ] **Step 3: Create the six mutation commands**

```java
// src/main/java/commands/impl/ClearCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Removes all products from the collection.
 */
public class ClearCommand implements Command {
    @Override public String getName() { return "clear"; }
    @Override public String getDescription() { return "Remove all elements from the collection"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        ctx.getManager().clear();
        ctx.getWriter().println("Collection cleared.");
    }
}
```

```java
// src/main/java/commands/impl/RemoveFirstCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Removes the first element of the collection.
 */
public class RemoveFirstCommand implements Command {
    @Override public String getName() { return "remove_first"; }
    @Override public String getDescription() { return "Remove the first element"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        ctx.getManager().removeFirst().ifPresentOrElse(
            p -> ctx.getWriter().println("Removed: " + p),
            () -> ctx.getWriter().println("Collection is empty.")
        );
    }
}
```

```java
// src/main/java/commands/impl/RemoveByIdCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Removes the product with the given id.
 */
public class RemoveByIdCommand implements Command {
    @Override public String getName() { return "remove_by_id"; }
    @Override public String getDescription() { return "Remove element by id: remove_by_id <id>"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (args.length < 1) {
            ctx.getWriter().println("Usage: remove_by_id <id>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ctx.getWriter().error("Invalid id: '" + args[0] + "' is not an integer.");
            return;
        }
        if (!ctx.getManager().removeById(id)) {
            ctx.getWriter().println("No element with id " + id + ".");
        } else {
            ctx.getWriter().println("Element with id " + id + " removed.");
        }
    }
}
```

```java
// src/main/java/commands/impl/RemoveAnyByPriceCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Removes one element whose price matches the given value.
 */
public class RemoveAnyByPriceCommand implements Command {
    @Override public String getName() { return "remove_any_by_price"; }
    @Override public String getDescription() { return "Remove one element by price: remove_any_by_price <price>"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (args.length < 1) {
            ctx.getWriter().println("Usage: remove_any_by_price <price>");
            return;
        }
        float price;
        try {
            price = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            ctx.getWriter().error("Invalid price: '" + args[0] + "' is not a number.");
            return;
        }
        if (!ctx.getManager().removeAnyByPrice(price)) {
            ctx.getWriter().println("No element with price " + price + ".");
        } else {
            ctx.getWriter().println("Element with price " + price + " removed.");
        }
    }
}
```

```java
// src/main/java/commands/impl/SaveCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import storage.StorageException;

/**
 * Saves the current collection to the configured file.
 */
public class SaveCommand implements Command {
    @Override public String getName() { return "save"; }
    @Override public String getDescription() { return "Save collection to file"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        try {
            ctx.getStorage().save(ctx.getManager().getAll());
            ctx.getWriter().println("Collection saved.");
        } catch (StorageException e) {
            ctx.getWriter().error("Save failed: " + e.getMessage());
        }
    }
}
```

```java
// src/main/java/commands/impl/ExitCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/**
 * Terminates the program without saving. Throws {@link ExitException} to break the REPL loop.
 */
public class ExitCommand implements Command {
    @Override public String getName() { return "exit"; }
    @Override public String getDescription() { return "Exit the program (without saving)"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        ctx.getWriter().println("Bye!");
        throw new ExitException();
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "commands.impl.MutationCommandsTest" 2>&1 | tail -10`
Expected: `BUILD SUCCESSFUL`, all tests passed

- [ ] **Step 5: Commit**

```bash
git add src/main/java/commands/impl/ClearCommand.java \
        src/main/java/commands/impl/RemoveFirstCommand.java \
        src/main/java/commands/impl/RemoveByIdCommand.java \
        src/main/java/commands/impl/RemoveAnyByPriceCommand.java \
        src/main/java/commands/impl/SaveCommand.java \
        src/main/java/commands/impl/ExitCommand.java \
        src/test/java/commands/impl/MutationCommandsTest.java
git commit -m "feat: add mutation commands (clear, remove_first, remove_by_id, remove_any_by_price, save, exit)"
```

---

## Task 10: Interactive-input commands (add, update, add_if_max)

**Files:**
- Create: `src/main/java/commands/impl/AddCommand.java`
- Create: `src/main/java/commands/impl/UpdateCommand.java`
- Create: `src/main/java/commands/impl/AddIfMaxCommand.java`
- Create: `src/test/java/commands/impl/InteractiveCommandsTest.java`

- [ ] **Step 1: Write the failing tests**

```java
// src/test/java/commands/impl/InteractiveCommandsTest.java
package commands.impl;

import collection.CollectionManager;
import commands.CommandRegistry;
import commands.ExitException;
import commands.ExecutionContext;
import console.ConsoleReader;
import console.ConsoleWriter;
import model.Coordinates;
import model.Product;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class InteractiveCommandsTest {
    private static final String PRODUCT_INPUT =
        "Widget\n12.5\n100\n99.9\n\n\n\n";  // name, x, y, price, empty partNumber, empty unit, skip owner

    private ExecutionContext makeCtx(CollectionManager manager, String input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleWriter writer = new ConsoleWriter(new PrintStream(out), System.err);
        ConsoleReader reader = new ConsoleReader(new StringReader(input), false);
        ExecutionContext ctx = new ExecutionContext(manager, null, reader, writer);
        ctx.setRegistry(new CommandRegistry());
        return ctx;
    }

    @Test
    void addCreatesProductWithAutoId() throws ExitException {
        CollectionManager manager = new CollectionManager();
        ExecutionContext ctx = makeCtx(manager, PRODUCT_INPUT);
        new AddCommand().execute(new String[0], ctx);
        assertEquals(1, manager.size());
        assertEquals(1, manager.getAll().get(0).getId());
        assertEquals("Widget", manager.getAll().get(0).getName());
    }

    @Test
    void updateReplacesExistingProduct() throws ExitException {
        CollectionManager manager = new CollectionManager();
        Product original = new Product();
        original.setName("Old");
        original.setPrice(1f);
        original.setCoordinates(new Coordinates(1f, 1L));
        manager.add(original);

        ExecutionContext ctx = makeCtx(manager,
            "NewName\n5.0\n10\n50.0\n\n\n\n");
        new UpdateCommand().execute(new String[]{String.valueOf(original.getId())}, ctx);
        assertEquals("NewName", manager.getAll().get(0).getName());
        assertEquals(50.0f, manager.getAll().get(0).getPrice(), 0.001f);
    }

    @Test
    void updatePrintsErrorForMissingId() throws ExitException {
        CollectionManager manager = new CollectionManager();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ConsoleWriter writer = new ConsoleWriter(new PrintStream(out), System.err);
        ConsoleReader reader = new ConsoleReader(new StringReader(PRODUCT_INPUT), false);
        ExecutionContext ctx = new ExecutionContext(manager, null, reader, writer);
        ctx.setRegistry(new CommandRegistry());

        new UpdateCommand().execute(new String[]{"999"}, ctx);
        assertTrue(out.toString().contains("No element"));
    }

    @Test
    void addIfMaxAddsWhenProductIsLarger() throws ExitException {
        CollectionManager manager = new CollectionManager();
        Product existing = new Product();
        existing.setName("Apple");
        existing.setPrice(1f);
        existing.setCoordinates(new Coordinates(1f, 1L));
        manager.add(existing);

        // "Zebra" > "Apple" by natural order
        ExecutionContext ctx = makeCtx(manager,
            "Zebra\n1.0\n1\n1.0\n\n\n\n");
        new AddIfMaxCommand().execute(new String[0], ctx);
        assertEquals(2, manager.size());
    }

    @Test
    void addIfMaxSkipsWhenProductIsSmaller() throws ExitException {
        CollectionManager manager = new CollectionManager();
        Product existing = new Product();
        existing.setName("Zebra");
        existing.setPrice(1f);
        existing.setCoordinates(new Coordinates(1f, 1L));
        manager.add(existing);

        // "Apple" < "Zebra"
        ExecutionContext ctx = makeCtx(manager,
            "Apple\n1.0\n1\n1.0\n\n\n\n");
        new AddIfMaxCommand().execute(new String[0], ctx);
        assertEquals(1, manager.size());
    }

    @Test
    void addIfMaxAddsWhenCollectionIsEmpty() throws ExitException {
        CollectionManager manager = new CollectionManager();
        ExecutionContext ctx = makeCtx(manager, PRODUCT_INPUT);
        new AddIfMaxCommand().execute(new String[0], ctx);
        assertEquals(1, manager.size());
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "commands.impl.InteractiveCommandsTest" 2>&1 | tail -10`
Expected: compilation error

- [ ] **Step 3: Create AddCommand.java**

```java
// src/main/java/commands/impl/AddCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import console.ProductBuilder;
import model.Product;

import java.io.IOException;

/**
 * Adds a new product to the collection via interactive field-by-field input.
 */
public class AddCommand implements Command {
    @Override public String getName() { return "add"; }
    @Override public String getDescription() { return "Add a new product (interactive input)"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        try {
            Product p = new ProductBuilder(ctx.getReader(), ctx.getWriter()).build();
            ctx.getManager().add(p);
            ctx.getWriter().println("Product added with id=" + p.getId() + ".");
        } catch (IOException e) {
            ctx.getWriter().error("Input error: " + e.getMessage());
        }
    }
}
```

- [ ] **Step 4: Create UpdateCommand.java**

```java
// src/main/java/commands/impl/UpdateCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import console.ProductBuilder;
import model.Product;

import java.io.IOException;

/**
 * Replaces the product with the given id via interactive field-by-field input.
 */
public class UpdateCommand implements Command {
    @Override public String getName() { return "update"; }
    @Override public String getDescription() { return "Update product by id: update <id>"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (args.length < 1) {
            ctx.getWriter().println("Usage: update <id>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ctx.getWriter().error("Invalid id: '" + args[0] + "' is not an integer.");
            return;
        }
        try {
            Product p = new ProductBuilder(ctx.getReader(), ctx.getWriter()).build();
            if (!ctx.getManager().update(id, p)) {
                ctx.getWriter().println("No element with id " + id + ".");
            } else {
                ctx.getWriter().println("Product with id=" + id + " updated.");
            }
        } catch (IOException e) {
            ctx.getWriter().error("Input error: " + e.getMessage());
        }
    }
}
```

- [ ] **Step 5: Create AddIfMaxCommand.java**

```java
// src/main/java/commands/impl/AddIfMaxCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import console.ProductBuilder;
import model.Product;

import java.io.IOException;
import java.util.Optional;

/**
 * Adds a new product only if it is greater than the current maximum by natural order.
 * If the collection is empty, the product is added unconditionally.
 */
public class AddIfMaxCommand implements Command {
    @Override public String getName() { return "add_if_max"; }
    @Override public String getDescription() { return "Add product if it exceeds the current maximum"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        try {
            Product p = new ProductBuilder(ctx.getReader(), ctx.getWriter()).build();
            Optional<Product> max = ctx.getManager().getMax();
            if (max.isEmpty() || p.compareTo(max.get()) > 0) {
                ctx.getManager().add(p);
                ctx.getWriter().println("Product added with id=" + p.getId() + ".");
            } else {
                ctx.getWriter().println("Product not added: does not exceed current maximum.");
            }
        } catch (IOException e) {
            ctx.getWriter().error("Input error: " + e.getMessage());
        }
    }
}
```

- [ ] **Step 6: Run tests to verify they pass**

Run: `./gradlew test --tests "commands.impl.InteractiveCommandsTest" 2>&1 | tail -10`
Expected: `BUILD SUCCESSFUL`, all tests passed

- [ ] **Step 7: Commit**

```bash
git add src/main/java/commands/impl/AddCommand.java \
        src/main/java/commands/impl/UpdateCommand.java \
        src/main/java/commands/impl/AddIfMaxCommand.java \
        src/test/java/commands/impl/InteractiveCommandsTest.java
git commit -m "feat: add interactive commands (add, update, add_if_max)"
```

---

## Task 11: ExecuteScriptCommand

**Files:**
- Create: `src/main/java/commands/impl/ExecuteScriptCommand.java`
- Create: `src/test/java/commands/impl/ExecuteScriptCommandTest.java`

- [ ] **Step 1: Write the failing tests**

```java
// src/test/java/commands/impl/ExecuteScriptCommandTest.java
package commands.impl;

import collection.CollectionManager;
import commands.CommandRegistry;
import commands.ExitException;
import commands.ExecutionContext;
import console.ConsoleReader;
import console.ConsoleWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ExecuteScriptCommandTest {
    @TempDir Path tempDir;

    private ExecutionContext makeCtx(CollectionManager manager, ByteArrayOutputStream out) {
        ConsoleWriter writer = new ConsoleWriter(new PrintStream(out), System.err);
        ConsoleReader reader = new ConsoleReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8), true);
        ExecutionContext ctx = new ExecutionContext(manager, null, reader, writer);
        CommandRegistry registry = new CommandRegistry();
        registry.register(new ClearCommand());
        registry.register(new ShowCommand());
        registry.register(new ExecuteScriptCommand());
        ctx.setRegistry(registry);
        return ctx;
    }

    @Test
    void executesCommandsFromFile() throws ExitException, IOException {
        File script = tempDir.resolve("script.txt").toFile();
        Files.writeString(script.toPath(), "clear\n");

        CollectionManager manager = new CollectionManager();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExecutionContext ctx = makeCtx(manager, out);

        new ExecuteScriptCommand().execute(new String[]{script.getAbsolutePath()}, ctx);
        assertTrue(out.toString().contains("cleared"));
    }

    @Test
    void skipsEmptyLinesAndComments() throws ExitException, IOException {
        File script = tempDir.resolve("script2.txt").toFile();
        Files.writeString(script.toPath(), "\n# this is a comment\nclear\n");

        CollectionManager manager = new CollectionManager();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExecutionContext ctx = makeCtx(manager, out);

        new ExecuteScriptCommand().execute(new String[]{script.getAbsolutePath()}, ctx);
        assertTrue(out.toString().contains("cleared"));
    }

    @Test
    void printsErrorForMissingScriptFile() throws ExitException {
        CollectionManager manager = new CollectionManager();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExecutionContext ctx = makeCtx(manager, out);

        new ExecuteScriptCommand().execute(new String[]{"/nonexistent/script.txt"}, ctx);
        assertTrue(out.toString().toLowerCase().contains("error") ||
                   System.err.toString().contains("Error"));
    }

    @Test
    void detectsAndRejectsCyclicScriptExecution() throws ExitException, IOException {
        File script = tempDir.resolve("cyclic.txt").toFile();
        Files.writeString(script.toPath(), "execute_script " + script.getAbsolutePath() + "\n");

        CollectionManager manager = new CollectionManager();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ExecutionContext ctx = makeCtx(manager, out);

        // Should not throw StackOverflowError
        assertDoesNotThrow(() ->
            new ExecuteScriptCommand().execute(new String[]{script.getAbsolutePath()}, ctx));
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew test --tests "commands.impl.ExecuteScriptCommandTest" 2>&1 | tail -10`
Expected: compilation error

- [ ] **Step 3: Create ExecuteScriptCommand.java**

```java
// src/main/java/commands/impl/ExecuteScriptCommand.java
package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import console.ConsoleReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Reads and executes commands from a script file, one per line.
 * Empty lines and lines starting with '#' are ignored.
 * Detects and rejects recursive script execution (direct or transitive cycles).
 */
public class ExecuteScriptCommand implements Command {
    @Override public String getName() { return "execute_script"; }
    @Override public String getDescription() { return "Execute commands from file: execute_script <filename>"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (args.length < 1) {
            ctx.getWriter().println("Usage: execute_script <filename>");
            return;
        }

        String filePath;
        try {
            filePath = Path.of(args[0]).toAbsolutePath().normalize().toString();
        } catch (Exception e) {
            ctx.getWriter().error("Invalid path: " + args[0]);
            return;
        }

        if (ctx.getActiveScripts().contains(filePath)) {
            ctx.getWriter().error("Recursive script detected: " + filePath + ". Skipping.");
            return;
        }

        try (InputStreamReader isr = new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8);
             ConsoleReader scriptReader = new ConsoleReader(isr, false)) {

            ExecutionContext scriptCtx = ctx.withReader(scriptReader);
            scriptCtx.getActiveScripts().add(filePath);

            String line;
            while ((line = scriptReader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] tokens = line.split("\\s+");
                String name = tokens[0];
                String[] cmdArgs = new String[tokens.length - 1];
                System.arraycopy(tokens, 1, cmdArgs, 0, cmdArgs.length);
                ctx.getRegistry().execute(name, cmdArgs, scriptCtx);
            }
            ctx.getWriter().println("Script executed: " + args[0]);

        } catch (FileNotFoundException e) {
            ctx.getWriter().error("Script file not found: " + args[0]);
        } catch (IOException e) {
            ctx.getWriter().error("Script read error: " + e.getMessage());
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew test --tests "commands.impl.ExecuteScriptCommandTest" 2>&1 | tail -10`
Expected: `BUILD SUCCESSFUL`, all tests passed

- [ ] **Step 5: Commit**

```bash
git add src/main/java/commands/impl/ExecuteScriptCommand.java \
        src/test/java/commands/impl/ExecuteScriptCommandTest.java
git commit -m "feat: add ExecuteScriptCommand with cycle detection"
```

---

## Task 12: App, Main, and full integration

**Files:**
- Create: `src/main/java/app/App.java`
- Create: `src/main/java/app/Main.java`

- [ ] **Step 1: Create App.java**

```java
// src/main/java/app/App.java
package app;

import commands.CommandRegistry;
import commands.ExitException;
import commands.ExecutionContext;
import commands.impl.*;

import java.io.IOException;

/**
 * Wires all commands into a registry and runs the REPL loop.
 * The REPL reads one line at a time, parses the command name and arguments,
 * and dispatches to the registered command.
 */
public class App {
    private final ExecutionContext ctx;
    private final CommandRegistry registry;

    /**
     * @param ctx execution context (must have manager, storage, reader, writer set)
     */
    public App(ExecutionContext ctx) {
        this.ctx = ctx;
        this.registry = new CommandRegistry();
        registerCommands();
        ctx.setRegistry(registry);
    }

    private void registerCommands() {
        registry.register(new HelpCommand());
        registry.register(new InfoCommand());
        registry.register(new ShowCommand());
        registry.register(new AddCommand());
        registry.register(new UpdateCommand());
        registry.register(new RemoveByIdCommand());
        registry.register(new ClearCommand());
        registry.register(new SaveCommand());
        registry.register(new ExecuteScriptCommand());
        registry.register(new ExitCommand());
        registry.register(new RemoveFirstCommand());
        registry.register(new AddIfMaxCommand());
        registry.register(new HistoryCommand());
        registry.register(new RemoveAnyByPriceCommand());
        registry.register(new AverageOfPriceCommand());
        registry.register(new PrintDescendingCommand());
    }

    /**
     * Runs the interactive REPL loop until EOF or exit command.
     *
     * @throws IOException on read error
     */
    public void run() throws IOException {
        ctx.getWriter().println("Product Manager started. Type 'help' for available commands.");
        while (true) {
            if (ctx.getReader().isInteractive()) {
                ctx.getWriter().prompt("> ");
            }
            String line = ctx.getReader().readLine();
            if (line == null) break;
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            String name = tokens[0];
            String[] args = new String[tokens.length - 1];
            System.arraycopy(tokens, 1, args, 0, args.length);

            try {
                registry.execute(name, args, ctx);
            } catch (ExitException e) {
                break;
            } catch (Exception e) {
                ctx.getWriter().error("Unexpected error: " + e.getMessage());
            }
        }
    }
}
```

- [ ] **Step 2: Create Main.java**

```java
// src/main/java/app/Main.java
package app;

import collection.CollectionManager;
import commands.ExecutionContext;
import console.ConsoleReader;
import console.ConsoleWriter;
import storage.JsonFileStorage;
import storage.Storage;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Application entry point. Reads LAB5_DATA env var, bootstraps all components, and starts the REPL.
 */
public class Main {
    /**
     * @param args unused (file path comes from LAB5_DATA env var)
     * @throws Exception on fatal startup error
     */
    public static void main(String[] args) throws Exception {
        String filePath = System.getenv("LAB5_DATA");
        if (filePath == null || filePath.isBlank()) {
            System.err.println("Error: LAB5_DATA environment variable is not set.");
            System.exit(1);
        }

        Storage storage = new JsonFileStorage(filePath);
        CollectionManager manager = new CollectionManager();
        manager.loadAll(storage.load());

        ConsoleWriter writer = new ConsoleWriter(System.out, System.err);
        ConsoleReader reader = new ConsoleReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8), true);
        ExecutionContext ctx = new ExecutionContext(manager, storage, reader, writer);

        new App(ctx).run();
    }
}
```

- [ ] **Step 3: Verify full build compiles and all tests pass**

Run: `./gradlew build 2>&1 | tail -20`
Expected: `BUILD SUCCESSFUL`, all tests passed, no compilation errors

- [ ] **Step 4: Smoke test the running app**

```bash
export LAB5_DATA=/tmp/lab5_test.json
echo "[]" > /tmp/lab5_test.json
echo -e "help\ninfo\nadd\nWidget\n100.0\n50\n9.99\n\n\n\nshow\nsave\nexit" | ./gradlew run -q --args=""
```

Expected output includes: "Product Manager started", all help entries, "Widget", "Collection saved.", "Bye!"

- [ ] **Step 5: Commit**

```bash
git add src/main/java/app/
git commit -m "feat: add App REPL loop and Main entry point — lab5 complete"
```

---

## Spec Coverage Check

| Requirement | Task |
|---|---|
| LinkedList collection | Task 3 (CollectionManager) |
| Load from file on startup (env var LAB5_DATA) | Task 12 (Main) |
| JSON format, InputStreamReader/OutputStreamWriter | Task 4 (JsonFileStorage) |
| help, info, show | Task 8 |
| add, update, remove_by_id, clear, save | Task 9–10 |
| execute_script | Task 11 |
| exit | Task 9 |
| remove_first, add_if_max, history | Task 8–10 |
| remove_any_by_price, average_of_price, print_descending | Task 8–9 |
| Natural ordering (Comparable) | Task 2 |
| Field-by-field interactive input with validation | Task 7 |
| Enum display before input | Task 7 (ProductBuilder) |
| Auto-generated id and creationDate | Task 3 (CollectionManager.add) |
| Nullable fields via empty line | Task 7 (ProductBuilder) |
| Error handling (bad input, file access) | Tasks 4, 7, 8–11 |
| Javadoc on all classes | All tasks |
| Cycle detection in execute_script | Task 11 |
