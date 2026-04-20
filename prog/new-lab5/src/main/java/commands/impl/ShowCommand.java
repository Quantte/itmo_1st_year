package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Prints all products in the collection via their toString() representation. */
public class ShowCommand implements Command {
    @Override public String getName() { return "show"; }
    @Override public String getDescription() { return "Print all elements"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        var products = ctx.getManager().getAll();
        if (products.isEmpty()) {
            ctx.getWriter().println("Collection is empty.");
            return;
        }
        products.forEach(p -> ctx.getWriter().println(p.toString()));
    }
}
