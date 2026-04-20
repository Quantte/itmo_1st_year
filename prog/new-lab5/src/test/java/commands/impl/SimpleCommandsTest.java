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
