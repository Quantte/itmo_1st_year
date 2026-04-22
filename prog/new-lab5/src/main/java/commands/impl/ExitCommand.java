package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;

/** Terminates the program without saving. Throws ExitException to break the REPL loop. */
public class ExitCommand implements Command {
    @Override 
    public String getName() { return "exit"; }
    
    @Override 
    public String getDescription() { return "Exit the program (without saving)"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        ctx.getWriter().println("Bye!");
        throw new ExitException();
    }
}
