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
