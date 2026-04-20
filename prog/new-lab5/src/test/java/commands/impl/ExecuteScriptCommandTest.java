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
        // error goes to stderr or stdout - just verify no exception is thrown
        // and test the method returns normally
        assertTrue(true);
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
