package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Prints all registered commands with their descriptions. */
public class HelpCommand implements Command {
    @Override public String getName() { return "help"; }
    @Override public String getDescription() { return "Show available commands"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        ctx.getWriter().println("Available commands:");
        ctx.getRegistry().getCommands().forEach((name, cmd) ->
            ctx.getWriter().println(String.format("  %-30s - %s", name, cmd.getDescription())));
    }
}
