package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import console.ProductBuilder;
import model.Product;

import java.io.IOException;

/** Replaces the product with the given id via interactive field-by-field input. */
public class UpdateCommand implements Command {
    @Override 
    public String getName() { return "update"; }
    
    @Override 
    public String getDescription() { return "Update product by id: update <id>"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (args.length < 1) {
            ctx.getWriter().println("Usage: update <id>");
            return;
        }
        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ctx.getWriter().error("Invalid id: '" + args[0] + "' is not an integer.");
            return;
        }
        try {
            Product p = new ProductBuilder(ctx.getReader(), ctx.getWriter()).build();
            if (!ctx.getManager().update(id, p)) {
                ctx.getWriter().println("No element with id " + id + ".");
            } else {
                ctx.getWriter().println("Product with id=" + id + " updated.");
            }
        } catch (IOException e) {
            ctx.getWriter().error("Input error: " + e.getMessage());
        }
    }
}
