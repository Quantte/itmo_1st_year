package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Prints all products sorted in descending natural order. */
public class PrintDescendingCommand implements Command {
    @Override 
    public String getName() { return "print_descending"; }
    
    @Override 
    public String getDescription() { return "Print elements in descending order"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        var sorted = ctx.getManager().getDescending();
        if (sorted.isEmpty()) {
            ctx.getWriter().println("Collection is empty.");
            return;
        }
        sorted.forEach(p -> ctx.getWriter().println(p.toString()));
    }
}
