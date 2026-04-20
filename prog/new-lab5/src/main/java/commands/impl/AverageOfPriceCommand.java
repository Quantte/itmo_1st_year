package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Prints the average price of all products in the collection. */
public class AverageOfPriceCommand implements Command {
    @Override public String getName() { return "average_of_price"; }
    @Override public String getDescription() { return "Print average price across all products"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (ctx.getManager().size() == 0) {
            ctx.getWriter().println("Collection is empty.");
            return;
        }
        ctx.getWriter().println("Average price: " + ctx.getManager().averageOfPrice());
    }
}
