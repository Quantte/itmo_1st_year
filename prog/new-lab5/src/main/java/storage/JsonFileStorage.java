package storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(JsonFileStorage.class);

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
        log.debug("Loading collection from {}", filePath);
        try (InputStreamReader reader = new InputStreamReader(
                new FileInputStream(filePath), StandardCharsets.UTF_8)) {
            LinkedList<Product> result = gson.fromJson(reader, LIST_TYPE);
            result = result != null ? result : new LinkedList<>();
            log.info("Loaded {} products from {}", result.size(), filePath);
            return result;
        } catch (FileNotFoundException e) {
            log.warn("File not found: {}. Starting with empty collection.", filePath);
            System.err.println("Warning: file not found: " + filePath + ". Starting with empty collection.");
            return new LinkedList<>();
        } catch (IOException e) {
            log.warn("Cannot read file: {}. Starting with empty collection.", e.getMessage());
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
        log.debug("Saving {} products to {}", collection.size(), filePath);
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(filePath), StandardCharsets.UTF_8)) {
            gson.toJson(collection, LIST_TYPE, writer);
            log.info("Saved {} products to {}", collection.size(), filePath);
        } catch (IOException e) {
            log.error("Failed to save collection to {}: {}", filePath, e.getMessage(), e);
            throw new StorageException("Cannot save to file: " + filePath, e);
        }
    }
}
