package net.starype.quiz.api.database;

import net.starype.quiz.api.util.ByteBufferUtils;
import net.starype.quiz.api.util.CheckSum;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SimpleDatabase implements Database {

    private final DatabaseIdGenerator idGenerator;
    private final DatabaseTable table;
    private final List<DatabaseEntry> entries;
    private final Map<DatabaseId, DatabaseEntry> entriesById;
    private final SerializedIO serializedIO;
    private boolean autoSync = false;
    private boolean internalAutoSyncLock = false;

    public SimpleDatabase(DatabaseTable table, SerializedIO serializedIO) {
        this.idGenerator = new DatabaseIdGenerator();
        this.table = table;
        this.serializedIO = serializedIO;
        entriesById = new HashMap<>();
        entries = new ArrayList<>();
    }

    protected void removeEntriesIf(Predicate<DatabaseEntry> predicate) {
        entries.stream()
                .filter(predicate)
                .forEach(entry -> entriesById.remove(entry.getId()));
        entries.removeIf(predicate);
    }

    protected void acquireLock() {
        internalAutoSyncLock = true;
    }

    protected void releaseLock() {
        internalAutoSyncLock = false;
    }

    protected DatabaseIdGenerator getIdGenerator() {
        return idGenerator;
    }

    protected List<? extends DatabaseEntry> getEntries() {
        return entries;
    }

    public SimpleDatabase autoSync() {
        autoSync = true;
        read();
        return this;
    }

    @Override
    public DatabaseEntry generateNewEntry() {
        DatabaseEntry entry = new DatabaseEntry(table, idGenerator, this::onChange);
        addEntry(entry);
        if(autoSync && internalAutoSyncLock) {
            write();
        }
        return entry;
    }

    @Override
    public Optional<DatabaseEntry> getEntryById(DatabaseId id) {
        return Optional.ofNullable(entriesById.getOrDefault(id, null));
    }

    @Override
    public void removeEntry(DatabaseId id) {
        entries.removeIf(entry -> entry.getId().equals(id));
        entriesById.remove(id);
        if(autoSync && internalAutoSyncLock) write();
    }

    @Override
    public void write() {
        try {
            // First create a new output stream
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Output the numbers of entries
            outputStream.write(ByteBuffer.allocate(4).putInt(entries.size()).array());

            // Output each entries with there respected size
            for(DatabaseEntry entry : entries) {

                // Compile the data of the entry
                ByteBuffer buffer = DatabaseEntry.serialize(entry);

                // Write the ByteBuffer data
                outputStream.write(ByteBuffer.allocate(4).putInt(buffer.array().length).array());

                // Write the buffer to the stream
                outputStream.write(buffer.array());
            }

            // Compute security hash and add it to the end of the file
            ByteBuffer rawData = ByteBuffer.wrap(outputStream.toByteArray());
            ByteBuffer checkSum = CheckSum.fromByteBuffer(rawData).rawData();
            ByteBuffer data = ByteBuffer.allocate(rawData.array().length + checkSum.array().length + 8);

            // Write the data
            data.putInt(checkSum.array().length);
            data.putInt(rawData.array().length);
            data.put(checkSum.array());
            data.put(rawData.array());

            // Write the database
            serializedIO.write(data);
            outputStream.close();
        }
        catch (IOException e) {
            System.out.println("ERROR: Cannot write the database for unknown reasons");
        }
    }

    @Override
    public void read() {
        Optional<ByteBuffer> read = serializedIO.read();
        if(read.isEmpty()) return;

        ByteBuffer data = read.get();

        // Read the data & checksum size
        int checkSumSize = data.getInt();
        int rawDataSize = data.getInt();
        if(checkSumSize + rawDataSize + 8 != data.position() + data.remaining()) {
            throw new IllegalArgumentException("The given database file was corrupted and cannot be read from");
        }

        // Read both data
        CheckSum checksum = CheckSum.fromRawCheckSum(ByteBufferUtils.extractSubBuffer(data, checkSumSize));
        ByteBuffer rawData = ByteBufferUtils.extractSubBuffer(data, rawDataSize);

        // Detection of corruption error of the file
        if(!checksum.equals(CheckSum.fromByteBuffer(rawData))) {
            System.out.println("ERROR: The given database file was corrupted and cannot be read from");
            return;
        }

        // Clear the current state of the database
        entries.clear();
        entriesById.clear();

        // Otherwise continue the process and parse the data
        int entriesCount = rawData.getInt();
        for (int i = 0; i < entriesCount; i++) {
            // Extract the required data
            int entrySize = rawData.getInt();
            ByteBuffer entriesRawData = ByteBufferUtils.extractSubBuffer(rawData, entrySize);

            // Parse the given data
            addEntry(DatabaseEntry.deserialize(entriesRawData, table, idGenerator, this::onChange));

        }
    }

    @Override
    public Set<DatabaseEntry> query(DatabaseQuery query) {
        return entries.stream()
                .filter(entry -> query.match(getIndex(entry)))
                .collect(Collectors.toSet());
    }

    private Map<String,String> getIndex(DatabaseEntry entry) {
        return table.getIndexedArguments().stream()
                .collect(Collectors.toMap(arg -> arg, arg -> entry.get(arg).orElseThrow()));
    }

    private void onChange(DatabaseId id) {
        if(autoSync && internalAutoSyncLock) write();
    }

    private void addEntry(DatabaseEntry entry) {
        entries.add(entry);
        entriesById.put(entry.getId(), entry);
    }
}
