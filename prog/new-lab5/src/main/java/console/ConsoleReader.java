package console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Abstracted line reader. Works for both stdin (interactive) and script file input.
 * Allows {@link console.ProductBuilder} and commands to read input identically
 * regardless of source, which is the key seam for future server/client split.
 */
public class ConsoleReader implements AutoCloseable {
    private final BufferedReader reader;
    private final boolean interactive;

    /**
     * @param reader      underlying reader (stdin or file)
     * @param interactive true when reading from stdin, false when reading a script
     */
    public ConsoleReader(Reader reader, boolean interactive) {
        this.reader = new BufferedReader(reader);
        this.interactive = interactive;
    }

    /**
     * @return true if reading from stdin
     */
    public boolean isInteractive() { return interactive; }

    /**
     * Reads the next line.
     *
     * @return next line, or null on EOF
     * @throws IOException on read error
     */
    public String readLine() throws IOException {
        return reader.readLine();
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
