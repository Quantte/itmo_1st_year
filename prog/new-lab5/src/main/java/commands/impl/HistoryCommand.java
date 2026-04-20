package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Prints the names of the last 9 executed commands (no arguments). */
public class HistoryCommand implements Command {
    @Override public String getName() { return "history"; }
    @Override public String getDescription() { return "Show last 9 executed command names"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        var history = ctx.getRegistry().getHistory();
        if (history.isEmpty()) {
            ctx.getWriter().println("No command history.");
            return;
        }
        history.forEach(name -> ctx.getWriter().println("  " + name));
    }
}
