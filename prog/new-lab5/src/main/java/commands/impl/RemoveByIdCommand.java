package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Removes the product with the given id. */
public class RemoveByIdCommand implements Command {
    @Override public String getName() { return "remove_by_id"; }
    @Override public String getDescription() { return "Remove element by id: remove_by_id <id>"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (args.length < 1) {
            ctx.getWriter().println("Usage: remove_by_id <id>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ctx.getWriter().println("Error: invalid id: '" + args[0] + "' is not an integer.");
            return;
        }
        if (!ctx.getManager().removeById(id)) {
            ctx.getWriter().println("No element with id " + id + ".");
        } else {
            ctx.getWriter().println("Element with id " + id + " removed.");
        }
    }
}
