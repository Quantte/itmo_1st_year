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
