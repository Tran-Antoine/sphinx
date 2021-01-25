package net.starype.quiz.api.parser;

import net.starype.quiz.api.util.CheckSum;
import net.starype.quiz.api.util.SerializedArgument;
import net.starype.quiz.api.util.Serializer;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Stores an entry for each corresponding argument defined in the {@link DBTable}
 *
 */
public class DBEntry extends Serializer {
    private final DBTable table;
    private Set<ArgumentValue<String>> indexedArguments;
    private Map<String, ByteBuffer> data;

    /**
     * Constructor of the {@link DBEntry}
     * @param table the table used to create the argument & value hold by the entry
     */
    public DBEntry(DBTable table) {
        super(Stream.concat(table.getArguments()
                .stream(), Stream.of("checksum", "file"))
                .distinct()
                .map(SerializedArgument::new)
                .collect(Collectors.toList()));
        this.table = table;
        data = new HashMap<>();
        indexedArguments = new HashSet<>();
        table.getIndexedArguments().forEach(arg -> indexedArguments.add(new ArgumentValue(arg, "")));
    }

    /**
     * Get the checksum argument of the entry
     * @return The checksum hold by the current entry (see {@link CheckSum})
     */
    public CheckSum checkSum() {
        return CheckSum.fromRawCheckSum(data.get("checksum"));
    }

    /**
     * Set the checksum of the given entry
     * @param checkSum the new checkSum value (see {@link CheckSum})
     */
    public void setCheckSum(CheckSum checkSum) {
        data.put("checksum", checkSum.rawData());
        compile();
    }

    /**
     * Get the file argument of the entry
     * @return An {@link String} that hold the file from which the entry has been loaded
     */
    public String file() {
        return get("file").orElseThrow();
    }

    /**
     * Sets the file argument of the entry
     * @param file an {@link String} that hold the file from which the current entry has been loaded
     */
    public void setFile(String file) {
        set("file", file);
    }

    /**
     * Retrieve a list of all the argument hold by the current entry (see {@link ArgumentValue<String>})
     * @return A sets of {@link ArgumentValue<String>}
     */
    public Set<ArgumentValue<String>> getIndexedArguments() {
        return indexedArguments;
    }

    /**
     * Get the entry corresponding to a given name
     * @param entry the name of the argument we wanted to retrieve {@link String}
     * @return An optional {@link String} that hold the result (or empty if the argument doesn't exists in the entry)
     */
    public Optional<String> get(String entry) {
        return Optional.ofNullable(data.getOrDefault(entry, null)).map(buffer -> new String(buffer.array()));
    }

    /**
     * Set the argument of the entry to a specific value
     * @param argumentName {@link String} that hold the argument we want to change
     * @param value {@link String} the new value hold by the argument
     */
    public void set(String argumentName, String value) {
        if(!table.containsArgument(argumentName) && !argumentName.equals("file") && !argumentName.equals("checksum")) {
            return;
        }
        data.put(argumentName, ByteBuffer.wrap(value.getBytes()));
        updateIndexedArgument(argumentName, value);
        compile();
    }

    /**
     * Load an Entry from a given {@link ByteBuffer}
     * @param data hold an {@link ByteBuffer} that will be used to load the data
     * @return An Optional that contains a map of {@link String} and {@link ByteBuffer}
     */
    @Override
    public Optional<Map<String, ByteBuffer>> evaluate(ByteBuffer data) {
        Optional<Map<String, ByteBuffer>> returnData = super.evaluate(data);
        this.data = returnData.orElse(this.data);
        returnData.ifPresent(stringByteBufferMap -> stringByteBufferMap
                .forEach((entry, buffer) -> updateIndexedArgument(entry, new String(buffer.array()))));
        compile();
        return returnData;
    }

    /**
     * Load the entry from a {@link ByteBuffer}
     * @param data {@link ByteBuffer} that hold the data from which the entry is loaded
     */
    public void load(ByteBuffer data) {
        this.data = evaluate(data).orElseThrow();
        this.data.forEach((entry, buffer) -> updateIndexedArgument(entry, new String(buffer.array())));
        compile();
    }

    /**
     * Save the entry to a {@link ByteBuffer}
     * @return {@link ByteBuffer} that contains an encoded version of the entry (that can be loaded with the load methods)
     */
    public ByteBuffer save() {
        return evaluate(data).orElseThrow();
    }

    private void compile() {
        indexedArguments = new HashSet<>();
        for(String indexedArgument : table.getIndexedArguments()) {
            indexedArguments.add(new ArgumentValue<>(indexedArgument,
                    new String(data.getOrDefault(indexedArgument, ByteBuffer.allocate(1)).array())));
        }
    }

    private void updateIndexedArgument(String entry, String value) {
        indexedArguments.stream()
                .filter(argumentValue -> argumentValue.getName().equals(entry))
                .collect(Collectors.toList())
                .forEach(indexedArguments -> indexedArguments.setValue(value));
    }


}
