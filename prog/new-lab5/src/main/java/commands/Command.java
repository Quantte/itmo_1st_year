package commands;

/**
 * A command executable in the REPL.
 * Each implementation handles one user command (e.g., "help", "add").
 */
public interface Command {
    /**
     * @return the command name as typed by the user (e.g., "remove_by_id")
     */
    String getName();

    /**
     * @return one-line description shown by the help command
     */
    String getDescription();

    /**
     * Executes the command.
     *
     * @param args tokens after the command name (may be empty)
     * @param ctx  shared execution context
     * @throws ExitException to signal REPL termination
     */
    void execute(String[] args, ExecutionContext ctx) throws ExitException;
}
