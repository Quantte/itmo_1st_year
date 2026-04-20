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
