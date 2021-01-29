package net.starype.quiz.api.database;

import net.starype.quiz.api.util.ByteBufferUtils;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Stores an entry for each corresponding argument defined in the {@link DatabaseTable}
 */
public class DatabaseEntry {

    private final DatabaseId id;
    private final Map<String,String> argumentMap = new TreeMap<>();
    private final List<String> indexedArguments = new ArrayList<>();
    private final DatabaseTable table;
    private final Consumer<DatabaseId> onChange;

    public static ByteBuffer serialize(DatabaseEntry entry) {
        ByteBuffer rawData = Serializer.serialize(entry.orderedArgumentValues(), s -> ByteBuffer.wrap(s.getBytes()));
        ByteBuffer idBuffer = DatabaseId.serialize(entry.id);
        return ByteBufferUtils.concat(idBuffer, rawData);
    }

    private Collection<String> orderedArgumentValues() {
        return argumentMap.keySet()
                .stream()
                .map(argumentMap::get)
                .collect(Collectors.toList());
    }

    public static DatabaseEntry deserialize(ByteBuffer data, DatabaseTable table, DatabaseIdGenerator idGenerator,
                                            Consumer<DatabaseId> onChange) {
        // Retrieve the idBuffer and the rawData buffer
        ByteBuffer idBuffer = ByteBufferUtils.extractSubBuffer(data, DatabaseId.getSerializedSize());
        ByteBuffer rawData = ByteBufferUtils.extractSubBuffer(data, data.array().length - DatabaseId.getSerializedSize());

        // Then retrieve the id
        DatabaseId id = DatabaseId.deserialize(idBuffer);

        // Create the entry
        DatabaseEntry entry = new DatabaseEntry(table, id, onChange);

        // Deserialize each of the value contains in the database
        List<String> values = Serializer.deserialize(rawData, byteBuffer -> new String(byteBuffer.array()));
        IntStream.range(0, values.size()).forEach(i -> entry.set(table.getArguments().get(i), values.get(i)));

        // Register the new id in the generator and return the resulting entry
        idGenerator.registerNewId(id);
        return entry;
    }

    private DatabaseEntry(DatabaseTable table, DatabaseId id, Consumer<DatabaseId> onChange) {
        this.table = table;
        this.id = id;
        this.onChange = onChange;
        table.getArguments().forEach(key -> argumentMap.put(key, ""));
        indexedArguments.addAll(table.getIndexedArguments());
    }

    public DatabaseEntry(DatabaseTable table, DatabaseIdGenerator idGenerator, Consumer<DatabaseId> onChange) {
        this(table, idGenerator.generateNextId(), onChange);
    }

    public DatabaseId getId() {
        return id;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(argumentMap.getOrDefault(key, null));
    }

    public void set(String key, String value) {
        if(!table.containsArgument(key))
            return;
        argumentMap.put(key, value);
        onChange.accept(id);
    }

    public List<String> getIndexedArguments() {
        return indexedArguments;
    }
}
