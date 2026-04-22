package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Prints collection metadata: type, initialization date, and element count. */
public class InfoCommand implements Command {
    @Override 
    public String getName() { return "info"; }
    
    @Override 
    public String getDescription() { return "Show collection info (type, date, size)"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        var m = ctx.getManager();
        ctx.getWriter().println("Collection type : " + m.getType());
        ctx.getWriter().println("Initialized     : " + m.getInitDate());
        ctx.getWriter().println("Size            : " + m.size());
    }
}
