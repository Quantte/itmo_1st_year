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
