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
