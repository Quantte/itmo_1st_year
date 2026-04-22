package commands.impl;

import commands.Command;
import commands.ExitException;
import commands.ExecutionContext;
import console.ConsoleReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

/**
 * Reads and executes commands from a script file, one per line.
 * Empty lines and lines starting with '#' are ignored.
 * Detects and rejects recursive script execution (direct or transitive cycles).
 */
public class ExecuteScriptCommand implements Command {
    private static final Logger log = LoggerFactory.getLogger(ExecuteScriptCommand.class);

    @Override
    public String getName() { return "execute_script"; }
    
    @Override 
    public String getDescription() { return "Execute commands from file: execute_script <filename>"; }

    @Override
    public void execute(String[] args, ExecutionContext ctx) throws ExitException {
        if (args.length < 1) {
            ctx.getWriter().println("Usage: execute_script <filename>");
            return;
        }

        String filePath;
        try {
            filePath = Path.of(args[0]).toAbsolutePath().normalize().toString();
        } catch (Exception e) {
            ctx.getWriter().error("Invalid path: " + args[0]);
            return;
        }

        if (ctx.getActiveScripts().contains(filePath)) {
            log.warn("Recursive script detected: {}", filePath);
            ctx.getWriter().error("Recursive script detected: " + filePath + ". Skipping.");
            return;
        }

        log.info("Executing script: {}", filePath);
        try (InputStreamReader isr = new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8);
             ConsoleReader scriptReader = new ConsoleReader(isr, false)) {

            ExecutionContext scriptCtx = ctx.withReader(scriptReader);
            scriptCtx.getActiveScripts().add(filePath);

            int lineNumber = 0;
            String line;
            while ((line = scriptReader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] tokens = line.split("\\s+");
                String name = tokens[0];
                String[] cmdArgs = new String[tokens.length - 1];
                System.arraycopy(tokens, 1, cmdArgs, 0, cmdArgs.length);
                log.debug("Script line {}: {} {}", lineNumber, name, String.join(" ", cmdArgs));
                ctx.getRegistry().execute(name, cmdArgs, scriptCtx);
            }
            log.info("Script finished: {} ({} lines processed)", filePath, lineNumber);
            ctx.getWriter().println("Script executed: " + args[0]);

        } catch (FileNotFoundException e) {
            log.error("Script file not found: {}", filePath);
            ctx.getWriter().error("Script file not found: " + args[0]);
        } catch (IOException e) {
            log.error("Script read error: {}", e.getMessage(), e);
            ctx.getWriter().error("Script read error: " + e.getMessage());
        }
    }
}
