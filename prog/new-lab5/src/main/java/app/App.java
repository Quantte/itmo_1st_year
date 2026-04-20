// src/main/java/app/App.java
package app;

import commands.CommandRegistry;
import commands.ExitException;
import commands.ExecutionContext;
import commands.impl.*;

import java.io.IOException;

/**
 * Wires all commands into a registry and runs the REPL loop.
 * The REPL reads one line at a time, parses the command name and arguments,
 * and dispatches to the registered command.
 */
public class App {
    private final ExecutionContext ctx;
    private final CommandRegistry registry;

    /**
     * @param ctx execution context (must have manager, storage, reader, writer set)
     */
    public App(ExecutionContext ctx) {
        this.ctx = ctx;
        this.registry = new CommandRegistry();
        registerCommands();
        ctx.setRegistry(registry);
    }

    private void registerCommands() {
        registry.register(new HelpCommand());
        registry.register(new InfoCommand());
        registry.register(new ShowCommand());
        registry.register(new AddCommand());
        registry.register(new UpdateCommand());
        registry.register(new RemoveByIdCommand());
        registry.register(new ClearCommand());
        registry.register(new SaveCommand());
        registry.register(new ExecuteScriptCommand());
        registry.register(new ExitCommand());
        registry.register(new RemoveFirstCommand());
        registry.register(new AddIfMaxCommand());
        registry.register(new HistoryCommand());
        registry.register(new RemoveAnyByPriceCommand());
        registry.register(new AverageOfPriceCommand());
        registry.register(new PrintDescendingCommand());
    }

    /**
     * Runs the interactive REPL loop until EOF or exit command.
     *
     * @throws IOException on read error
     */
    public void run() throws IOException {
        ctx.getWriter().println("Product Manager started. Type 'help' for available commands.");
        while (true) {
            if (ctx.getReader().isInteractive()) {
                ctx.getWriter().prompt("> ");
            }
            String line = ctx.getReader().readLine();
            if (line == null) break;
            line = line.trim();
            if (line.isEmpty()) continue;

            String[] tokens = line.split("\\s+");
            String name = tokens[0];
            String[] args = new String[tokens.length - 1];
            System.arraycopy(tokens, 1, args, 0, args.length);

            try {
                registry.execute(name, args, ctx);
            } catch (ExitException e) {
                break;
            } catch (Exception e) {
                ctx.getWriter().error("Unexpected error: " + e.getMessage());
            }
        }
    }
}
