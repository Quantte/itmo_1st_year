// src/main/java/app/Main.java
package app;

import collection.CollectionManager;
import commands.ExecutionContext;
import console.ConsoleReader;
import console.ConsoleWriter;
import storage.JsonFileStorage;
import storage.Storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Application entry point. Reads LAB5_DATA env var, bootstraps all components, and starts the REPL.
 */
public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * @param args unused (file path comes from LAB5_DATA env var)
     * @throws Exception on fatal startup error
     */
    public static void main(String[] args) throws Exception {
        log.info("Starting Product Manager");

        String filePath = System.getenv("LAB5_DATA");
        if (filePath == null || filePath.isBlank()) {
            log.error("LAB5_DATA environment variable is not set");
            System.err.println("Error: LAB5_DATA environment variable is not set.");
            System.exit(1);
        }
        log.debug("Data file: {}", filePath);

        Storage storage = new JsonFileStorage(filePath);
        CollectionManager manager = new CollectionManager();
        manager.loadAll(storage.load());
        log.info("Collection loaded: {} elements", manager.size());

        ConsoleWriter writer = new ConsoleWriter(System.out, System.err);
        ConsoleReader reader = new ConsoleReader(
            new InputStreamReader(System.in, StandardCharsets.UTF_8), true);
        ExecutionContext ctx = new ExecutionContext(manager, storage, reader, writer);

        log.info("Startup complete, entering REPL");
        new App(ctx).run();
        log.info("REPL exited, shutting down");
    }
}
