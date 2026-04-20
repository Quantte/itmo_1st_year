package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Removes the first element of the collection. */
public class RemoveFirstCommand implements Command {
    @Override public String getName() { return "remove_first"; }
    @Override public String getDescription() { return "Remove the first element"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        ctx.getManager().removeFirst().ifPresentOrElse(
            p -> ctx.getWriter().println("Removed: " + p),
            () -> ctx.getWriter().println("Collection is empty.")
        );
    }
}
