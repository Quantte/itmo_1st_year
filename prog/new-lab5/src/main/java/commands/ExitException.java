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
