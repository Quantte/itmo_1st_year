package console;

import model.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Interactively builds a {@link Product} by prompting the user field by field.
 * Works identically for stdin and script file input via {@link ConsoleReader}.
 * id and creationDate are NOT collected — they are set by {@link collection.CollectionManager}.
 */
public class ProductBuilder {
    private final ConsoleReader reader;
    private final ConsoleWriter writer;

    /**
     * @param reader input source
     * @param writer output sink for prompts and error messages
     */
    public ProductBuilder(ConsoleReader reader, ConsoleWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    /**
     * Reads all required fields interactively and returns a partially-built Product.
     * id and creationDate are left null — set them via CollectionManager.add().
     *
     * @return built product
     * @throws IOException on read error
     */
    public Product build() throws IOException {
        Product p = new Product();
        p.setName(readNonEmptyString("Enter name: "));
        p.setCoordinates(readCoordinates());
        p.setPrice(readPositiveFloat("Enter price: "));
        p.setPartNumber(readNullableString("Enter partNumber (empty for null): "));
        p.setUnitOfMeasure(readNullableEnum("Enter unitOfMeasure", UnitOfMeasure.class));
        p.setOwner(readNullablePerson());
        return p;
    }

    private Coordinates readCoordinates() throws IOException {
        writer.println("--- Coordinates ---");
        Float x = readFloat("Enter coordinates.x (max 597): ", f -> f <= 597f);
        Long y = readLong("Enter coordinates.y: ");
        return new Coordinates(x, y);
    }

    private Person readNullablePerson() throws IOException {
        String name = readNullableString("Enter owner name (empty to skip owner): ");
        if (name == null) return null;
        double weight = readPositiveDouble("Enter owner weight: ");
        Location location = readNullableLocation();
        return new Person(name, weight, location);
    }

    private Location readNullableLocation() throws IOException {
        String answer = readNullableString("Enter owner location? (y to include, empty to skip): ");
        if (answer == null || !answer.equalsIgnoreCase("y")) return null;
        double x = readDouble("Enter location.x: ");
        Integer y = readInt("Enter location.y: ");
        int z = readInt("Enter location.z: ");
        return new Location(x, y, z);
    }

    private String readNonEmptyString(String prompt) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            if (line == null) throw new IOException("Unexpected EOF while reading input.");
            if (!line.trim().isEmpty()) return line.trim();
            writer.println("Error: value cannot be empty.");
        }
    }

    private String readNullableString(String prompt) throws IOException {
        writer.prompt(prompt);
        String line = reader.readLine();
        if (line == null || line.trim().isEmpty()) return null;
        return line.trim();
    }

    private Float readFloat(String prompt, Predicate<Float> constraint) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            if (line == null) throw new IOException("Unexpected EOF while reading input.");
            try {
                float val = Float.parseFloat(line.trim());
                if (!constraint.test(val)) {
                    writer.println("Error: value out of allowed bounds.");
                    continue;
                }
                return val;
            } catch (NumberFormatException e) {
                writer.println("Error: expected a floating-point number.");
            }
        }
    }

    private Float readPositiveFloat(String prompt) throws IOException {
        return readFloat(prompt, f -> f > 0f);
    }

    private Long readLong(String prompt) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            if (line == null) throw new IOException("Unexpected EOF while reading input.");
            try {
                return Long.parseLong(line.trim());
            } catch (NumberFormatException e) {
                writer.println("Error: expected an integer (long).");
            }
        }
    }

    private double readDouble(String prompt) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            if (line == null) throw new IOException("Unexpected EOF while reading input.");
            try {
                return Double.parseDouble(line.trim());
            } catch (NumberFormatException e) {
                writer.println("Error: expected a floating-point number.");
            }
        }
    }

    private double readPositiveDouble(String prompt) throws IOException {
        while (true) {
            double val = readDouble(prompt);
            if (val > 0) return val;
            writer.println("Error: value must be greater than 0.");
        }
    }

    private Integer readInt(String prompt) throws IOException {
        while (true) {
            writer.prompt(prompt);
            String line = reader.readLine();
            if (line == null) throw new IOException("Unexpected EOF while reading input.");
            try {
                return Integer.parseInt(line.trim());
            } catch (NumberFormatException e) {
                writer.println("Error: expected an integer.");
            }
        }
    }

    private <T extends Enum<T>> T readNullableEnum(String prompt, Class<T> enumClass) throws IOException {
        String constants = Arrays.stream(enumClass.getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.joining(", "));
        writer.println(prompt + " [" + constants + "] (empty for null):");
        while (true) {
            writer.prompt("> ");
            String line = reader.readLine();
            if (line == null) return null;
            if (line.trim().isEmpty()) return null;
            try {
                return Enum.valueOf(enumClass, line.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                writer.println("Error: unknown value. Choose from: " + constants);
            }
        }
    }
}
