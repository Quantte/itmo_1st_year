package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Removes all products from the collection. */
public class ClearCommand implements Command {
    @Override 
    public String getName() { return "clear"; }
    
    @Override 
    public String getDescription() { return "Remove all elements from the collection"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        ctx.getManager().clear();
        ctx.getWriter().println("Collection cleared.");
    }
}
