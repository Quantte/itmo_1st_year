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
    // name, x, y, price, empty partNumber, empty unit (via "> " prompt), skip owner
    private static final String PRODUCT_INPUT =
        "Widget\n12.5\n100\n99.9\n\n\n\n";

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
