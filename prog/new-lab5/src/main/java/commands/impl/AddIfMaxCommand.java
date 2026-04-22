package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import console.ProductBuilder;
import model.Product;

import java.io.IOException;
import java.util.Optional;

/**
 * Adds a new product only if it is greater than the current maximum by natural order.
 * If the collection is empty, the product is added unconditionally.
 */
public class AddIfMaxCommand implements Command {
    @Override 
    public String getName() { return "add_if_max"; }
    
    @Override 
    public String getDescription() { return "Add product if it exceeds the current maximum"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        try {
            Product p = new ProductBuilder(ctx.getReader(), ctx.getWriter()).build();
            Optional<Product> max = ctx.getManager().getMax();
            if (max.isEmpty() || p.compareTo(max.get()) > 0) {
                ctx.getManager().add(p);
                ctx.getWriter().println("Product added with id=" + p.getId() + ".");
            } else {
                ctx.getWriter().println("Product not added: does not exceed current maximum.");
            }
        } catch (IOException e) {
            ctx.getWriter().error("Input error: " + e.getMessage());
        }
    }
}
