package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import console.ProductBuilder;
import model.Product;

import java.io.IOException;

/** Adds a new product to the collection via interactive field-by-field input. */
public class AddCommand implements Command {
    @Override public String getName() { return "add"; }
    @Override public String getDescription() { return "Add a new product (interactive input)"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        try {
            Product p = new ProductBuilder(ctx.getReader(), ctx.getWriter()).build();
            ctx.getManager().add(p);
            ctx.getWriter().println("Product added with id=" + p.getId() + ".");
        } catch (IOException e) {
            ctx.getWriter().error("Input error: " + e.getMessage());
        }
    }
}
