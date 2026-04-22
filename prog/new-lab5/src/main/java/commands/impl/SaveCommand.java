package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import storage.StorageException;

/** Saves the current collection to the configured file. */
public class SaveCommand implements Command {
    @Override 
    public String getName() { return "save"; }
    
    @Override 
    public String getDescription() { return "Save collection to file"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        try {
            ctx.getStorage().save(ctx.getManager().getAll());
            ctx.getWriter().println("Collection saved.");
        } catch (StorageException e) {
            ctx.getWriter().error("Save failed: " + e.getMessage());
        }
    }
}
