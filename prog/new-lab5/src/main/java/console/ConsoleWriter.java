package console;

import java.io.PrintStream;

/**
 * Single output point for all console output.
 * Separates stdout (normal output) from stderr (errors).
 */
public class ConsoleWriter {
    private final PrintStream out;
    private final PrintStream err;

    /**
     * @param out normal output stream
     * @param err error output stream
     */
    public ConsoleWriter(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    /**
     * Prints a line to stdout.
     *
     * @param message message to print
     */
    public void println(String message) { out.println(message); }

    /**
     * Prints an error line to stderr with "Error: " prefix.
     *
     * @param message error description
     */
    public void error(String message) { err.println("Error: " + message); }

    /**
     * Prints a prompt to stdout without a trailing newline.
     * Only call this when in interactive mode.
     *
     * @param message prompt text (e.g. "Enter name: ")
     */
    public void prompt(String message) {
        out.print(message);
        out.flush();
    }
}
