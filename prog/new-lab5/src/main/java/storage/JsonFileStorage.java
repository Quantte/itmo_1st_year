package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Product;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

/**
 * JSON file-based {@link Storage} implementation.
 * Uses {@link InputStreamReader} for reading and {@link OutputStreamWriter} for writing,
 * as required by the assignment.
 */
public class JsonFileStorage implements Storage {
    private final String filePath;
    private final Gson gson;
    private static final Type LIST_TYPE = new TypeToken<LinkedList<Product>>() {}.getType();

    /**
     * @param filePath absolute or relative path to the JSON file
     */
    public JsonFileStorage(String filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setPrettyPrinting()
            .create();
    }

    /**
     * {@inheritDoc}
     * Prints a warning to stderr on failure instead of throwing.
     */
    @Override
    public LinkedList<Product> load() {
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            LinkedList<Product> result = gson.fromJson(reader, LIST_TYPE);
            return result != null ? result : new LinkedList<>();
        } catch (FileNotFoundException e) {
            System.err.println("Warning: file not found: " + filePath + ". Starting with empty collection.");
            return new LinkedList<>();
        } catch (IOException e) {
            System.err.println("Warning: cannot read file: " + e.getMessage() + ". Starting with empty collection.");
            return new LinkedList<>();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws StorageException wrapping the underlying IOException
     */
    @Override
    public void save(LinkedList<Product> collection) {
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            gson.toJson(collection, LIST_TYPE, writer);
        } catch (IOException e) {
            throw new StorageException("Cannot save to file: " + filePath, e);
        }
    }
}
