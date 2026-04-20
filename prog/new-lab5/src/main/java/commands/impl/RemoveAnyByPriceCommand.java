package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Removes one element whose price matches the given value. */
public class RemoveAnyByPriceCommand implements Command {
    @Override public String getName() { return "remove_any_by_price"; }
    @Override public String getDescription() { return "Remove one element by price: remove_any_by_price <price>"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (args.length < 1) {
            ctx.getWriter().println("Usage: remove_any_by_price <price>");
            return;
        }
        float price;
        try {
            price = Float.parseFloat(args[0]);
        } catch (NumberFormatException e) {
            ctx.getWriter().error("Invalid price: '" + args[0] + "' is not a number.");
            return;
        }
        if (!ctx.getManager().removeAnyByPrice(price)) {
            ctx.getWriter().println("No element with price " + price + ".");
        } else {
            ctx.getWriter().println("Element with price " + price + " removed.");
        }
    }
}
