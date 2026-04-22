package commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps command names to {@link Command} instances and maintains an execution history.
 */
public class CommandRegistry {
    private static final Logger log = LoggerFactory.getLogger(CommandRegistry.class);

    private final Map<String, Command> commands = new LinkedHashMap<>();
    private final Deque<String> history = new ArrayDeque<>();
    private static final int MAX_HISTORY = 9;

    /**
     * Registers a command. Later registrations overwrite earlier ones for the same name.
     *
     * @param command command to register
     */
    public void register(Command command) {
        commands.put(command.getName(), command);
    }

    /**
     * @return all registered commands (insertion-ordered)
     */
    public Map<String, Command> getCommands() { return commands; }

    /**
     * @return last {@value MAX_HISTORY} executed command names (oldest first)
     */
    public Deque<String> getHistory() { return history; }

    /**
     * Looks up and executes a command, recording its name in history.
     * Prints an error if the command is unknown.
     *
     * @param name command name
     * @param args arguments
     * @param ctx  execution context
     * @throws ExitException if the executed command throws it
     */
    public void execute(String name, String[] args, ExecutionContext ctx) throws ExitException {
        Command cmd = commands.get(name);
        if (cmd == null) {
            log.warn("Unknown command: '{}'", name);
            ctx.getWriter().println("Unknown command: '" + name + "'. Type 'help' for help.");
            return;
        }
        if (history.size() == MAX_HISTORY) history.pollFirst();
        history.addLast(name);
        log.debug("Dispatching command '{}'", name);
        cmd.execute(args, ctx);
    }
}
